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
		return refreshCartItem(user);
	}

	private CartItem refreshCartItem(User user) {
		CartItem cartItem = user.getCartItem();
		if(cartItem == null) {
			return new CartItem()
					.setUser(user);
		}
		long totalValue = 0;
		boolean detailsChanged = false;
		//TODO:: what if product is not there
		for(CartDetail cartDetail : cartItem.getCartDetails()) {
			int yourPrice = cartDetail.getProduct().getYourPrice();
			int quantity = cartDetail.getQuantity();
			long productTotal = yourPrice * quantity;
			totalValue += productTotal;
			if(cartDetail.getProductTotal() != productTotal) {
				cartDetail.setProductTotal(productTotal);
				detailsChanged = true;
			}
		}
		cartItem.setTotalValue(totalValue);
		if(detailsChanged) {
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
		System.out.println(cartItem +" user:"+cartItem.getUser());
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
}
