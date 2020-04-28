package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NotFound;
import com.ecommerce.basic.models.Category;
import com.ecommerce.basic.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		optionalCategory.orElseThrow(() -> new NotFound(CategoryService.class, "no category found on categoryID: "+categoryId));
		return optionalCategory.get();
	}
}
