package com.ecommerce.basic.repositories;

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
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Modifying
    @Query(value = "UPDATE Product p SET p.deleted=true WHERE p IN :products")
    int markAllProductDeleted(@Param("products") List<Product> products);
}
