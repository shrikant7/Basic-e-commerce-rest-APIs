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
	private String description;
	private Integer price;
	private String imageURI;

	/* Ignoring category field in json response of getting all products of a category;
	   creates repetition of category object in all product objects.*/
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "categoryId")
	private Category category;
}
