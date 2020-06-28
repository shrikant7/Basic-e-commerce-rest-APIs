package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class Product {
	@Id
	@GeneratedValue
	private Long productId;

	@NotBlank(message = "Product name can't be blank")
	private String name;

	@NotBlank(message = "Product description can't be blank")
	@Size(max = 1000, message = "Description length can't be greater than 1000")
	@Column(length = 1000)
	private String description;

	@NotNull(message = "MRP can't be null")
	@Positive(message = "MRP should be positive")
	private Integer mrpPrice;

	@NotNull(message = "YouPrice can't be null")
	@Positive(message = "YouPrice should be positive")
	private Integer yourPrice;

	private String imageUri;

	@JsonIgnore
	private boolean deleted;

	/* Ignoring category field in json response of getting all products of a category;
	   creates repetition of category object in all product objects.*/
	@JsonIgnore
	@EqualsAndHashCode.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId")
	private Category category;

	public String shortenToString() {
		return "Product{" +
				"productId=" + productId +
				", name='" + name +
				", mrpPrice=" + mrpPrice +
				", yourPrice=" + yourPrice +
				'}';
	}
}
