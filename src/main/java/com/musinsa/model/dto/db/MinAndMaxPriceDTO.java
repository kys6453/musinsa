package com.musinsa.model.dto.db;

import lombok.Data;

@Data
public class MinAndMaxPriceDTO {
    private String brandName;
    private Integer price;
    private String priceType;

    public MinAndMaxPriceDTO(String brandName, Integer price, String priceType) {
        this.brandName = brandName;
        this.price = price;
        this.priceType = priceType;
    }
}
