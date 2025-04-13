package com.hungum.product.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRating {
    private String id;
    @Min(1)
    @Max(5)
    private BigDecimal ratingStars;
    private String productId;
    private String elasticSearchProductId;
    private String review;
    private String userName;
}
