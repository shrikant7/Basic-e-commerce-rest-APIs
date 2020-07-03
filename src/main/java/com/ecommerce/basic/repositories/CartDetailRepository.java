package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.CartDetail;
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
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    @Modifying
    @Query(value = "DELETE FROM CartDetail c WHERE c.product IN :products")
    int deleteCartDetailsHaveProducts(@Param("products") List<Product> products);

    //above can be done by JPA deleteBy... query derivation
    //int deleteByProduct(Product product);
}
