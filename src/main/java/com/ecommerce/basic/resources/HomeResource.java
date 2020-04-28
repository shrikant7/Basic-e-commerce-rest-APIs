package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.AuthenticationRequest;
import com.ecommerce.basic.models.AuthenticationResponse;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.services.ImageStorageService;
import com.ecommerce.basic.services.MyUserDetailsService;
import com.ecommerce.basic.services.ProductService;
import com.ecommerce.basic.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

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
	@Autowired
	private ImageStorageService imageStorageService;
	@Autowired
private ProductService productService;

	ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/user")
	public String user(){
		return "Hello user";
	}

	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
			);
		}catch (BadCredentialsException e){
			throw new Exception("Incorrect username or password",e);
		}

		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
		final String jwt = jwtTokenUtil.generateToken(userDetails);

		return ResponseEntity.ok(new AuthenticationResponse(jwt));
	}

	@PostMapping(value = "/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Product createProduct(
			@RequestParam("productJson") String productJson,
			@RequestParam("file") MultipartFile productImage) throws JsonProcessingException {
		String imageName = imageStorageService.storeImage(productImage);
		String imageDownloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
										.path("downloadImage")
										.path(imageName).toUriString();
		Product product = objectMapper.readValue(productJson,Product.class);
		product.setImageURI(imageDownloadURI);
		productService.createProduct(product);
		return product;
	}

}
