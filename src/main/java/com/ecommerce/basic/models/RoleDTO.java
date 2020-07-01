package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.Pattern;

@Data @Accessors(chain = true)
public class RoleDTO {
    @Pattern(regexp = "ROLE_USER|ROLE_ADMIN", message = "Role should be in [ROLE_USER/ROLE_ADMIN]")
    String role;
}
