package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shrikant Sharma
 */
public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
