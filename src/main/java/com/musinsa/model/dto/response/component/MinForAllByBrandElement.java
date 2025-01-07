package com.musinsa.model.dto.response.component;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.model.dto.db.MinForAllByBrandDTO;
import lombok.Data;

import java.util.List;

@Data
public class MinForAllByBrandElement {
    @JsonProperty("브랜드")
    private String brandName;
    @JsonProperty("카테고리")
    private List<MinForAllByBrandDTO> productList;
    @JsonProperty("총액")
    private Integer totalSum;

    public MinForAllByBrandElement(String brandName, List<MinForAllByBrandDTO> productList, Integer totalSum) {
        this.brandName = brandName;
        this.productList = productList;
        this.totalSum = totalSum;
    }
}
