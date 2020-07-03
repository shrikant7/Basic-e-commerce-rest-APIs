package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.Highlight;
import com.ecommerce.basic.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Repository
@Transactional
public interface HighlightRepository extends JpaRepository<Highlight, Long> {
    int deleteByHighlightedProduct(Product product);

    @Modifying
    @Query(value = "DELETE FROM Highlight h WHERE h.highlightedProduct IN :products")
    int deleteHighlightsHaveProducts(@Param("products") List<Product> products);
}
