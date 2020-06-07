package com.ecommerce.basic.utils;

import com.ecommerce.basic.exceptions.InvalidResourceName;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Shrikant Sharma
 */
public class Utils {
	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public static <T> void validateBean(T object) {
		Set<ConstraintViolation<T>> violations = validator.validate(object);
		for (ConstraintViolation<T> violation : violations) {
			throw new InvalidResourceName(object.getClass(), violation.getMessage());
		}
	}
}
