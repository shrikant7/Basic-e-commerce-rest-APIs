package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.Highlight;
import com.ecommerce.basic.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Shrikant Sharma
 */
public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    int deleteByHighlightedProduct(Product product);
}
