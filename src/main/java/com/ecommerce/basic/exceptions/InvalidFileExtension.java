package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class InvalidFileExtension extends RuntimeException {

	public InvalidFileExtension(String msg) {
		super(msg);
	}
}
