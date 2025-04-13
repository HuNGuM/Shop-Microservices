package com.hungum.product.controller;

import com.hungum.common.dto.ProductRatingDto;
import com.hungum.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/products/ratings")
@AllArgsConstructor
public class ProductRatingController {

    private final ProductService productService;

    @PostMapping("/submit")
    public void postRating(@Valid @RequestBody ProductRatingDto productRatingDto) {
        productService.postProductRating(productRatingDto);
    }

    @PutMapping("/edit")
    public void editRating(@Valid @RequestBody ProductRatingDto productRatingDto) {
        productService.editProductRating(productRatingDto);
    }

    @DeleteMapping("/delete/{ratingId}")
    public void deleteRating(@PathVariable String ratingId) {
        productService.deleteProductRating(ratingId);
    }


    @GetMapping("/get/{sku}")
    public ResponseEntity<List<ProductRatingDto>> getRating(@PathVariable String sku) {
        return new ResponseEntity<>(productService.getProductRating(sku), OK);
    }
}
