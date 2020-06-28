package com.ecommerce.basic.models;

import lombok.Data;

/**
 * @author Shrikant Sharma
 */
@Data
public class AuthenticationResponse {
	private final String jwt;
	private final UserWithInfoDto user;
}
