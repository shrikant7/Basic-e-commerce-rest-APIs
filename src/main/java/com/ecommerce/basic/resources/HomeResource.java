package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.AuthenticationRequest;
import com.ecommerce.basic.models.AuthenticationResponse;
import com.ecommerce.basic.services.MyUserDetailsService;
import com.ecommerce.basic.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @author Shrikant Sharma
 */
@RestController
@RequestMapping("/api")
public class HomeResource {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private MyUserDetailsService userDetailsService;

	@Autowired
	private JwtUtil jwtTokenUtil;

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUserName(), authenticationRequest.getPassword())
			);
		}catch (BadCredentialsException e){
			throw new Exception("Incorrect username or password",e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUserName());
		final String jwt = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

}
