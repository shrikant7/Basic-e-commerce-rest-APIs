package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int productId;
	private String name;
	@Column(length = 1000)
	private String description;
	private Integer mrpPrice;
	private Integer yourPrice;
	private String imageUri;

	/* Ignoring category field in json response of getting all products of a category;
	   creates repetition of category object in all product objects.*/
	@ManyToOne
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
