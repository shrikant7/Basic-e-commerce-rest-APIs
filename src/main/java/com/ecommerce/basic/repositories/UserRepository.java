package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Shrikant Sharma
 */
public interface UserRepository extends JpaRepository<User, Integer>{
	Optional<User> findByUsername(String username);
}
