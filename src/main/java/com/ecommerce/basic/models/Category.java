package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class Category {
	@Id
	@GeneratedValue
	private Long categoryId;

	@NotBlank(message = "CategoryName can't be blank")
	@Column(unique = true)
	private String categoryName;

	@JsonIgnore
	private boolean deleted;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	@Where(clause = "deleted=false")                     //filtering out products which are marked deleted
	private List<Product> products;
}
