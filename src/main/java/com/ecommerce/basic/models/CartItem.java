package com.ecommerce.basic.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CartItem {
	@Id
	@GeneratedValue
	private int cartId;

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
