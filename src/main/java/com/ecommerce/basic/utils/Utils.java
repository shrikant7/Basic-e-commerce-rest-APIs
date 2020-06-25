package com.ecommerce.basic.utils;

import com.ecommerce.basic.exceptions.InvalidResourceName;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author Shrikant Sharma
 */
public class Utils {
	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public static <T> void validateBean(T object) {
		validateBean(object, null);
	}

	public static <T> void validateBean(T object, Runnable afterViolationRunnable) {
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		for (ConstraintViolation<T> violation : violations) {
			//running on same thread, runnable will decide to run parallel or not
			afterViolationRunnable.run();
			throw new InvalidResourceName(object.getClass(), violation.getMessage());
		}
	}
}
