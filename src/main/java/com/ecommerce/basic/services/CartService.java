package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.CartDetailRepository;
import com.ecommerce.basic.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public CartItem getCartItem(String userName) {
		User user = userService.findByUsername(userName);
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

	public CartItem removeFromCart(String userName, long cartDetailId) {
		CartItem cartItem = getCartItem(userName);
		CartDetail removedCartDetail = null;
		for(CartDetail cartDetail : cartItem.getCartDetails()) {
			if(cartDetail.getCartDetailId() == cartDetailId) {
				removedCartDetail = cartDetail;
				cartItem.getCartDetails().remove(cartDetail);
				break;
			}
		}
		if(removedCartDetail == null) {
			throw new NoSuchResourceException(CartService.class, "No cartDetailId:"+cartDetailId+" found for username: "+userName);
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
		optionalCartDetail.orElseThrow(()->new NoSuchResourceException(CartService.class, "No cartDetail found for cartDetailId: "+cartDetailId));
		return optionalCartDetail.get();
	}

	public CartItem deleteCart(String userName) {
		CartItem cartItem = getCartItem(userName);
		cartRepository.delete(cartItem);
		return cartItem;
	}

	public CartItem addToCart(String userName, CartDetailRequest detailRequest) {
		CartItem cartItem = getCartItem(userName);
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

	public CartItem addToCartBatch(String userName, List<OrderRequest> orderRequests) {
		return null;
	}


	public void deleteProductFromAnyCartDetail(Product product) {
		int deletedRow = cartDetailRepository.deleteByProductQuery(product);
		//System.err.println("deletedRow: "+deletedRow);
	}
}
