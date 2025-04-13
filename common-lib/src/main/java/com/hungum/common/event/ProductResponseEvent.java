package com.hungum.common.event;

import com.hungum.common.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductResponseEvent {
    private String sku;
    private String name;
    private BigDecimal price;

    public ProductResponseEvent(ProductDto productDto) {
        this.sku = productDto.getSku();
        this.name = productDto.getProductName();
        this.price = productDto.getPrice();
    }
}
