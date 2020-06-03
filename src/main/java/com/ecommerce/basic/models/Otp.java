package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
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
	private String otp;
	private LocalDateTime generatedDatetime;
}
