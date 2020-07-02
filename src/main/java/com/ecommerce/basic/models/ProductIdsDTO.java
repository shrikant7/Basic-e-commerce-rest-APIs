package com.ecommerce.basic.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data @Accessors(chain = true)
public class ProductIdsDTO {
    List<Long> productIds;
}
