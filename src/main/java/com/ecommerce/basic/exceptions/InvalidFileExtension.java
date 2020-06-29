package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class InvalidFileExtension extends BaseException {

	public InvalidFileExtension(ErrorConstant.ErrorCode errorCode, String msg) {
		super(errorCode, msg);
	}
}
