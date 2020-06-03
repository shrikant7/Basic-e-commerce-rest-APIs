package com.ecommerce.basic.services;

import com.ecommerce.basic.models.Otp;
import com.ecommerce.basic.models.OtpVerificationRequest;
import com.ecommerce.basic.models.User;
import com.ecommerce.basic.repositories.OtpRepository;
import com.ecommerce.basic.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * @author Shrikant Sharma
 */

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OtpRepository otpRepository;

	public User findByUsername(String username) {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		optionalUser.orElseThrow(()-> new UsernameNotFoundException("Not found: "+username));
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
		optionalOtp.orElseThrow(()-> new RuntimeException("Otp expired, please request again."));
		Otp otp =optionalOtp.get();
		if(!otp.getOtp().equals(otpVerificationRequest.getOtp()) || LocalDateTime.now().isAfter(otp.getGeneratedDatetime().plusMinutes(10))) {
			throw new RuntimeException("Otp doesn't match/expired, retry please.");
		}
		validatePassword(otpVerificationRequest.getNewPassword());
		user.setPassword(otpVerificationRequest.getNewPassword());
		return userRepository.saveAndFlush(user);
	}

	private void validatePassword(String newPassword) {
		if(newPassword == null || newPassword.length() < 4) {
			throw new RuntimeException("Not a valid Password, password length should be greater than 4.");
		}
	}
}
