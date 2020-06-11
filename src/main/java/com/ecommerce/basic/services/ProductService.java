package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.InvalidResourceName;
import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.ecommerce.basic.utils.Utils.validateBean;

/**
 * @author Shrikant Sharma
 */

@Service
public class ProductService {
	@Autowired
	ProductRepository productRepository;

	public Product createProduct(Product product) {
		validateBean(product);
		if(product.getYourPrice() > product.getMrpPrice()) {
			throw new InvalidResourceName(ProductService.class, "YourPrice cannot be greater than MrpPrice");
		}
		return productRepository.save(product);
	}

	public Product getProductById(int productId) {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		optionalProduct.orElseThrow(() -> new NoSuchResourceException(ProductService.class, "No product found for productId: " + productId));
		return optionalProduct.get();
	}

	public Product getProductByCategory(String categoryName, int productId) {
		Product product = getProductById(productId);
		if(!product.getCategory().getCategoryName().equalsIgnoreCase(categoryName)) {
			throw new NoSuchResourceException(ProductService.class, "Product does not belongs to category: "+categoryName);
		}
		return product;
	}

	public Product updateSingleProduct(Product product, Product newProduct) {
		product.setCategory(newProduct.getCategory())
				.setName(newProduct.getName())
				.setDescription(newProduct.getDescription())
				.setMrpPrice(newProduct.getMrpPrice())
				.setYourPrice(newProduct.getYourPrice())
				.setImageUri(newProduct.getImageUri());

		validateBean(product);
		productRepository.save(product);
		return product;
	}

	public Product deleteProductByCategory(String categoryName, int productId) {
		Product product = getProductByCategory(categoryName, productId);
		productRepository.delete(product);
		return product;
	}
}
