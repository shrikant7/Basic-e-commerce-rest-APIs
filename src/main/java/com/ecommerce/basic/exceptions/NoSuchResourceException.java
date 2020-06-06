package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class NoSuchResourceException extends RuntimeException {
	private Class clazz;

	public NoSuchResourceException(Class clazz, String message) {
		super(message);
		this.clazz = clazz;
	}

	public Class getClazz() {
		return clazz;
	}
}
