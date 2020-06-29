package com.ecommerce.basic.exceptions;

public class UniqueKeyViolationException extends BaseException {
    public UniqueKeyViolationException(ErrorConstant.ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
