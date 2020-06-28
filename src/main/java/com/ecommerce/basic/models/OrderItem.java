package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
	private Long orderId;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "userId")
	private User user;

	private LocalDateTime placedOn;
	private Long totalValue;

	@OneToMany(mappedBy = "orderItem", cascade = CascadeType.ALL)
	private List<OrderDetail> orderDetails;

	public String shortenToString() {
		return "OrderItem{" +
				"orderId=" + orderId +
				", placedOn=" + placedOn +
				", totalValue=" + totalValue +
				'}';
	}
}
