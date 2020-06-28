package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
public class UserInfo implements Serializable {
	@Id
	@GeneratedValue
	private Long userInfoId;

	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	@NotBlank(message = "Name can't be blank")
	private String fullName;

	@Column(length = 10)
	@NotNull(message = "PhoneNumber is a mandatory field")
	@Size(min = 10, max = 10, message = "Please enter a valid 10 digit Phone Number")
	private String phoneNumber;

	@NotNull(message = "Email is a mandatory field")
	@Email(message = "Please enter a valid Email")
	private String email;

	private String address;
	private String pincode;
	private String city;
	private String state;
}
