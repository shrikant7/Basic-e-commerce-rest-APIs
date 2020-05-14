package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.OrderRepository;
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
			OrderDetail orderDetail = new OrderDetail()
										.setOrderItem(orderItem)
										.setProduct(product)
										.setBoughtPrice(product.getYourPrice())
										.setQuantity(orderDetailRequest.getQuantity())
										.setProductTotal(product.getYourPrice() * orderDetailRequest.getQuantity());
			orderDetails.add(orderDetail);
			totalValue += orderDetail.getProductTotal();
		}

		orderItem.setUser(user)
				.setTotalValue(totalValue)
				.setOrderDetails(orderDetails)
				.setPlacedOn(LocalDateTime.now());

		return orderRepository.saveAndFlush(orderItem);
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
		optionalOrderItem.orElseThrow(() -> new NoSuchResourceException(OrderService.class,"No product found for orderId: "+orderId));
		return optionalOrderItem.get();
	}
}
