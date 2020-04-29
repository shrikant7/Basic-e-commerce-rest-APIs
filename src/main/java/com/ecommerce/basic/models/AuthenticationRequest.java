package com.ecommerce.basic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Shrikant Sharma
 */
@Data @AllArgsConstructor @Accessors(chain = true)
public class AuthenticationRequest {
	private String username;
	private String password;
}
