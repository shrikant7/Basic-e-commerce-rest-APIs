package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class InvalidResourceName extends RuntimeException {
	private Class clazz;

	public InvalidResourceName(Class clazz, String msg) {
		super(msg);
		this.clazz = clazz;
	}

	public Class getClazz() {
		return clazz;
	}
}
