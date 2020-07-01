package com.ecommerce.basic.exceptions;

public class InvalidCredentialException extends BaseException {

    public InvalidCredentialException(ErrorConstant.ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
