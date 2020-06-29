package com.ecommerce.basic.exceptions;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.*;

/**
 * @author Shrikant Sharma
 */

@ControllerAdvice
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

	private ResponseEntity<Object> getResponseEntity(String message, HttpStatus status, Integer errorCode) {
		ApiException apiException = new ApiException(errorCode,
														status,
														message,
														LocalDateTime.now());

		return new ResponseEntity<>(apiException, status);
	}

	@ExceptionHandler(value = {FileStorageException.class})
	public ResponseEntity<?> handleServerException(BaseException e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return getResponseEntity(e.getMessage(), status, e.getErrorCode().ordinal());
	}

	@ExceptionHandler(value = {InvalidFileExtension.class, InvalidResourceName.class, UniqueKeyViolationException.class})
	public ResponseEntity<?> handleBadRequestException(BaseException e) {
		HttpStatus status = HttpStatus.BAD_REQUEST;
		return getResponseEntity(e.getMessage(), status, e.getErrorCode().ordinal());
	}

	@ExceptionHandler(value = {NoSuchResourceException.class, EmptyResultDataAccessException.class})
	public ResponseEntity<?> handleNotFoundException(RuntimeException e) {
		HttpStatus status = HttpStatus.NOT_FOUND;
		if(e instanceof BaseException) {
			return getResponseEntity(e.getMessage(), status, ((BaseException) e).getErrorCode().ordinal());
		}
		return getResponseEntity(e.getMessage(), status, EMPTY_RESULT_SET_EXCEPTION.ordinal());
	}

	//for other spring exceptions
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		return getResponseEntity(ex.getMessage(), status, OTHER_SPRING_EXCEPTION.ordinal());
	}

	//handling all other exceptions
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<?> handleAllException(RuntimeException e) {
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		return getResponseEntity(e.getMessage(), status, EXCEPTION.ordinal());
	}
}
