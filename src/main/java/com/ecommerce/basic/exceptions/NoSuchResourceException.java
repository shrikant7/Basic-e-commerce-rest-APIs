package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class NoSuchResourceException extends RuntimeException {

	private NoSuchResourceException(String message) {
		super(message);
	}

	public NoSuchResourceException(Class clazz, String message) {
		this(clazz+": "+message);
	}

}
