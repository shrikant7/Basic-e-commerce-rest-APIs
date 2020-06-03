package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.Otp;
import com.ecommerce.basic.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author Shrikant Sharma
 */
public interface OtpRepository extends JpaRepository<Otp,Long> {
	Optional<Otp> findByUser(User user);
}
