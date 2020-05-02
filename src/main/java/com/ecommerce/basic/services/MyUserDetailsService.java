package com.ecommerce.basic.services;

import com.ecommerce.basic.models.MyUserDetails;
import com.ecommerce.basic.models.User;
import com.ecommerce.basic.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Shrikant Sharma
 */
@Service
public class MyUserDetailsService implements UserDetailsService {

	@Autowired
	UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return new MyUserDetails(userService.findByUsername(username));
	}
}
