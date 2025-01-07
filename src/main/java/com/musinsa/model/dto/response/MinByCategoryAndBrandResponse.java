package com.musinsa.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.model.dto.db.MinByCategoryAndBrandDTO;
import lombok.Data;

import java.util.List;

@Data
public class MinByCategoryAndBrandResponse {
    @JsonProperty("최저가")
    private List<MinByCategoryAndBrandDTO> productList;
    @JsonProperty("총액")
    private Integer totalSum;

    public MinByCategoryAndBrandResponse(List<MinByCategoryAndBrandDTO> productList, Integer totalSum) {
        this.productList = productList;
        this.totalSum = totalSum;
    }
}
