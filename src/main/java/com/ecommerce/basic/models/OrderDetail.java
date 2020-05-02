package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author Shrikant Sharma
 */

@Entity
@Data @Accessors(chain = true)
public class OrderDetail {
	@Id
	@GeneratedValue
	private long orderDetailId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "orderId")
	private OrderItem orderItem;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;

	private int boughtPrice;
	private int quantity;
	private long productTotal;
}
