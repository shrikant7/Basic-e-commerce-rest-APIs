package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

/**
 * @author Shrikant Sharma
 */

@Entity
@Table
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

	public Product() {
	}

	public int getProductId() {
		return productId;
	}

	public Product setProductId(int productId) {
		this.productId = productId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Product setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Product setDescription(String description) {
		this.description = description;
		return this;
	}

	public Integer getPrice() {
		return price;
	}

	public Product setPrice(Integer price) {
		this.price = price;
		return this;
	}

	public String getImageURI() {
		return imageURI;
	}

	public Product setImageURI(String imageURI) {
		this.imageURI = imageURI;
		return this;
	}

	public Category getCategory() {
		return category;
	}

	public Product setCategory(Category category) {
		this.category = category;
		return this;
	}
}
