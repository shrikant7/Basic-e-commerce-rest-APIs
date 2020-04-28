package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class NotFound extends RuntimeException {

	private NotFound(String message) {
		super(message);
	}

	public NotFound(Class clazz, String message) {
		this(clazz+": "+message);
	}

}
