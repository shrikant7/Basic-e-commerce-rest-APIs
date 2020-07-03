package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.OrderDetailRepository;
import com.ecommerce.basic.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.NO_ORDER_EXCEPTION;
import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.NO_ORDER_IN_USER_EXCEPTION;

/**
 * @author Shrikant Sharma
 */

@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;
	@Autowired
	private CartService cartService;

	public OrderItem createOrder(User user, List<OrderRequest> orderRequest) {
		OrderItem orderItem = new OrderItem();
		List<OrderDetail> orderDetails = new ArrayList<>();
		long totalValue = 0;
		for(OrderRequest orderDetailRequest : orderRequest) {
			Product product = productService.getProductById(orderDetailRequest.getProductId());
			OrderDetail orderDetail = createOrderDetail(orderItem, product, orderDetailRequest.getQuantity());
			orderDetails.add(orderDetail);
			totalValue += orderDetail.getProductTotal();
		}

		orderItem.setUser(user)
				.setTotalValue(totalValue)
				.setOrderDetails(orderDetails)
				.setPlacedOn(LocalDateTime.now());

		return orderRepository.saveAndFlush(orderItem);
	}

	private OrderDetail createOrderDetail(OrderItem orderItem, Product product, int quantity) {
		return new OrderDetail()
					.setOrderItem(orderItem)
					.setProduct(product)
					.setBoughtPrice(product.getYourPrice())
					.setQuantity(quantity)
					.setProductTotal(product.getYourPrice() * quantity);
	}

	//provides orderItems with details
	public List<OrderItem> getOrderHistory(User user, int offset, int limit) {
		List<OrderItem> orderItems = user.getOrderItems();
		orderItems.sort(Comparator.comparing(OrderItem::getPlacedOn).reversed());
		int size = orderItems.size();

		if(offset >= 0 && offset < size && limit >= 0) {
			//if offset and limit is zero then default condition and return all orderItems
			if(offset == 0 && limit == 0){
				return orderItems;
			}
			//return sublist from offset to limit/size length;
			return orderItems.subList(offset, Math.min(offset+limit,size));
		}

		//otherwise return empty list;
		return Collections.emptyList();
	}

	public OrderItem getOrderItem(User user, long orderId) {
		Optional<OrderItem> optionalOrderItem = orderRepository.findById(orderId);
		optionalOrderItem.orElseThrow(() -> new NoSuchResourceException(NO_ORDER_EXCEPTION,"No order found for orderId: "+orderId));

		OrderItem orderItem = optionalOrderItem.get();
		if(!user.getUsername().equalsIgnoreCase(orderItem.getUser().getUsername())) {
			throw new NoSuchResourceException(NO_ORDER_IN_USER_EXCEPTION, "OrderItem does not belongs to User: "+user.getUsername());
		}
		return orderItem;
	}

	public CartItem reOrderItem(User user, long itemId) {
		OrderItem reOrderItem = getOrderItem(user, itemId);
		List<CartDetailRequest> cartDetailRequests = reOrderItem.getOrderDetails()
				.stream()
				.filter(d -> !d.getProduct().isDeleted())
				.map(d -> new CartDetailRequest(d.getProduct().getProductId(), d.getQuantity()))
				.collect(Collectors.toList());
		return cartService.addToCartBatch(user,cartDetailRequests);
	}

	public List<Product> filterProductsNotPresentInOrderDetail(List<Product> deletedMarkedProducts) {
		List<OrderDetail> orderDetailsWithDeletedProduct = orderDetailRepository.findAllByProductIn(deletedMarkedProducts);
		List<Product> deletedProductsPresentInOrderDetails = orderDetailsWithDeletedProduct.stream()
																		.map(OrderDetail::getProduct)
																		.collect(Collectors.toList());
		deletedMarkedProducts.removeAll(deletedProductsPresentInOrderDetails);
		return deletedMarkedProducts;
	}
}
