package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity @Table(name = "User")
@Data @Accessors(chain = true)
public class User {
	public static final String ROLE_ADMIN = "ROLE_ADMIN";
	public static final String ROLE_USER = "ROLE_USER";

	@Id
	@GeneratedValue
	private int id;

	@NotBlank(message = "Username can't be blank")
	@Column(unique = true)
	private String username;

	@JsonIgnore
	@ToString.Exclude
	@NotBlank(message = "Password can't be blank")
	@Size(message = "Password's length can't be less than 4")
	private String password;

	private boolean active;
	private String roles;

	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@NotNull(message = "UserInfo is mandatory field")
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "user", cascade = CascadeType.ALL)
	private UserInfo userInfo;

	@JsonIgnore
	@ToString.Exclude
	@OneToMany(mappedBy = "user")
	private List<OrderItem> orderItems;
}
