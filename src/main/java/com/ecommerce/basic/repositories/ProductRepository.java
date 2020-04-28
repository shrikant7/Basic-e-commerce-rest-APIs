package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Shrikant Sharma
 */
public interface ProductRepository extends JpaRepository<Product, Integer> {
}
