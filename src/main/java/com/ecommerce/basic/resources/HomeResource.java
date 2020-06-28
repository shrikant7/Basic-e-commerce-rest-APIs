package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.*;
import com.ecommerce.basic.services.*;
import com.ecommerce.basic.utils.JwtUtil;
import com.ecommerce.basic.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
	@Autowired
	private CartService cartService;
	@Autowired
	private MailSenderService mailSenderService;
	@Value("${host-address}")
	private String hostAddress;

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

	@GetMapping("/users/{userName}/user-info")
	public UserInfo getUserInfoByUsername(@PathVariable String userName) {
		return userService.getUserInfoByUsername(userName);
	}

	@PostMapping("users/{userName}/generate-otp")
	public MailDTO generateOtp(@PathVariable String userName) {
		Otp otp = userService.generateOtp(userName);
		String email = userService.getUserInfoByUser(otp.getUser()).getEmail();
		mailSenderService.sendSimpleMailOtpToUserCloud(otp,email);
		return new MailDTO(email);
	}

	@PostMapping("users/{userName}/verify-otp")
	public ResponseEntity<?> verifyOtp(@PathVariable String userName,
	                                   @RequestBody OtpVerificationRequest otpVerificationRequest) {
		User user = userService.verifyOtp(userName, otpVerificationRequest);
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@GetMapping("users/{userName}/cart")
	public CartItem getCart(@PathVariable String userName) {
		return cartService.getCartItem(userName);
	}

	@DeleteMapping("users/{userName}/cart")
	public CartItem deleteCart(@PathVariable String userName) {
		return cartService.deleteCart(userName);
	}

	@PostMapping("users/{userName}/cart/cart-detail")
	public CartItem createCartDetail(@PathVariable String userName,
	                          @Valid @RequestBody CartDetailRequest detailRequest) {
		return cartService.addToCart(userName, detailRequest);
	}

	@DeleteMapping("users/{userName}/cart/cart-detail/{cartDetailId}")
	public CartItem deleteCartDetail(@PathVariable("userName") String userName,
	                               @PathVariable("cartDetailId") long cartDetailId) {
		return cartService.removeFromCart(userName, cartDetailId);
	}

	@PostMapping("/users/{userName}/checkout")
	public OrderItem createOrder(@PathVariable("userName") String userName,
	                             @Valid @RequestBody List<OrderRequest> orderRequest) {
		OrderItem orderItem = orderService.createOrder(userName, orderRequest);
		mailSenderService.sendSimpleMailToAdminCloud(orderItem);
		return orderItem;
	}

	@GetMapping("/users/{userName}/order-history")
	public List<OrderItem> getOrderHistory(@PathVariable("userName") String userName,
	                                       @RequestParam(value = "offset", defaultValue = "0") int offset,
	                                       @RequestParam(value = "limit", defaultValue = "0") int limit) {
		return orderService.getOrderHistory(userName, offset, limit);
	}

	@GetMapping("/users/{userName}/orderItems")
	public List<OrderItemDto> getOnlyOrderItems(@PathVariable("userName") String userName,
	                                            @RequestParam(value = "offset", defaultValue = "0") int offset,
	                                            @RequestParam(value = "limit", defaultValue = "0") int limit) {
		List<OrderItem> orderHistory = orderService.getOrderHistory(userName, offset, limit);
		return orderHistory.stream().map(Utils::mapToOrderItemDto).collect(Collectors.toList());
	}

	@GetMapping("/users/{userName}/orderItems/{itemId}")
	public List<OrderDetailDto> getOrderDetail(@PathVariable("userName") String userName,
	                                     @PathVariable("itemId") long orderId) {
		OrderItem orderItem = orderService.getOrderItem(userName, orderId);
		return orderItem.getOrderDetails().stream().map(Utils::mapToOrderDetailDto).collect(Collectors.toList());
	}

	@PostMapping("/users/{userName}/reorder/{itemId}")
	public OrderItem reOrderItem(@PathVariable("userName") String userName,
	                             @PathVariable("itemId") long itemId) {
		return orderService.reOrderItem(userName, itemId);
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

		final User user = userService.findByUsername(authenticationRequest.getUsername());
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@PostMapping("/sign-up")
	public ResponseEntity<?> signUpUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		User user = userService.signUpUserRequest(signUpRequest);
		return ResponseEntity.ok(createAuthenticationResponse(user));
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

	@PutMapping("/categories/{categoryName}")
	public Category updateCategory(@PathVariable String categoryName,@Valid @RequestBody Category newCategory) {
		return categoryService.updateCategory(categoryName, newCategory);
	}

	@DeleteMapping("/categories/{categoryName}")
	public Category deleteCategory(@PathVariable String categoryName) {
		Category category = categoryService.deleteCategory(categoryName);
		//imageStorageService.deleteAllImages(category.getCategoryId());
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
			@RequestParam("file") MultipartFile productImage) throws IOException {

		Category category = categoryService.getCategoryByName(categoryName);
		String imageDownloadURI = storeAndGetImageUri(category.getCategoryId(), productImage);

		Product product = objectMapper.readValue(productJson,Product.class)
										.setCategory(category)
										.setImageUri(imageDownloadURI)
										.setDeleted(false);

		return productService.createProduct(product);
	}

	private String storeAndGetImageUri(long categoryId, MultipartFile productImage) {
		String imageName = imageStorageService.storeImage(categoryId, productImage);
		return ServletUriComponentsBuilder.fromHttpUrl(hostAddress)
										.path("api/downloadImage/")
										.path(imageName).toUriString();
	}

	@GetMapping("/categories/{categoryName}/products/{productId}")
	public Product getSingleProduct(@PathVariable("categoryName") String categoryName,
	                                @PathVariable("productId") long productId) {
		return productService.getProductUnderCategory(categoryName, productId);
	}

	@PutMapping(value = "/categories/{categoryName}/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Product updateSingleProduct(@PathVariable("categoryName") String categoryName,
	                                   @PathVariable("productId") long productId,
	                                   @RequestParam(value = "productJson", required = false) String productJson,
	                                   @RequestParam(value = "file", required = false) MultipartFile productImage) throws IOException {
		Product product = productService.getProductUnderCategory(categoryName, productId);

		Product newProduct;
		if(productJson != null) {
			newProduct = objectMapper.readValue(productJson, Product.class);
			newProduct.setProductId(product.getProductId());
			newProduct.setImageUri(product.getImageUri());
			newProduct.setDeleted(false);

			if (newProduct.getName() == null || newProduct.getName().equalsIgnoreCase("")) {
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
				if (!category.getCategoryName().equalsIgnoreCase(categoryName)) {
					String[] split = product.getImageUri().split("/");
					String imageName = split[split.length - 1];
					String newImageName = imageStorageService.moveImage(imageName, category.getCategoryId());
					String imageDownloadURI = ServletUriComponentsBuilder.fromHttpUrl(hostAddress)
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
			imageStorageService.deleteImage(product.getImageUri());
			String imageDownloadURI = storeAndGetImageUri(newProduct.getCategory().getCategoryId(), productImage);
			newProduct.setImageUri(imageDownloadURI);
		}
		return productService.updateSingleProduct(product, newProduct);
	}

	@DeleteMapping("/categories/{categoryName}/products/{productId}")
	public Product deleteSingleProduct(@PathVariable("categoryName") String categoryName,
	                                   @PathVariable("productId") long productId) {
		Product product = productService.deleteProductByCategory(categoryName, productId);
		//imageStorageService.deleteImage(product.getImageUri());
		return product;
	}

	@GetMapping("/highlights")
	public List<Highlight> getAllHighlights() {
		return highlightService.getAllHighlights();
	}

	@PostMapping("/highlights")
	public Highlight createHighlight(@RequestParam long productId) {
		return highlightService.createHighlight(productId);
	}

	@DeleteMapping("/highlights/{highlightId}")
	public Highlight deleteHighlight(@PathVariable("highlightId") long highlightId) {
		return highlightService.deleteHighlight(highlightId);
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

	private AuthenticationResponse createAuthenticationResponse(User user) {
		final UserInfo userInfo = userService.getUserInfoByUser(user);
		final UserWithInfoDto userWithInfoDto = Utils.mapToUserWithInfoDto(user,userInfo);
		final String jwt = jwtTokenUtil.generateToken(user.getUsername());
		return new AuthenticationResponse(jwt, userWithInfoDto);
	}
}
