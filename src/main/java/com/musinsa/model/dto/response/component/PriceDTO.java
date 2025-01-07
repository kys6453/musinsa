package com.musinsa.model.dto.response.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PriceDTO {
    @JsonProperty("브랜드")
    private String brandName;
    @JsonProperty("가격")
    private Integer price;

    public PriceDTO(String brandName, Integer price) {
        this.brandName = brandName;
        this.price = price;
    }
}
