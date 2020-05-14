package com.ecommerce.basic.models;

import lombok.Value;
import java.time.LocalDateTime;

/**
 * @author Shrikant Sharma
 */

@Value
public class OrderItemDto {
	int orderId;
	LocalDateTime placedOn;
	Long totalValue;
}
