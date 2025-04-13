package com.hungum.product.service;

import com.hungum.common.dto.ProductAvailability;
import com.hungum.common.dto.ProductDto;
import com.hungum.common.dto.ProductRatingDto;
import com.hungum.common.exceptions.ShopException;
import com.hungum.product.model.Category;
import com.hungum.product.model.Product;
import com.hungum.product.model.ProductRating;
import com.hungum.product.repository.CategoryRepository;
import com.hungum.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Service
@AllArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Cacheable(value = "products1")
    public List<ProductDto> findAll() {
        List<Product> all = productRepository.findAll();
        log.info("Found {} products..", all.size());
        return all.stream()
                .map(this::mapToDto)
                .collect(toList());
    }

    private ProductDto mapToDto(Product product) {
        ProductAvailability productAvailability = product.getQuantity() > 0 ? inStock() : outOfStock();
        if (product.getProductRating() != null) {
            List<ProductRatingDto> productRatingDtoList = product.getProductRating().stream().map(productMapper::mapProductRating).collect(toList());
            return new ProductDto(product.getName(), product.getImageUrl(), product.getSku(), product.getPrice(), product.getDescription(), product.getManufacturer(), productAvailability, product.getProductAttributeList(), product.isFeatured(), productRatingDtoList);
        } else {
            return new ProductDto(product.getName(), product.getImageUrl(), product.getSku(), product.getPrice(), product.getDescription(), product.getManufacturer(), productAvailability, product.getProductAttributeList(), product.isFeatured(), emptyList());
        }
    }

    private ProductAvailability outOfStock() {
        return new ProductAvailability("Out of Stock", "red");
    }

    private ProductAvailability inStock() {
        return new ProductAvailability("In Stock", "forestgreen");
    }

    @Cacheable(value = "singleProduct", key = "#sku")
    public ProductDto readOneProduct(String sku) {
        log.info("Reading Product with productName - {}", sku);
        Optional<Product> optionalProduct = productRepository.findBySku(sku);
        Product product = optionalProduct.orElseThrow(IllegalArgumentException::new);
        return mapToDto(product);
    }

    @Cacheable(value = "productsByCategory")
    public List<ProductDto> findByCategoryName(String categoryName) {
        log.info("Reading Products belonging to category- {}", categoryName);
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);
        Category category = categoryOptional.orElseThrow(() -> new IllegalArgumentException("Category Not Found"));

        List<Product> products = productRepository.findByCategory(category);
        log.info("Found {} products in category", products.size());
        return products.stream().map(this::mapToDto).collect(toList());
    }

    public void save(ProductDto productDto) {
        Product product = new Product();
        product.setName(productDto.getProductName());
        product.setSku(productDto.getSku());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());

        productRepository.save(product);
    }

    public void postProductRating(ProductRatingDto productRatingDto) {
        Supplier<ShopException> shopExceptionSupplier = () -> new ShopException("No Product exists with sku - " + productRatingDto.getSku());
        Product product = productRepository.findBySku(productRatingDto.getSku()).orElseThrow(shopExceptionSupplier);

        ProductRating productRating = productMapper.mapProductRatingDto(productRatingDto);
        productRating.setId(UUID.randomUUID().toString());
        productRating.setProductId(product.getId());
        List<ProductRating> productRatingList = product.getProductRating() == null ? new ArrayList<>() : product.getProductRating();
        productRatingList.add(productRating);
        product.setProductRating(productRatingList);

        productRepository.save(product);
    }

    public void editProductRating(ProductRatingDto productRatingDto) {
        Supplier<ShopException> shopExceptionSupplier = () -> new ShopException("No Product exists with sku - " + productRatingDto.getSku());
        Product product = productRepository.findBySku(productRatingDto.getSku()).orElseThrow(shopExceptionSupplier);

        List<ProductRating> productRatingList = product.getProductRating();
        ProductRating productRating = productRatingList.stream().filter(rating -> rating.getId().equals(productRatingDto.getRatingId())).findFirst().orElseThrow(() -> new ShopException("No Rating found with id - " + productRatingDto.getRatingId()));
        productRating.setRatingStars(productRatingDto.getRatingStars());
        productRating.setReview(productRatingDto.getReview());

        productRepository.save(product);
    }

    public void deleteProductRating(String sku) {
        Product product = productRepository.findBySku(sku)
                .orElseThrow(() -> new ShopException("No Product exists with sku - " + sku));
        product.setProductRating(null);
        productRepository.save(product);
    }


    public List<ProductRatingDto> getProductRating(String sku) {
        Supplier<ShopException> shopExceptionSupplier = () -> new ShopException("No Product exists with sku - " + sku);
        Product product = productRepository.findBySku(sku).orElseThrow(shopExceptionSupplier);
        return product.getProductRating().stream().map(productMapper::mapProductRating).collect(toList());
    }
}
