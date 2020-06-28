package com.ecommerce.basic.services;

import com.ecommerce.basic.exceptions.NoSuchResourceException;
import com.ecommerce.basic.models.Category;
import com.ecommerce.basic.models.Product;
import com.ecommerce.basic.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ecommerce.basic.utils.Utils.validateBean;

/**
 * @author Shrikant Sharma
 */

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private ProductService productService;

	public Category createCategory(Category category) {
		validateBean(category);
		return categoryRepository.save(category);
	}

	public Category getCategoryByID(long categoryId) {
		Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
		optionalCategory.orElseThrow(() -> new NoSuchResourceException(CategoryService.class, "No category found for categoryID: "+categoryId));
		Category category = optionalCategory.get();
		if(category.isDeleted()) {
			throw new NoSuchResourceException(CategoryService.class, "Category:"+categoryId+" is deleted");
		}
		return category;
	}

	public Category getCategoryByName(String categoryName) {
		Optional<Category> optionalCategory = categoryRepository.findByCategoryName(categoryName);
		optionalCategory.orElseThrow(() -> new NoSuchResourceException(CategoryService.class, "No category found for categoryName: "+categoryName));
		Category category = optionalCategory.get();
		if(category.isDeleted()) {
			throw new NoSuchResourceException(CategoryService.class, "Category:"+categoryName+" is deleted");
		}
		return category;
	}
	public List<Product> getAllProductsOfCategoryName(String categoryName) {
		return getCategoryByName(categoryName).getProducts();
	}

	public List<Category> getAllCategories() {
		List<Category> categories = categoryRepository.findAll();
		//return non deleted categories
		return categories.stream().filter(c->!c.isDeleted()).collect(Collectors.toList());
	}

	public Category updateCategory(String categoryName, Category newCategory) {
		Category category = getCategoryByName(categoryName);
		category.setCategoryName(newCategory.getCategoryName())
				//cannot mark category deleted in update
				.setDeleted(false);
		validateBean(category);
		return categoryRepository.save(category);
	}

	//soft deleting category and its products
	public Category deleteCategory(String categoryName) {
		Category category = getCategoryByName(categoryName);
		// marking all products of category deleted
		productService.deleteAllProducts(category.getProducts());

		//changing category name to random so that user can use this name again and be safe from Unique name constraint
		category.setCategoryName(getRandomName());
		category.setDeleted(true);
		categoryRepository.flush();
		//categoryRepository.delete(category);
		return category;
	}

	private String getRandomName() {
		return String.valueOf(System.currentTimeMillis());
	}
}
