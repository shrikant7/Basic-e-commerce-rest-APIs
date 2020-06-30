package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
public class OtpVerificationRequest {
	@NotBlank(message = "Username can't be blank")
	private String username;
	@NotBlank(message = "OTP can't be blank")
	private String otp;
	@NotBlank(message = "Password can't be blank")
	@Size(message = "Password's length can't be less than 4")
	private String newPassword;
}
