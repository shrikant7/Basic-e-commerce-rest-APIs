package com.ecommerce.basic.services;

import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
