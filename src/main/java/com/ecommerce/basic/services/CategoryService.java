package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.InvalidResourceName;
import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Category;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.ecommerce.basic.utils.Utils.validateBean;

/**
 * @author Shrikant Sharma
 */

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;

	public Category createCategory(Category category) {
		validateBean(category);
		return categoryRepository.save(category);
	}

	public Category getCategoryByID(int categoryId) {
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		optionalCategory.orElseThrow(() -> new NoSuchResourceException(CategoryService.class, "No category found for categoryID: "+categoryId));
		return optionalCategory.get();
	}

	public Category getCategoryByName(String categoryName) {
		Optional<Category> optionalCategory = categoryRepository.findByCategoryName(categoryName);
		optionalCategory.orElseThrow(() -> new NoSuchResourceException(CategoryService.class, "No category found for categoryName: "+categoryName));
		return optionalCategory.get();
	}
	public List<Product> getAllProductsOfCategoryName(String categoryName) {
		return getCategoryByName(categoryName).getProducts();
	}

	public List<Category> getAllCategories() {
		return categoryRepository.findAll();
	}

	public Category updateCategory(String categoryName, Category newCategory) {
		Category category = getCategoryByName(categoryName);
		category.setCategoryName(newCategory.getCategoryName());
		validateBean(category);
		return categoryRepository.save(category);
	}

	public Category deleteCategory(String categoryName) {
		Category category = getCategoryByName(categoryName);
		categoryRepository.delete(category);
		return category;
	}
}
