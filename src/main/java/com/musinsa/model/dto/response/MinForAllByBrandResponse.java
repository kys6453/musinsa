package com.musinsa.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.model.dto.response.component.MinForAllByBrandElement;
import lombok.Data;

import java.util.List;

@Data
public class MinForAllByBrandResponse {
    @JsonProperty("최저가")
    private List<MinForAllByBrandElement> minPrice;

    public MinForAllByBrandResponse(List<MinForAllByBrandElement> minPrice) {
        this.minPrice = minPrice;
    }
}
