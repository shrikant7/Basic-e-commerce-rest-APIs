package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.CartDetail;
import com.ecommerce.basic.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Shrikant Sharma
 */

@Repository
@Transactional
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
    @Modifying
    @Query(value = "DELETE FROM CartDetail WHERE product = :product")
    int deleteByProductQuery(@Param("product") Product product);

    //above can be done by JPA deleteBy... query derivation
    //int deleteByProduct(Product product);
}
