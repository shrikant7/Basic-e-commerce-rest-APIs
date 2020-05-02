package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Shrikant Sharma
 */
public interface OrderRepository extends JpaRepository<OrderItem,Integer> {
}
