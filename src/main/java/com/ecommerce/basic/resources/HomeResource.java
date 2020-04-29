package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.AuthenticationRequest;
import com.ecommerce.basic.models.AuthenticationResponse;
import com.ecommerce.basic.models.Category;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.services.CategoryService;
import com.ecommerce.basic.services.ImageStorageService;
import com.ecommerce.basic.services.MyUserDetailsService;
import com.ecommerce.basic.services.ProductService;
import com.ecommerce.basic.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

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
	@Autowired
	private CategoryService categoryService;

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

	@GetMapping("/categories")
	public List<Category> getAllCategories() {
		return categoryService.getAllCategories();
	}

	@GetMapping("/categories/{categoryId}")
	public Category getCategoryByCategoryId(@PathVariable int categoryId){
		return categoryService.getCategoryByID(categoryId);
	}

	@PostMapping("/categories")
	public Category createCategory(@RequestParam("categoryName") String categoryName){
		return categoryService.createCategory(new Category().setCategoryName(categoryName));
	}

	@GetMapping("/categories/{categoryId}/products")
	public List<Product> getAllProductsOfCategory(@PathVariable("categoryId") int categoryId) {
		return categoryService.getAllProductsOfCategoryId(categoryId);
	}

	@PostMapping(value = "/categories/{categoryId}/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Product createProduct(
			@PathVariable("categoryId") int categoryId,
			@RequestParam("productJson") String productJson,
			@RequestParam("file") MultipartFile productImage) throws JsonProcessingException {

		String imageName = imageStorageService.storeImage(categoryId, productImage);
		String imageDownloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
										.path("api/downloadImage/")
										.path(imageName).toUriString();

		Category category = categoryService.getCategoryByID(categoryId);

		Product product = objectMapper.readValue(productJson,Product.class)
				.setCategory(category)
				.setImageURI(imageDownloadURI);

		return productService.createProduct(product);
	}

	@GetMapping("/downloadImage/{imageName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String imageName, HttpServletRequest request){
		Resource resource = imageStorageService.loadImageAsResource(imageName);
		String contentType = null;
		try{
			contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
		} catch (IOException e){
			e.printStackTrace();
		}

		//if we are not able to determine it contentType then mark it unknown binary object;
		if(contentType == null){
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
		}
		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION,resource.getFilename())
				.body(resource);
	}

}
