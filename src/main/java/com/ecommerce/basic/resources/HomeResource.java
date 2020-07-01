package com.ecommerce.basic.resources;

import com.ecommerce.basic.exceptions.InvalidCredentialException;
import com.ecommerce.basic.models.*;
import com.ecommerce.basic.services.ImageStorageService;
import com.ecommerce.basic.services.MailSenderService;
import com.ecommerce.basic.services.UserService;
import com.ecommerce.basic.utils.JwtUtil;
import com.ecommerce.basic.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.BAD_CREDENTIAL_EXCEPTION;

/**
 * @author Shrikant Sharma
 */

@RestController
@RequestMapping("/api")
public class HomeResource {
	@Autowired
	private JwtUtil jwtTokenUtil;
	@Autowired
	private UserService userService;
	@Autowired
	private MailSenderService mailSenderService;
	@Autowired
	private ImageStorageService imageStorageService;
	@Autowired
	private AuthenticationManager authenticationManager;

	private AuthenticationResponse createAuthenticationResponse(User user) {
		final UserInfo userInfo = userService.getUserInfoByUser(user);
		final UserWithInfoDto userWithInfoDto = Utils.mapToUserWithInfoDto(user,userInfo);
		final String jwt = jwtTokenUtil.generateToken(user.getUsername());
		return new AuthenticationResponse(jwt, userWithInfoDto);
	}

	@PostMapping("/generate-otp")
	public MailDTO generateOtp(@Valid @RequestBody UsernameDTO usernameDTO) {
		Otp otp = userService.generateOtp(usernameDTO.getUsername());
		String email = userService.getUserInfoByUser(otp.getUser()).getEmail();
		mailSenderService.sendSimpleMailOtpToUserCloud(otp,email);
		return new MailDTO(email);
	}

	@PostMapping("/verify-otp")
	public ResponseEntity<?> verifyOtp(@Valid @RequestBody OtpVerificationRequest otpVerificationRequest) {
		User user = userService.verifyOtp(otpVerificationRequest);
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		User user = userService.signUpUserRequest(signUpRequest);
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
															authenticationRequest.getPassword()));
		}catch (InternalAuthenticationServiceException | BadCredentialsException e){
			throw new InvalidCredentialException(BAD_CREDENTIAL_EXCEPTION, BAD_CREDENTIAL_EXCEPTION.getHint());
		}

		final User user = userService.findByUsername(authenticationRequest.getUsername());
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@GetMapping("/downloadImage/{imageName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String imageName, HttpServletRequest request) {
		Resource resource = imageStorageService.loadImageAsResource(imageName);
		String contentType;
		contentType = request.getServletContext().getMimeType(imageName);

		//if we are not able to determine it contentType then mark it unknown binary object;
		if(contentType == null){
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,resource.getFilename())
				.body(resource);
	}
}
