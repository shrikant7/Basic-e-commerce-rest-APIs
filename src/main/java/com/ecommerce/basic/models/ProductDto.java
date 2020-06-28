package com.ecommerce.basic.models;

import lombok.Value;

/**
 * @author Shrikant Sharma
 */

@Value
public class ProductDto {
	Long productId;
	String name;
	Integer mrpPrice;
	String imageUri;
	boolean isDeleted;
}
