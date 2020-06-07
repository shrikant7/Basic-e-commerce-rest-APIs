package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class Otp {
	@Id
	@GeneratedValue
	private Long otpId;

	@ManyToOne
	@JoinColumn(name = "username", referencedColumnName = "username")
	private User user;

	@NotEmpty(message = "OTP can't be empty")
	private String otp;

	@NotNull(message = "DateTime can't be null")
	private LocalDateTime generatedDatetime;
}
