package com.hungum.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SearchQueryDto {
    private String textQuery;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<Filter> filters;

    @Data
    public static class Filter implements Serializable {
        private String key;
        private String value;
        private String from;
        private String to;
    }
}
