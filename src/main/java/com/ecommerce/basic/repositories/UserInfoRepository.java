package com.ecommerce.basic.repositories;

import com.ecommerce.basic.models.User;
import com.ecommerce.basic.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    UserInfo findByUser(User user);
}
