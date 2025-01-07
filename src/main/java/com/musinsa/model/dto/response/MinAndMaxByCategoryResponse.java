package com.musinsa.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.musinsa.model.dto.db.MinAndMaxPriceDTO;
import com.musinsa.model.dto.response.component.PriceDTO;
import lombok.Data;

import java.util.List;

@Data
public class MinAndMaxByCategoryResponse {
    @JsonProperty("카테고리")
    private String category;
    @JsonProperty("최저가")
    private List<PriceDTO> minPriceByCategory;
    @JsonProperty("최고가")
    private List<PriceDTO> maxPriceByCategory;

    public MinAndMaxByCategoryResponse(String category, List<PriceDTO> minPriceByCategory, List<PriceDTO> maxPriceByCategory) {
        this.category = category;
        this.minPriceByCategory = minPriceByCategory;
        this.maxPriceByCategory = maxPriceByCategory;
    }
}
