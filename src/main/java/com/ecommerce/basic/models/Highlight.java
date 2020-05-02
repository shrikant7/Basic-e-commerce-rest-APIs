package com.ecommerce.basic.models;

import lombok.Data;
import lombok.Value;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author Shrikant Sharma
 */

@Entity
@Data @Accessors(chain = true)
public class Highlight {
	@Id
	@GeneratedValue
	private int highlightId;
	@OneToOne
	@JoinColumn(name = "highlightedProduct", referencedColumnName = "productId")
	private Product highlightedProduct;
}
