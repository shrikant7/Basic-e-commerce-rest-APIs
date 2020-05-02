package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.Highlight;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Shrikant Sharma
 */
public interface HighlightRepository extends JpaRepository<Highlight, Integer> {
}
