package com.ecommerce.basic.services;

import com.ecommerce.basic.models.User;
import com.ecommerce.basic.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Shrikant Sharma
 */

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

	public User findByUsername(String username) {
		Optional<User> optionalUser = userRepository.findByUsername(username);
		optionalUser.orElseThrow(()-> new UsernameNotFoundException("Not found: "+username));
		return optionalUser.get();
	}
}
