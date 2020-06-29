package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class NoSuchResourceException extends BaseException {

	public NoSuchResourceException(ErrorConstant.ErrorCode errorCode, String message) {
		super(errorCode, message);
	}
}
