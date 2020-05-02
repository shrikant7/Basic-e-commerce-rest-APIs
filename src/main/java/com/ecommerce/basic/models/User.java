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

@Entity @Table(name = "User")
@Data @Accessors(chain = true)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Column(unique = true)
	private String username;

	@JsonIgnore
	@ToString.Exclude
	private String password;

	private boolean active;
	private String roles;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(mappedBy = "user")
	private List<OrderItem> orderItems;
}
