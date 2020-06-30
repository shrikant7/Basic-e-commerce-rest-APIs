package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.ErrorConstant;
import com.ecommerce.basic.exceptions.InvalidResourceName;
import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.ecommerce.basic.exceptions.ErrorConstant.ErrorCode.*;
import static com.ecommerce.basic.utils.Utils.validateBean;

/**
 * @author Shrikant Sharma
 */

@Service
public class ProductService {
	@Autowired
	CartService cartService;
	@Autowired
	HighlightService highlightService;
	@Autowired
	ProductRepository productRepository;
	@Autowired
	ImageStorageService imageStorageService;

	public Product createProduct(Product product) {
		//delete the stored image if validation failed
		Runnable deleteImageRunnable = () -> imageStorageService.deleteImage(product.getImageUri());
		validateBean(product, deleteImageRunnable);
		if(product.getYourPrice() > product.getMrpPrice()) {
			deleteImageRunnable.run();
			throw new InvalidResourceName(INVALID_YOUR_PRICE, "YourPrice cannot be greater than MrpPrice");
		}
		return productRepository.saveAndFlush(product);
	}

	public Product getProductById(long productId) {
		Optional<Product> optionalProduct = productRepository.findById(productId);
		optionalProduct.orElseThrow(() -> new NoSuchResourceException(NO_PRODUCT_ID_EXCEPTION, "No product found for productId: " + productId));
		Product product = optionalProduct.get();
		if(product.isDeleted()) {
			throw new NoSuchResourceException(DELETED_PRODUCT_EXCEPTION, "Product:"+productId+" is deleted");
		}
		return product;
	}

	public Product getProductUnderCategory(String categoryName, long productId) {
		Product product = getProductById(productId);
		if(!product.getCategory().getCategoryName().equalsIgnoreCase(categoryName)) {
			throw new NoSuchResourceException(NO_PRODUCT_IN_CATEGORY_EXCEPTION, "Product does not belongs to category: "+categoryName);
		}
		return product;
	}

	public Product updateSingleProduct(Product product, Product newProduct) {
		product.setCategory(newProduct.getCategory())
				.setName(newProduct.getName())
				.setDescription(newProduct.getDescription())
				.setMrpPrice(newProduct.getMrpPrice())
				.setYourPrice(newProduct.getYourPrice())
				.setImageUri(newProduct.getImageUri())
				//product cannot be marked deleted in update query
				.setDeleted(false);

		validateBean(product);
		productRepository.saveAndFlush(product);
		return product;
	}

	//soft deleting products; marking them deleted
	public Product deleteProductByCategory(String categoryName, long productId) {
		Product product = getProductUnderCategory(categoryName, productId);
		/* will update cart and highlight in next fetch by user

		//delete cartDetail if this product is embedded in it
		cartService.deleteProductFromAnyCartDetail(product);

		//delete highlight if this product is embedded in it
		highlightService.deleteProductFromAnyHighlight(product);*/

		//mark product as deleted
		product.setDeleted(true);
		productRepository.flush();
		return product;
	}

	public void markDeletedAllProducts(List<Product> products) {
		int marked = productRepository.markAllProductDeleted(products);
		System.err.println(marked+" products marked deleted");
	}
}
