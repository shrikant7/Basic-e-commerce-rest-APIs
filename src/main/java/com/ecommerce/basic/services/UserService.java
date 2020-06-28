package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.InvalidResourceName;
import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.repositories.OtpRepository;
import com.ecommerce.basic.repositories.UserInfoRepository;
import com.ecommerce.basic.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import static com.ecommerce.basic.utils.Utils.validateBean;

/**
 * @author Shrikant Sharma
 */

@Service
public class UserService {
	@Autowired
	private UserInfoRepository userInfoRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OtpRepository otpRepository;

	public User findByUsername(String username) {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		optionalUser.orElseThrow(()-> new NoSuchResourceException(UserService.class, "No user found for UserName: "+username));
		return optionalUser.get();
	}

	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public Otp generateOtp(String userName) {
		User user = findByUsername(userName);
		Optional<Otp> optionalOtp = otpRepository.findByUser(user);
		Otp otp = new Otp();
		if(optionalOtp.isPresent()) {
			otp = optionalOtp.get();
		}

		otp.setUser(user)
				.setOtp(getRandomOtp())
				.setGeneratedDatetime(LocalDateTime.now());

		return otpRepository.saveAndFlush(otp);
	}

	private String getRandomOtp() {
		Random random = new Random();
		int count = 6;
		StringBuilder otp = new StringBuilder();
		while (count > 0) {
			int digit = random.nextInt(10 + 26 + 26);
			if(digit<10) {
				otp.append(digit);
			} else if(digit >= 10+26) {
				otp.append((char)('A'+digit-10-26));
			} else {
				otp.append((char)('a'+digit-10));
			}
			count--;
		}
		return otp.toString();
	}

	public User verifyOtp(String userName, OtpVerificationRequest otpVerificationRequest) {
		User user = findByUsername(userName);
		Optional<Otp> optionalOtp = otpRepository.findByUser(user);
		optionalOtp.orElseThrow(()-> new InvalidResourceName(UserService.class, "Otp expired, please request again"));
		Otp otp =optionalOtp.get();
		if(!otp.getOtp().equalsIgnoreCase(otpVerificationRequest.getOtp()) || LocalDateTime.now().isAfter(otp.getGeneratedDatetime().plusMinutes(10))) {
			throw new InvalidResourceName(UserService.class, "Otp doesn't match/expired, retry please");
		}
		user.setPassword(otpVerificationRequest.getNewPassword());

		validateBean(user);
		return userRepository.saveAndFlush(user);
	}

	public User signUpUserRequest(SignUpRequest signUpRequest) {
		User user = new User()
						.setUsername(signUpRequest.getUsername())
						.setPassword(signUpRequest.getPassword())
						.setActive(true)
						.setRoles(signUpRequest.getRole());
		UserInfo userInfo = new UserInfo()
				.setFullName(signUpRequest.getFullName())
				.setPhoneNumber(signUpRequest.getPhoneNumber())
				.setEmail(signUpRequest.getEmail())
				.setAddress(signUpRequest.getAddress())
				.setCity(signUpRequest.getCity())
				.setPincode(signUpRequest.getPincode())
				.setState(signUpRequest.getState())
				.setUser(user);

		if(user.getRoles() == null) {
			user.setRoles(User.ROLE_USER);
		} else if(!user.getRoles().equalsIgnoreCase(User.ROLE_ADMIN) && !user.getRoles().equalsIgnoreCase(User.ROLE_USER)) {
			throw new InvalidResourceName(UserService.class, "User's role is not valid");
		}

		validateBean(user);
		validateBean(userInfo);
		User savedUser = userRepository.save(user);
		userInfoRepository.saveAndFlush(userInfo);
		return savedUser;
	}

	public UserInfo getUserInfoByUser(User user) {
		return userInfoRepository.findByUser(user);
	}

	public UserInfo getUserInfoByUsername(String userName) {
		User user = findByUsername(userName);
		return getUserInfoByUser(user);
	}
}
