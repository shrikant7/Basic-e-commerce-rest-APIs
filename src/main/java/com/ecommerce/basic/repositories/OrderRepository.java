package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.OrderItem;
import com.ecommerce.basic.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Repository
public interface OrderRepository extends JpaRepository<OrderItem,Long> {
	@Query("SELECT o FROM OrderItem o JOIN FETCH o.orderDetails od JOIN FETCH od.product WHERE o.user=:user")
	List<OrderItem> getOrderItemWithDetailByUser(@Param("user") User user);
}
