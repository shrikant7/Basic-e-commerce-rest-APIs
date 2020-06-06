package com.ecommerce.basic.exceptions;

import java.io.IOException;

/**
 * @author Shrikant Sharma
 */
public class FileStorageException extends RuntimeException {

	public FileStorageException(String message, Exception cause) {
		super(message, cause);
	}
}
