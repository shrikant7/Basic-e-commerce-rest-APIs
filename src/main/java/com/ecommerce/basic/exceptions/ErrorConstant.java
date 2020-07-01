package com.ecommerce.basic.exceptions;

import javax.validation.Payload;

public class ErrorConstant {
    //ErrorCode start from 0
    public enum ErrorCode implements Payload {
        FILE_STORAGE_EXCEPTION("Could not store image, server error"),
        INVALID_IMAGE_EXTENSION("INVALID name/extension of Image"),

        BAD_CREDENTIAL_EXCEPTION("Incorrect username or password"),
        INVALID_USER_ROLE("User's role is not valid"),
        BEAN_VALIDATION_EXCEPTION("Bean validation failed"),
        OTP_VERIFICATION_FAILED("OTP verification failed"),
        OTP_EXPIRED_EXCEPTION("OTP expired"),
        INVALID_YOUR_PRICE("YourPrice cannot be greater than MrpPrice"),
        UNIQUE_HIGHLIGHT_EXCEPTION("Product already exist in highlight"),
        UNIQUE_CATEGORY_EXCEPTION("CategoryName already exist"),
        UNIQUE_USERNAME_EXCEPTION("Username already exist"),

        NO_USER_FOUND_EXCEPTION("Username not found in system"),
        NO_CATEGORY_ID_EXCEPTION("CategoryId not found in system"),
        DELETED_CATEGORY_ID_EXCEPTION("CategoryId is marked deleted"),
        NO_CATEGORY_NAME_EXCEPTION("CategoryName not found in system"),
        DELETED_CATEGORY_NAME_EXCEPTION("CategoryName is marked deleted"),
        NO_HIGHLIGHT_ID_EXCEPTION("HighlightId not found in system"),
        NO_IMAGE_IN_CATEGORY_EXCEPTION("Image does not belongs to category"),
        NO_PRODUCT_ID_EXCEPTION("Product not found in system"),
        DELETED_PRODUCT_EXCEPTION("Product is marked deleted"),
        NO_PRODUCT_IN_CATEGORY_EXCEPTION("Product does not belongs to category"),
        NO_ORDER_EXCEPTION("Order not found in system"),
        NO_ORDER_IN_USER_EXCEPTION("Order does not belongs to user"),
        NO_CART_DETAIL_EXCEPTION("CartDetail not found in system"),
        NO_CART_DETAIL_IN_USER_EXCEPTION("CartDetail does not belongs to user"),
        EMPTY_RESULT_SET_EXCEPTION("Empty result set from table"),

        OTHER_SPRING_EXCEPTION("Other spring exception"),
        EXCEPTION("Exception");

        private final String hint;
        ErrorCode(String hint){
            this.hint = hint;
        }
        public String getHint(){
            return hint;
        }
        };
}
