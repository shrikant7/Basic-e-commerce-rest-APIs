package com.ecommerce.basic.exceptions;

/**
 * @author Shrikant Sharma
 */
public class InvalidResourceName extends BaseException {

	public InvalidResourceName(ErrorConstant.ErrorCode errorCode, String msg) {
		super(errorCode,msg);
	}
}
