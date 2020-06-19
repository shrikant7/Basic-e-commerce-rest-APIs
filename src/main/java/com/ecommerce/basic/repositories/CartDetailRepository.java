package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.CartDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shrikant Sharma
 */

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long> {
}
