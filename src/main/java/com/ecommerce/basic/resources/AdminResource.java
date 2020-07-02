package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.*;
import com.ecommerce.basic.services.*;
import com.ecommerce.basic.utils.JwtUtil;
import com.ecommerce.basic.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@RestController
@RequestMapping("/api/admin")
public class AdminResource {
    @Autowired
    private JwtUtil jwtTokenUtil;
    @Autowired
    private UserService userService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private HighlightService highlightService;
    @Autowired
    private ImageStorageService imageStorageService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AuthenticationResponse createAuthenticationResponse(User user) {
        final UserInfo userInfo = userService.getUserInfoByUser(user);
        final UserWithInfoDto userWithInfoDto = Utils.mapToUserWithInfoDto(user,userInfo);
        final String jwt = jwtTokenUtil.generateToken(user.getUsername());
        return new AuthenticationResponse(jwt, userWithInfoDto);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{username}")
    public User getUserByUsername(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/users/{username}/user-info")
    public UserInfo getUserInfoByUsername(@PathVariable String username) {
        return userService.getUserInfoByUsername(username);
    }

    @PostMapping("/users/{username}/authenticate")
    public ResponseEntity<?> createAuthenticationForUser(@PathVariable("username") String username) {
        User user = userService.findByUsername(username);
        return ResponseEntity.ok(createAuthenticationResponse(user));
    }

    @PostMapping("/users/{username}/authorize")
    public User changeAuthorizationOfUser(@PathVariable("username") String username,
                                          @Valid @RequestBody RoleDTO roleDTO) {
        return userService.changeAuthorizationOfUser(username,roleDTO.getRole());
    }

    @PostMapping("/highlights")
    public Highlight createHighlight(@RequestParam long productId) {
        return highlightService.createHighlight(productId);
    }

    @DeleteMapping("/highlights/{highlightId}")
    public Highlight deleteHighlight(@PathVariable("highlightId") long highlightId) {
        return highlightService.deleteHighlight(highlightId);
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

    @PostMapping(value = "/categories/{categoryName}/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product createProduct(
            @PathVariable("categoryName") String categoryName,
            @RequestParam("productJson") String productJson,
            @RequestParam("file") MultipartFile productImage) throws IOException {

        Category category = categoryService.getCategoryByName(categoryName);
        String imageDownloadURI = imageStorageService.storeImage(category.getCategoryId(), productImage);

        Product product = objectMapper.readValue(productJson,Product.class)
                .setCategory(category)
                .setImageUri(imageDownloadURI)
                .setDeleted(false);

        return productService.createProduct(product);
    }

    @PostMapping("/categories/{categoryName}/move-products")
    public Category moveProductsToNewCategory(@PathVariable("categoryName") String categoryName,
                                              @RequestBody ProductIdsDTO productIdsDTO) {
        return productService.moveProductsToCategory(categoryName, productIdsDTO.getProductIds());
    }

    @PatchMapping(value = "/categories/{categoryName}/products/{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Product editSingleProduct(@PathVariable("categoryName") String categoryName,
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
                    String newImageDownloadURI = imageStorageService.moveImage(product.getImageUri(), category.getCategoryId());
                    newProduct.setImageUri(newImageDownloadURI);
                }
            } else {
                newProduct.setCategory(product.getCategory());
            }
        }else {
            newProduct = product;
        }

        if(productImage != null && !productImage.isEmpty()) {
            imageStorageService.deleteImage(product.getImageUri());
            String imageDownloadURI = imageStorageService.storeImage(newProduct.getCategory().getCategoryId(), productImage);
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
}
