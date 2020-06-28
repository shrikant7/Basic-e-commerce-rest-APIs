package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class CartDetail {

	@Id
	@GeneratedValue
	private Long cartDetailId;

	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cartId", nullable = false)
	private CartItem cartItem;

	@OneToOne
	@JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Product product;

	private int quantity;
	private long productTotal;
}
