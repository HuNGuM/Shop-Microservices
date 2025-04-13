package com.hungum.product.service;

import com.hungum.common.dto.*;
import com.hungum.common.dto.SearchQueryDto.Filter;
import com.hungum.common.exceptions.ShopException;
import com.hungum.product.model.Category;
import com.hungum.product.model.Product;
import com.hungum.common.dto.ProductAttribute;
import com.hungum.product.repository.CategoryRepository;
import com.hungum.product.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class SearchService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductSearchResponseDto searchWithFilters(SearchQueryDto searchQueryDto, String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new ShopException("Category - " + categoryName + " Not Found"));

        List<Product> products = productRepository.findByCategory(category);
        List<Product> filteredProducts = applyFilters(products, searchQueryDto);

        return buildSearchResponse(filteredProducts);
    }

    public ProductSearchResponseDto search(SearchQueryDto searchQueryDto) {
        List<Product> products = productRepository.findAll();
        List<Product> filteredProducts = applyFilters(products, searchQueryDto);

        return buildSearchResponse(filteredProducts);
    }

    private List<Product> applyFilters(List<Product> products, SearchQueryDto searchQueryDto) {
        return products.stream()
                .filter(p -> p.getPrice().compareTo(searchQueryDto.getMinPrice()) >= 0)
                .filter(p -> p.getPrice().compareTo(searchQueryDto.getMaxPrice()) <= 0)
                .filter(p -> {
                    for (Filter filter : searchQueryDto.getFilters()) {
                        boolean match = p.getProductAttributeList().stream().anyMatch(attr ->
                                        attr.getAttributeName().equalsIgnoreCase(filter.getKey()) &&
                                        attr.getAttributeValue().equalsIgnoreCase(filter.getValue()));
                        if (!match) return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private ProductSearchResponseDto buildSearchResponse(List<Product> products) {
        List<ProductDto> productDtos = products.stream().map(this::mapToDto).collect(Collectors.toList());

        BigDecimal minPrice = products.stream().map(Product::getPrice).min(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        BigDecimal maxPrice = products.stream().map(Product::getPrice).max(Comparator.naturalOrder()).orElse(BigDecimal.ZERO);
        List<FacetDto> facets = extractFacets(products);

        return new ProductSearchResponseDto(productDtos, minPrice, maxPrice, facets);
    }

    private ProductDto mapToDto(Product product) {
        return new ProductDto(
                product.getName(),
                product.getImageUrl(),
                product.getSku(),
                product.getPrice(),
                product.getDescription(),
                product.getManufacturer(),
                new ProductAvailability("In Stock", "forestgreen"),
                product.getProductAttributeList(),
                product.isFeatured(),
                Collections.emptyList()
        );
    }

    private List<FacetDto> extractFacets(List<Product> products) {
        Map<String, Map<String, Integer>> facetMap = new HashMap<>();

        for (Product product : products) {
            for (ProductAttribute attr : product.getProductAttributeList()) {
                String name = attr.getAttributeName();
                String value = attr.getAttributeValue();
                facetMap.computeIfAbsent(name, k -> new HashMap<>())
                        .merge(value, 1, Integer::sum);
            }
        }

        List<FacetDto> facetDtos = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : facetMap.entrySet()) {
            List<FacetValueDto> values = entry.getValue().entrySet().stream()
                    .map(e -> new FacetValueDto(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            facetDtos.add(new FacetDto(entry.getKey(), values));
        }
        return facetDtos;
    }
}
