package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
public class OrderRequest {
	private int productId;
	private int quantity;
}
