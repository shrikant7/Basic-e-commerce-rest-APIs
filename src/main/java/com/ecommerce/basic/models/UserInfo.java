package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Shrikant Sharma
 */
@Entity @Table
@Data @Accessors(chain = true)
public class UserInfo implements Serializable {
	@Id
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	@JsonIgnore @ToString.Exclude
	private User user;
	private String fullName;
	@Column(length = 10)
	@Size(min = 10, max = 10, message = "Please enter a valid 10 digit Phone Number")
	private String phoneNumber;
	@Email(message = "Please enter a valid Email")
	private String email;
	private String address;
	private String pincode;
	private String city;
	private String state;
}
