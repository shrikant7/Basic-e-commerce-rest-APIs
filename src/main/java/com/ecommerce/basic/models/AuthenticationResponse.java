package com.ecommerce.basic.models;

/**
 * @author Shrikant Sharma
 */
public class AuthenticationResponse {
	private final String jwt;

	public AuthenticationResponse(String jwt) {
		this.jwt = jwt;
	}

	public String getJwt() {
		return jwt;
	}
}
