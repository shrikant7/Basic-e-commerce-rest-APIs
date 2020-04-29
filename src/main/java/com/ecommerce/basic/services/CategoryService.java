package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Category;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author Shrikant Sharma
 */

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;


	public Category createCategory(Category category) {
		return categoryRepository.save(category);
	}

	public Category getCategoryByID(int categoryId) {
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		optionalCategory.orElseThrow(() -> new NoSuchResourceException(CategoryService.class, "no category found for categoryID: "+categoryId));
		return optionalCategory.get();
	}

	public List<Product> getAllProductsOfCategoryId(int categoryId) {
		return getCategoryByID(categoryId).getProducts();
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}
}