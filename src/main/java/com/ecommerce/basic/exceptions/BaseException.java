package com.ecommerce.basic.exceptions;

import com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode;

public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;

    public BaseException(ErrorCode errorCode, String message, Exception cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BaseException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
