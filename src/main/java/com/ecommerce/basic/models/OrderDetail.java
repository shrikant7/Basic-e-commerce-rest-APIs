package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

/**
 * @author Shrikant Sharma
 */

@Entity
@Data @Accessors(chain = true)
public class OrderDetail {
	@Id
	@GeneratedValue
	private long orderDetailId;

	@ToString.Exclude
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "orderId")
	private OrderItem orderItem;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Product product;

	private int boughtPrice;
	private int quantity;
	private long productTotal;

	public String shortenToString() {
		return "OrderDetail{" +
				"product=" + product.shortenToString() +
				", boughtPrice=" + boughtPrice +
				", quantity=" + quantity +
				", productTotal=" + productTotal +
				'}';
	}
}
