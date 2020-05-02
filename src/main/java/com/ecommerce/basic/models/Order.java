package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.sql.Date;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
public class Order {
	@Id
	@GeneratedValue
	int orderId;
	User user;
	Date placedOn;
	Long totalValue;
}
