package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity
@Data @Accessors(chain = true)
public class OrderItem {
	@Id
	@GeneratedValue
	private int orderId;

	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	private LocalDateTime placedOn;
	private Long totalValue;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
	private List<OrderDetail> orderDetails;
}
