package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author Shrikant Sharma
 */

@Service
public class ProductService {
	@Autowired
	ProductRepository productRepository;

	public Product createProduct(Product product) {
		return productRepository.save(product);
	}

	public Product getProductById(int productId) {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		optionalProduct.orElseThrow(() -> new NoSuchResourceException(ProductService.class, "No product found for productId: " + productId));
		return optionalProduct.get();
	}
}
