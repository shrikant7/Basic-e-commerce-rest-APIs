package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.*;
import com.ecommerce.basic.services.*;
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
	@Autowired
	private HighlightService highlightService;
	@Autowired
	private OrderService orderService;

	ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/user")
	public String user(){
		return "Hello user";
	}

	@PostMapping("/users/{userName}/checkout")
	public OrderItem createOrder(@PathVariable("userName") String userName,
	                             @RequestBody List<OrderRequest> orderRequest) {
		return orderService.createOrder(userName, orderRequest);
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

	@GetMapping("categories/{categoryName}")
	public Category geCategoryByName(@PathVariable String categoryName){
		return categoryService.getCategoryByName(categoryName);
	}
	@PostMapping("/categories")
	public Category createCategory(@RequestParam("categoryName") String categoryName){
		return categoryService.createCategory(new Category().setCategoryName(categoryName));
	}

	@GetMapping("/categories/{categoryName}/products")
	public List<Product> getAllProductsOfCategory(@PathVariable("categoryName") String categoryName) {
		return categoryService.getAllProductsOfCategoryName(categoryName);
	}

	@PostMapping(value = "/categories/{categoryName}/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Product createProduct(
			@PathVariable("categoryName") String categoryName,
			@RequestParam("productJson") String productJson,
			@RequestParam("file") MultipartFile productImage) throws JsonProcessingException {

		String imageName = imageStorageService.storeImage(categoryName, productImage);
		String imageDownloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
										.path("api/downloadImage/")
										.path(imageName).toUriString();

		Category category = categoryService.getCategoryByName(categoryName);

		Product product = objectMapper.readValue(productJson,Product.class)
										.setCategory(category)
										.setImageUri(imageDownloadURI);

		return productService.createProduct(product);
	}

	@GetMapping("/highlights")
	public List<Highlight> getAllHighlights() {
		return highlightService.getAllHighlights();
	}

	@PostMapping("/highlights")
	public Highlight createHighlight(@RequestParam int productId) {
		return highlightService.createHighlight(productId);
	}

	@DeleteMapping("/highlights/{highlightId}")
	public Highlight deleteHighlight(@PathVariable("highlightId") int highlightId){
		return highlightService.deleteHighlight(highlightId);
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
