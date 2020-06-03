package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
public class OtpVerificationRequest {
	private String otp;
	private String newPassword;
}
