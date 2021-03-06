package com.ecommerce.basic.exceptions;

import lombok.Value;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * @author Shrikant Sharma
 */

@Value
public class ApiException {
	Integer errorCode;
	HttpStatus status;
	String message;
	LocalDateTime timestamp;
}
