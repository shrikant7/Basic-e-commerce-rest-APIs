package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity
@Table
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int categoryId;
	private String categoryName;

	@JsonIgnore
	@OneToMany(mappedBy = "category")
	private List<Product> products;

	public Category() {
	}

	public int getCategoryId() {
		return categoryId;
	}

	public Category setCategoryId(int categoryId) {
		this.categoryId = categoryId;
		return this;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public Category setCategoryName(String categoryName) {
		this.categoryName = categoryName;
		return this;
	}

	public List<Product> getProducts() {
		return products;
	}

	public Category setProducts(List<Product> products) {
		this.products = products;
		return this;
	}
}
