package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int categoryId;
	private String categoryName;

	@ToString.Exclude
	@OneToMany(mappedBy = "category")
	private List<Product> products;
}
