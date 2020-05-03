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
	private UserService userService;
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

	@GetMapping("/users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("users/{userName}")
	public User getUserByUsername(@PathVariable String userName) {
		return userService.findByUsername(userName);
	}

	@PostMapping("/users/{userName}/checkout")
	public OrderItem createOrder(@PathVariable("userName") String userName,
	                             @RequestBody List<OrderRequest> orderRequest) {
		return orderService.createOrder(userName, orderRequest);
	}

	@GetMapping("/users/{userName}/order-history")
	public List<OrderItem> getOrderHistory(@PathVariable("userName") String userName,
	                                       @RequestParam(value = "offset", defaultValue = "0") int offset,
	                                       @RequestParam(value = "limit", defaultValue = "0") int limit) {
		return orderService.getOrderHistory(userName, offset, limit);
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
	public Category getCategoryByName(@PathVariable String categoryName) {
		return categoryService.getCategoryByName(categoryName);
	}

	@PostMapping("/categories")
	public Category createCategory(@RequestParam("categoryName") String categoryName) {
		return categoryService.createCategory(new Category().setCategoryName(categoryName));
	}

/*
	@PutMapping("/categories/{categoryName}")
	public Category updateCategory(@PathVariable String categoryName, @RequestBody Category newCategory) {
		return categoryService.updateCategory(categoryName, newCategory);
	}
*/

	@DeleteMapping("/categories/{categoryName}")
	public Category deleteCategory(@PathVariable String categoryName) {
		Category category = categoryService.deleteCategory(categoryName);
		imageStorageService.deleteAllImages(category.getCategoryName());
		return category;
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

		String imageDownloadURI = storeAndGetImageUri(categoryName, productImage);

		Category category = categoryService.getCategoryByName(categoryName);
		Product product = objectMapper.readValue(productJson,Product.class)
										.setCategory(category)
										.setImageUri(imageDownloadURI);

		return productService.createProduct(product);
	}

	private String storeAndGetImageUri(@PathVariable("categoryName") String categoryName, @RequestParam("file") MultipartFile productImage) {
		String imageName = imageStorageService.storeImage(categoryName, productImage);
		return ServletUriComponentsBuilder.fromCurrentContextPath()
										.path("api/downloadImage/")
										.path(imageName).toUriString();
	}

	@GetMapping("/categories/{categoryName}/products/{productId}")
	public Product getSingleProduct(@PathVariable("categoryName") String categoryName,
	                                @PathVariable("productId") int productId) {
		return productService.getProductByCategory(categoryName, productId);
	}

	@PutMapping(value = "/categories/{categoryName}/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Product updateSingleProduct(@PathVariable("categoryName") String categoryName,
	                                   @PathVariable("productId") int productId,
	                                   @RequestParam(value = "productJson", required = false) String productJson,
	                                   @RequestParam(value = "file", required = false) MultipartFile productImage) throws JsonProcessingException {
		Product product = productService.getProductByCategory(categoryName, productId);

		Product newProduct;
		if(productJson != null) {
			newProduct = objectMapper.readValue(productJson, Product.class);
			newProduct.setProductId(product.getProductId());
			newProduct.setImageUri(product.getImageUri());

			if (newProduct.getName() == null || newProduct.getName().equals("")) {
				newProduct.setName(product.getName());
			}

			if(newProduct.getDescription() == null || newProduct.getDescription().equals("")) {
				newProduct.setDescription(product.getDescription());
			}

			if (newProduct.getMrpPrice() == null || newProduct.getMrpPrice() == 0) {
				newProduct.setMrpPrice(product.getMrpPrice());
			}

			if (newProduct.getYourPrice() == null || newProduct.getYourPrice() == 0) {
				newProduct.setYourPrice(product.getYourPrice());
			}

			if (newProduct.getCategory() != null && newProduct.getCategory().getCategoryName() != null) {
				Category category = categoryService.getCategoryByName(newProduct.getCategory().getCategoryName());
				newProduct.setCategory(category);
				if (!category.getCategoryName().equals(categoryName)) {
					String[] split = product.getImageUri().split("/");
					String imageName = split[split.length - 1];
					String newImageName = imageStorageService.moveImage(imageName, category.getCategoryName());
					String imageDownloadURI = ServletUriComponentsBuilder.fromCurrentContextPath()
																		.path("api/downloadImage/")
																		.path(newImageName).toUriString();
					newProduct.setImageUri(imageDownloadURI);
				}
			} else {
				newProduct.setCategory(product.getCategory());
			}
		}else {
			newProduct = product;
		}

		if(productImage != null && !productImage.isEmpty()) {
			String[] split = product.getImageUri().split("/");
			String imageName = split[split.length-1];
			imageStorageService.deleteImage(imageName);
			String imageDownloadURI = storeAndGetImageUri(newProduct.getCategory().getCategoryName(), productImage);
			newProduct.setImageUri(imageDownloadURI);
		}
		return productService.updateSingleProduct(product, newProduct);
	}

	@DeleteMapping("/categories/{categoryName}/products/{productId}")
	public Product deleteSingleProduct(@PathVariable("categoryName") String categoryName,
	                                   @PathVariable("productId") int productId) {
		Product product = productService.deleteProductByCategory(categoryName, productId);
		String[] split = product.getImageUri().split("/");
		String imageName = split[split.length-1];
		imageStorageService.deleteImage(imageName);
		return product;
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
	public Highlight deleteHighlight(@PathVariable("highlightId") int highlightId) {
		return highlightService.deleteHighlight(highlightId);
	}

	@GetMapping("/downloadImage/{imageName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String imageName, HttpServletRequest request) {
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
