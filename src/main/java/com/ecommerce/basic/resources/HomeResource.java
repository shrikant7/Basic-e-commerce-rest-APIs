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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
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
	private final ObjectMapper objectMapper = new ObjectMapper();

	@GetMapping("/admin/users")
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/admin/users/{username}")
	public User getUserByUsername(@PathVariable String username) {
		return userService.findByUsername(username);
	}

	@GetMapping("/admin/users/{username}/user-info")
	public UserInfo getUserInfoByUsername(@PathVariable String username) {
		return userService.getUserInfoByUsername(username);
	}

	@PostMapping("/admin/users/{username}/authenticate")
	public ResponseEntity<?> getAuthenticationForUser(@PathVariable("username") String username) {
		User user = userService.findByUsername(username);
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@PostMapping("/generate-otp")
	public MailDTO generateOtp(@RequestBody Map<String, String> body) {
		Otp otp = userService.generateOtp(body.get("username"));
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
	public ResponseEntity<?> createAuthenticationToken(
									@Valid @RequestBody AuthenticationRequest authenticationRequest) throws Exception {
		try {
			authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
															authenticationRequest.getPassword()));
		}catch (BadCredentialsException e){
			throw new Exception("Incorrect username or password",e);
		}

		final User user = userService.findByUsername(authenticationRequest.getUsername());
		return ResponseEntity.ok(createAuthenticationResponse(user));
	}

	@GetMapping("/user")
	public User getUser() {
		return getPrincipalUser();
	}

	@GetMapping("/user/user-info")
	public UserInfo getUserInfo() {
		return userService.getUserInfoByUser(getPrincipalUser());
	}

	@GetMapping("/user/cart")
	public CartItem getCart() {
		User user = getPrincipalUser();
		return cartService.getCartItem(user);
	}

	@DeleteMapping("/user/cart")
	public CartItem deleteCart() {
		return cartService.deleteCart(getPrincipalUser());
	}

	@PostMapping("/user/cart/cart-detail")
	public CartItem createCartDetail(@Valid @RequestBody CartDetailRequest detailRequest) {
		User user = getPrincipalUser();
		return cartService.addToCart(user, detailRequest);
	}

	@DeleteMapping("/user/cart/cart-detail/{cartDetailId}")
	public CartItem deleteCartDetail(@PathVariable("cartDetailId") long cartDetailId) {
		User user = getPrincipalUser();
		return cartService.removeFromCart(user, cartDetailId);
	}

	@PostMapping("/user/checkout")
	public OrderItem createOrder(@Valid @RequestBody List<OrderRequest> orderRequest) {
		User user = getPrincipalUser();
		OrderItem orderItem = orderService.createOrder(user, orderRequest);
		mailSenderService.sendSimpleMailToAdminCloud(orderItem);
		return orderItem;
	}

	@GetMapping("/user/order-history")
	public List<OrderItem> getOrderHistory(@RequestParam(value = "offset", defaultValue = "0") int offset,
	                                       @RequestParam(value = "limit", defaultValue = "0") int limit) {
		User user = getPrincipalUser();
		return orderService.getOrderHistory(user, offset, limit);
	}

	@GetMapping("/user/order-items")
	public List<OrderItemDto> getOnlyOrderItems(@RequestParam(value = "offset", defaultValue = "0") int offset,
	                                            @RequestParam(value = "limit", defaultValue = "0") int limit) {
		User user = getPrincipalUser();
		List<OrderItem> orderHistory = orderService.getOrderHistory(user, offset, limit);
		return orderHistory.stream().map(Utils::mapToOrderItemDto).collect(Collectors.toList());
	}

	@GetMapping("/user/order-items/{itemId}")
	public List<OrderDetailDto> getOrderDetail(@PathVariable("itemId") long orderId) {
		User user = getPrincipalUser();
		OrderItem orderItem = orderService.getOrderItem(user, orderId);
		return orderItem.getOrderDetails().stream().map(Utils::mapToOrderDetailDto).collect(Collectors.toList());
	}

	//add now available products to cart from orderItem
	@PostMapping("/user/reorder/{itemId}")
	public CartItem reOrderItem(@PathVariable("itemId") long itemId) {
		User user = getPrincipalUser();
		return orderService.reOrderItem(user, itemId);
	}

	@GetMapping("/user/categories")
	public List<Category> getAllCategories() {
		return categoryService.getAllCategories();
	}

	@GetMapping("/user/categories/{categoryName}")
	public Category getCategoryByName(@PathVariable String categoryName) {
		return categoryService.getCategoryByName(categoryName);
	}

	@PostMapping("/admin/categories")
	public Category createCategory(@RequestParam("categoryName") String categoryName) {
		return categoryService.createCategory(new Category().setCategoryName(categoryName));
	}

	@PutMapping("/admin/categories/{categoryName}")
	public Category updateCategory(@PathVariable String categoryName,@Valid @RequestBody Category newCategory) {
		return categoryService.updateCategory(categoryName, newCategory);
	}

	@DeleteMapping("/admin/categories/{categoryName}")
	public Category deleteCategory(@PathVariable String categoryName) {
		Category category = categoryService.deleteCategory(categoryName);
		//imageStorageService.deleteAllImages(category.getCategoryId());
		return category;
	}

	@GetMapping("/user/categories/{categoryName}/products")
	public List<Product> getAllProductsOfCategory(@PathVariable("categoryName") String categoryName) {
		return categoryService.getAllProductsOfCategoryName(categoryName);
	}

	@PostMapping(value = "/admin/categories/{categoryName}/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

	@GetMapping("/user/categories/{categoryName}/products/{productId}")
	public Product getSingleProduct(@PathVariable("categoryName") String categoryName,
	                                @PathVariable("productId") long productId) {
		return productService.getProductUnderCategory(categoryName, productId);
	}

	@PutMapping(value = "/admin/categories/{categoryName}/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

	@DeleteMapping("/admin/categories/{categoryName}/products/{productId}")
	public Product deleteSingleProduct(@PathVariable("categoryName") String categoryName,
	                                   @PathVariable("productId") long productId) {
		Product product = productService.deleteProductByCategory(categoryName, productId);
		//imageStorageService.deleteImage(product.getImageUri());
		return product;
	}

	@GetMapping("/user/highlights")
	public List<Highlight> getAllHighlights() {
		return highlightService.getAllHighlights();
	}

	@PostMapping("/admin/highlights")
	public Highlight createHighlight(@RequestParam long productId) {
		return highlightService.createHighlight(productId);
	}

	@DeleteMapping("/admin/highlights/{highlightId}")
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

	//get authenticated user
	private User getPrincipalUser() {
		return ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
	}
}
