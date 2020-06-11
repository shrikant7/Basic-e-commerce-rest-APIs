package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.OrderRepository;
import com.ecommerce.basic.resources.HomeResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @author Shrikant Sharma
 */

@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private UserService userService;
	@Autowired
	private ProductService productService;

	public OrderItem createOrder(String userName, List<OrderRequest> orderRequest) {
		User user = userService.findByUsername(userName);
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
	public List<OrderItem> getOrderHistory(String userName, int offset, int limit) {
		User user = userService.findByUsername(userName);
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

	public OrderItem getOrderItem(String userName, int orderId) {
		Optional<OrderItem> optionalOrderItem = orderRepository.findById(orderId);
		optionalOrderItem.orElseThrow(() -> new NoSuchResourceException(OrderService.class,"No order found for orderId: "+orderId));

		OrderItem orderItem = optionalOrderItem.get();
		if(!userName.equalsIgnoreCase(orderItem.getUser().getUsername())) {
			throw new NoSuchResourceException(HomeResource.class, "OrderItem does not belongs to User: "+userName);
		}
		return orderItem;
	}

	public OrderItem reOrderItem(String userName, int itemId) {
		User user = userService.findByUsername(userName);
		OrderItem reOrderItem = getOrderItem(userName, itemId);
		OrderItem orderItem = new OrderItem();
		List<OrderDetail> orderDetails = new ArrayList<>();
		long totalValue = 0;
		for(OrderDetail detail : reOrderItem.getOrderDetails()) {
			//TODO:: is detail.getProduct() still exist in our domain
			OrderDetail orderDetail = createOrderDetail(orderItem, detail.getProduct(), detail.getQuantity());
			orderDetails.add(orderDetail);
			totalValue += orderDetail.getProductTotal();
		}

		orderItem.setUser(user)
				.setTotalValue(totalValue)
				.setOrderDetails(orderDetails)
				.setPlacedOn(LocalDateTime.now());

		return orderRepository.saveAndFlush(orderItem);
	}
}
