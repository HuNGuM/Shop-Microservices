package com.hungum.common.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddToCartEvent {
    private String username;
    private String sku;
    private int quantity;
}
