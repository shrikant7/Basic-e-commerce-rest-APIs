package com.ecommerce.basic.models;

import lombok.Value;

@Value
public class UserWithInfoDto {

    long id;
    String username;
    boolean active;
    String roles;
    UserInfo userInfo;
}
