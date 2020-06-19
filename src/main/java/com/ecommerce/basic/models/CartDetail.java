package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class CartDetail {

	@Id
	@GeneratedValue
	private long cartDetailId;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cartId")
	private CartItem cartItem;

	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	@JsonIgnoreProperties
	private Product product;

	private int quantity;
	private long productTotal;
}
