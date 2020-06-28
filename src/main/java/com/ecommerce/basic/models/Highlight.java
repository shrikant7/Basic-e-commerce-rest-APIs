package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

import static javax.persistence.ConstraintMode.NO_CONSTRAINT;

/**
 * @author Shrikant Sharma
 */

@Entity
@Data @Accessors(chain = true)
public class Highlight {
	@Id
	@GeneratedValue
	private Long highlightId;
	@OneToOne
	@JoinColumn(name = "highlightedProduct", referencedColumnName = "productId",unique = true, nullable = false, foreignKey = @ForeignKey(NO_CONSTRAINT))
	private Product highlightedProduct;
}
