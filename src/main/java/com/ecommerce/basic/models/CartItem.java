package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Shrikant Sharma
 */

@Entity @Table
@Data @Accessors(chain = true)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class CartItem {
	@Id
	@GeneratedValue
	private Long cartId;

	@JsonIgnore
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId")
	private User user;

	private LocalDateTime lastModified;
	private long totalValue;

	@OneToMany(mappedBy = "cartItem", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<CartDetail> cartDetails;
}
