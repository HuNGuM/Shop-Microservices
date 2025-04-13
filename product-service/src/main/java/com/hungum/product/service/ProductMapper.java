package com.hungum.product.service;

import com.hungum.common.dto.ProductRatingDto;
import com.hungum.product.model.ProductRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {


    ProductRating mapProductRatingDto(ProductRatingDto productRatingDto);

    @Mapping(source = "id", target = "ratingId")
    ProductRatingDto mapProductRating(ProductRating productRating);
}
