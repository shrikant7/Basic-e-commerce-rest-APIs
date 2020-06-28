package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.CartItem;
import com.ecommerce.basic.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Shrikant Sharma
 */

@Repository
public interface CartRepository extends JpaRepository<CartItem, Long> {
    CartItem findByUser(User user);
}
