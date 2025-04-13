package com.hungum.cart.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShoppingCartItem {
    private String name;
    private BigDecimal price;

    public ShoppingCartItem(String name, BigDecimal price) {
        this.name = name;
        this.price = price;
    }

    public ShoppingCartItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}

