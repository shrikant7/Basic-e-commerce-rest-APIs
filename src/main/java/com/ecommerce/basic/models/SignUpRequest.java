package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
public class SignUpRequest {
	@NotBlank(message = "Username can't be blank")
	String username;

	@NotBlank(message = "Password can't be blank")
	@Size(message = "Password's length can't be less than 4")
	String password;

	String role;

	@NotBlank(message = "FullName can't be blank")
	String fullName;

	@NotNull(message = "PhoneNumber is a mandatory field")
	@Size(min = 10, max = 10, message = "Please enter a valid 10 digit Phone Number")
	String phoneNumber;

	@NotNull(message = "Email is a mandatory field")
	@Email(message = "Please enter a valid Email")
	String email;

	String address;
	String pincode;
	String city;
	String state;
}
