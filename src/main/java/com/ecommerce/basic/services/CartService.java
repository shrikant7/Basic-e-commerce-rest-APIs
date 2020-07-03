package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.CartDetailRepository;
import com.ecommerce.basic.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.NO_CART_DETAIL_EXCEPTION;
import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.NO_CART_DETAIL_IN_USER_EXCEPTION;

/**
 * @author Shrikant Sharma
 */

@Service
public class CartService {
	@Autowired
	private CartRepository cartRepository;
	@Autowired
	private CartDetailRepository cartDetailRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;

	public CartItem getCartItem(User user) {
		CartItem cartItem = cartRepository.findByUser(user);
		if(cartItem == null) {
			return new CartItem()
					//setting user as used by addToCart and user is required to create new detail
					.setUser(user);
		}
		return refreshCartItem(cartItem);
	}

	private CartItem refreshCartItem(CartItem cartItem) {
		long totalValue = 0;
		boolean detailsChanged = false;
		List<CartDetail> expiredDetails = new ArrayList<>();
		for(CartDetail cartDetail : cartItem.getCartDetails()) {
			//check if product is marked deleted or not
			if(cartDetail.getProduct().isDeleted()) {
				expiredDetails.add(cartDetail);
				continue;
			}

			int yourPrice = cartDetail.getProduct().getYourPrice();
			int quantity = cartDetail.getQuantity();
			long productTotal = yourPrice * quantity;
			totalValue += productTotal;
			//update cartDetail's total value if any product's price changes
			if(cartDetail.getProductTotal() != productTotal) {
				cartDetail.setProductTotal(productTotal);
				detailsChanged = true;
			}
		}

		//remove all expiredDetail from database as well as itemDetails
		if(expiredDetails.size() > 0) {
			cartItem.getCartDetails().removeAll(expiredDetails);
			cartDetailRepository.deleteAll(expiredDetails);
		}

		// if any cartDetail changed or product is not exist now, update cartItem's total value
		if(detailsChanged || totalValue != cartItem.getTotalValue()) {
			cartItem.setTotalValue(totalValue);
			cartRepository.saveAndFlush(cartItem);
		}
		return cartItem;
	}

	public CartItem removeFromCart(User user, long cartDetailId) {
		CartItem cartItem = getCartItem(user);
		CartDetail removedCartDetail = null;
		for(CartDetail cartDetail : cartItem.getCartDetails()) {
			if(cartDetail.getCartDetailId() == cartDetailId) {
				removedCartDetail = cartDetail;
				cartItem.getCartDetails().remove(cartDetail);
				break;
			}
		}
		if(removedCartDetail == null) {
			throw new NoSuchResourceException(NO_CART_DETAIL_IN_USER_EXCEPTION, "No cartDetailId:"+cartDetailId+" found for username: "+user.getUsername());
		}
		cartItem.setTotalValue(cartItem.getTotalValue() - removedCartDetail.getProductTotal())
				.setLastModified(LocalDateTime.now());
		cartDetailRepository.deleteById(cartDetailId);
		//updation of cartItem is flushed after delete query automatically.
		//cartRepository.saveAndFlush(cartItem);
		return cartItem;
	}

	private CartDetail getCartDetail(long cartDetailId) {
		Optional<CartDetail> optionalCartDetail = cartDetailRepository.findById(cartDetailId);
		optionalCartDetail.orElseThrow(()->new NoSuchResourceException(NO_CART_DETAIL_EXCEPTION, "No cartDetail found for cartDetailId: "+cartDetailId));
		return optionalCartDetail.get();
	}

	public CartItem deleteCart(User user) {
		CartItem cartItem = getCartItem(user);
		cartRepository.delete(cartItem);
		return cartItem;
	}

	public CartItem addToCart(User user, CartDetailRequest detailRequest) {
		CartItem cartItem = getCartItem(user);
		List<CartDetail> cartDetails = cartItem.getCartDetails();
		if(cartDetails == null){
			cartDetails = new ArrayList<>();
		}

		long totalValue = cartItem.getTotalValue();
		CartDetail cartDetail = null;
		for(CartDetail detail : cartDetails) {
			if(detail.getProduct().getProductId() == detailRequest.getProductId()) {
				cartDetail = detail;
				totalValue -= detail.getProductTotal();
				break;
			}
		}
		if(cartDetail == null) {
			Product product = productService.getProductById(detailRequest.getProductId());
			cartDetail = new CartDetail()
					.setCartItem(cartItem)
					.setProduct(product);
			cartDetails.add(cartDetail);
		}
		cartDetail.setQuantity(detailRequest.getQuantity())
					.setProductTotal(cartDetail.getProduct().getYourPrice() * detailRequest.getQuantity());
		totalValue += cartDetail.getProductTotal();

		cartItem.setTotalValue(totalValue)
				.setCartDetails(cartDetails)
				.setLastModified(LocalDateTime.now());

		cartRepository.saveAndFlush(cartItem);
		return cartItem;
	}

	public CartItem addToCartBatch(User user, List<CartDetailRequest> cartDetailRequests) {
		CartItem cartItem = getCartItem(user);
		List<CartDetail> cartDetails = cartItem.getCartDetails();
		if(cartDetails == null){
			cartDetails = new ArrayList<>();
		}
		Map<Long, Integer> detailMap = cartDetailRequests.stream()
				.collect(Collectors.toMap(CartDetailRequest::getProductId, CartDetailRequest::getQuantity, (o, n) -> n));

		//merge common products in cart and detailRequest
		cartDetails.forEach(cd->{
			Long productId = cd.getProduct().getProductId();
			if(detailMap.containsKey(productId)) {
				cd.setQuantity(cd.getQuantity()+detailMap.get(productId));
				detailMap.remove(productId);
			}
		});

		//add all remaining detailRequests in cart
		List<CartDetail> finalCartDetails = cartDetails;
		detailMap.forEach((key, value) -> {
			Product product = productService.getProductById(key);
			CartDetail cartDetail = new CartDetail()
					.setCartItem(cartItem)
					.setProduct(product)
					.setQuantity(value);
			finalCartDetails.add(cartDetail);
		});

		//as content of cart is modified by user, set lastModified field
		cartItem.setCartDetails(finalCartDetails).setLastModified(LocalDateTime.now());
		//refreshing cartItem will set ans save to db about cartDetail price and total value of item
		return refreshCartItem(cartItem);
	}


	public void deleteProductsFromAnyCartDetail(List<Product> product) {
		int deletedRow = cartDetailRepository.deleteCartDetailsHaveProducts(product);
		//System.err.println("deletedCartDetails: "+deletedRow);
	}
}
