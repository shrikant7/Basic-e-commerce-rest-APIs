package com.ecommerce.basic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Positive;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CartDetailRequest {
	private long productId;
	@Positive(message = "Quantity should be positive")
	private int quantity;
}
