package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;

/**
 * @author Shrikant Sharma
 */

@Entity @Table(name = "User")
@Data @Accessors(chain = true)
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	private String username;
	private String password;
	private boolean active;
	private String roles;
}
