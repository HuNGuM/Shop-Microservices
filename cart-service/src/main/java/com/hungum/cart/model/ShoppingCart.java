package com.hungum.cart.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.math.BigDecimal;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("Cart")
public class ShoppingCart {
    @Id
    private String id;
    private Set<ShoppingCartItem> shoppingCartItems;
    private BigDecimal cartTotalPrice;
    private int numberOfItems;
    private String username;
}
