package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class FileStorageException extends BaseException {

	public FileStorageException(ErrorConstant.ErrorCode errorCode, String message, Exception cause) {
		super(errorCode, message, cause);
	}
}
