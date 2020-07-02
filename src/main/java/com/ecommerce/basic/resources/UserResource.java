package com.ecommerce.basic.resources;

import com.ecommerce.basic.models.*;
import com.ecommerce.basic.services.*;
import com.ecommerce.basic.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shrikant Sharma
 */

@RestController
@RequestMapping("/api/user")
public class UserResource {
    @Autowired
    private UserService userService;
    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private HighlightService highlightService;
    @Autowired
    private MailSenderService mailSenderService;

    //get authenticated user
    private User getPrincipalUser() {
        return ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

    @GetMapping("")
    public User getUser() {
        return getPrincipalUser();
    }

    @GetMapping("/user-info")
    public UserInfo getUserInfo() {
        return userService.getUserInfoByUser(getPrincipalUser());
    }

    @GetMapping("/cart")
    public CartItem getCart() {
        User user = getPrincipalUser();
        return cartService.getCartItem(user);
    }

    @DeleteMapping("/cart")
    public CartItem deleteCart() {
        return cartService.deleteCart(getPrincipalUser());
    }

    @PostMapping("/cart/cart-detail")
    public CartItem createCartDetail(@Valid @RequestBody CartDetailRequest detailRequest) {
        User user = getPrincipalUser();
        return cartService.addToCart(user, detailRequest);
    }

    @DeleteMapping("/cart/cart-detail/{cartDetailId}")
    public CartItem deleteCartDetail(@PathVariable("cartDetailId") long cartDetailId) {
        User user = getPrincipalUser();
        return cartService.removeFromCart(user, cartDetailId);
    }

    @GetMapping("/highlights")
    public List<Highlight> getAllHighlights() {
        return highlightService.getAllHighlights();
    }

    @PostMapping("/checkout")
    public OrderItem createOrder(@Valid @RequestBody List<OrderRequest> orderRequest) {
        User user = getPrincipalUser();
        OrderItem orderItem = orderService.createOrder(user, orderRequest);
        mailSenderService.sendSimpleMailToAdminCloud(orderItem);
        return orderItem;
    }

    @GetMapping("/order-history")
    public List<OrderItem> getOrderHistory(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                           @RequestParam(value = "limit", defaultValue = "0") int limit) {
        User user = getPrincipalUser();
        return orderService.getOrderHistory(user, offset, limit);
    }

    @GetMapping("/order-items")
    public List<OrderItemDto> getOnlyOrderItems(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                                @RequestParam(value = "limit", defaultValue = "0") int limit) {
        User user = getPrincipalUser();
        List<OrderItem> orderHistory = orderService.getOrderHistory(user, offset, limit);
        return orderHistory.stream().map(Utils::mapToOrderItemDto).collect(Collectors.toList());
    }

    @GetMapping("/order-items/{itemId}")
    public List<OrderDetailDto> getOrderDetail(@PathVariable("itemId") long orderId) {
        User user = getPrincipalUser();
        OrderItem orderItem = orderService.getOrderItem(user, orderId);
        return orderItem.getOrderDetails().stream().map(Utils::mapToOrderDetailDto).collect(Collectors.toList());
    }

    //add now available products to cart from orderItem
    @PostMapping("/reorder/{itemId}")
    public CartItem reOrderItem(@PathVariable("itemId") long itemId) {
        User user = getPrincipalUser();
        return orderService.reOrderItem(user, itemId);
    }

    @GetMapping("/categories")
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/categories/{categoryName}")
    public Category getCategoryByName(@PathVariable String categoryName) {
        return categoryService.getCategoryByName(categoryName);
    }

    @GetMapping("/categories/{categoryName}/products")
    public List<Product> getAllProductsOfCategory(@PathVariable("categoryName") String categoryName) {
        return categoryService.getAllProductsOfCategoryName(categoryName);
    }

    @GetMapping("/categories/{categoryName}/products/{productId}")
    public Product getSingleProduct(@PathVariable("categoryName") String categoryName,
                                    @PathVariable("productId") long productId) {
        return productService.getProductUnderCategory(categoryName, productId);
    }
}
