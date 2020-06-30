package com.ecommerce.basic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author Shrikant Sharma
 */
@Data @AllArgsConstructor @NoArgsConstructor @Accessors(chain = true)
public class AuthenticationRequest {
	@NotBlank(message = "Username can't be blank")
	private String username;
	@NotBlank(message = "Password can't be blank")
	private String password;
}
