package com.musinsa.model.dto.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MinByCategoryAndBrandDTO {
    @JsonProperty("카테고리")
    private String category;
    @JsonProperty("브랜드")
    private String brandName;
    @JsonProperty("가격")
    private Integer price;

    public MinByCategoryAndBrandDTO(String category, String brandName, Integer price) {
        this.category = category;
        this.brandName = brandName;
        this.price = price;
    }
}
