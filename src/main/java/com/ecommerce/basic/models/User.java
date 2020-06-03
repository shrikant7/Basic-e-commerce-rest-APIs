package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity @Table(name = "User")
@Data @Accessors(chain = true)
public class User implements Serializable {
	@Id
	@GeneratedValue
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
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private UserInfo userInfo;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(mappedBy = "user")
	private List<OrderItem> orderItems;
}
