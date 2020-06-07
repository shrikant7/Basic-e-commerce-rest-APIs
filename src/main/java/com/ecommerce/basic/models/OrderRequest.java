package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Positive;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
public class OrderRequest {
	private int productId;
	@Positive(message = "Quantity should be positive")
	private int quantity;
}
