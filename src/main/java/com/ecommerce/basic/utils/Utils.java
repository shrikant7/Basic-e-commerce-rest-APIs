package com.ecommerce.basic.utils;

import com.ecommerce.basic.exceptions.InvalidResourceName;
import com.ecommerce.basic.models.*;

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

	public static OrderItemDto mapToOrderItemDto(OrderItem orderItem) {
		return new OrderItemDto(orderItem.getOrderId(),
				orderItem.getPlacedOn(),
				orderItem.getTotalValue());
	}

	public static OrderDetailDto mapToOrderDetailDto(OrderDetail orderDetail) {
		Product product = orderDetail.getProduct();
		return new OrderDetailDto(new ProductDto(product.getProductId(), product.getName(), product.getYourPrice(), product.getImageUri(), product.isDeleted()),
				orderDetail.getBoughtPrice(),
				orderDetail.getQuantity(),
				orderDetail.getProductTotal());
	}

	public static UserWithInfoDto mapToUserWithInfoDto(User user, UserInfo userInfo) {
		return new UserWithInfoDto(user.getId(),
									user.getUsername(),
									user.isActive(),
									user.getRoles(),
									userInfo);
	}
}
