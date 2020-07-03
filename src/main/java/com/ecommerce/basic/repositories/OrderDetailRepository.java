package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.OrderDetail;
import com.ecommerce.basic.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findAllByProductIn(List<Product> deletedMarkedProducts);
}
