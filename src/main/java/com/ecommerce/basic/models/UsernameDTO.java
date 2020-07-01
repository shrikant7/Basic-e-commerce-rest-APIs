package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

@Data @Accessors(chain = true)
public class UsernameDTO {
    @NotBlank(message = "Username can't be blank")
    String username;
}
