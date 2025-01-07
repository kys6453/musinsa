package com.musinsa.model.dto.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MinForAllByBrandDTO {
    @JsonIgnore
    private String brandName;
    @JsonProperty("카테고리")
    private String category;
    @JsonProperty("가격")
    private Integer price;

    public MinForAllByBrandDTO(String brandName, String category, Integer price) {
        this.brandName = brandName;
        this.category = category;
        this.price = price;
    }
}
