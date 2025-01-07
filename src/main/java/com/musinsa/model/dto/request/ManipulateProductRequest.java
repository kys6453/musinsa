package com.musinsa.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ManipulateProductRequest {
    private Long id;
    @JsonProperty("brand_name")
    private String brandName;
    private String category;
    private Integer price;
}
