package com.ecommerce.basic.services;

import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
}
