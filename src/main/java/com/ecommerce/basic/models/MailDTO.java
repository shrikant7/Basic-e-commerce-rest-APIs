package com.ecommerce.basic.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author Shrikant Sharma
 */

@Data @Accessors(chain = true)
@AllArgsConstructor @NoArgsConstructor
public class MailDTO {
	String email;
}
