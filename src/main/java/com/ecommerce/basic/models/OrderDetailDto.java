package com.ecommerce.basic.models;

import lombok.Value;
import lombok.experimental.Accessors;

/**
 * @author Shrikant Sharma
 */

@Value
public class OrderDetailDto {
	ProductDto productDto;
	Integer boughtPrice;
	Integer quantity;
	Long productTotal;
}
