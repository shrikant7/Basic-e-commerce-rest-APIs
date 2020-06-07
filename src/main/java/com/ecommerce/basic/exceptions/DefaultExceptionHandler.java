package com.ecommerce.basic.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

/**
 * @author Shrikant Sharma
 */

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

	private ResponseEntity<Object> getResponseEntity(String message, HttpStatus status) {
		ApiException apiException = new ApiException(message,
														status,
														LocalDateTime.now());

		return new ResponseEntity<>(apiException, status);
	}

	@ExceptionHandler(value = {FileStorageException.class})
	public ResponseEntity<?> handleServerException(RuntimeException e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return getResponseEntity(e.getMessage(), status);
	}

	@ExceptionHandler(value = {InvalidFileExtension.class, InvalidResourceName.class})
	public ResponseEntity<?> handleBadRequestException(RuntimeException e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return getResponseEntity(e.getMessage(), status);
	}

	@ExceptionHandler(value = {NoSuchResourceException.class})
	public ResponseEntity<?> handleNotFoundException(RuntimeException e) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		return getResponseEntity(e.getMessage(), status);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return getResponseEntity(ex.getMessage(), status);
	}
}
