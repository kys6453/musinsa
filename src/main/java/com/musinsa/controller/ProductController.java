package com.musinsa.controller;

import com.musinsa.model.dto.request.ManipulateProductRequest;
import com.musinsa.model.dto.response.MinAndMaxByCategoryResponse;
import com.musinsa.model.dto.response.MinByCategoryAndBrandResponse;
import com.musinsa.model.dto.response.MinForAllByBrandResponse;
import com.musinsa.model.entity.Product;
import com.musinsa.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/products")
@Tag(name = "MUSINSA API", description = "[MUSINSA] Java(Kotlin) Backend Engineer 과제")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/min-by-category-brand")
    @Operation(
            summary = "구현 1",
            description = "카테고리 별 최저가격 브랜드와 상품 가격, 총액을 조회하는 API"
    )
    public MinByCategoryAndBrandResponse findMinimumPriceByCategoryAndBrand() {
        return productService.findMinimumPriceByCategoryAndBrand();
    }

    @GetMapping("/min-for-all-by-brand")
    @Operation(
            summary = "구현 2",
            description = "단일 브랜드로 모든 카테고리 상품을 구매할 때 최저가격에 판매하는 브랜드와 카테고리의 상품가격, 총액을 조회하는 API"
    )
    public MinForAllByBrandResponse findMinimumPriceForAllByBrand() {
        return productService.findMinimumPriceForAllByBrand();
    }

    @GetMapping("/min-max-by-category")
    @Operation(
            summary = "구현 3",
            description = "카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회하는 API"
    )
    public MinAndMaxByCategoryResponse findMinimumAndMaximumPriceByCategory(
            @RequestParam String category) {
        return productService.findMinimumAndMaximumPriceByCategory(category);
    }

    @PostMapping
    @Operation(
            summary = "구현 4",
            description = "브랜드 및 상품을 추가 / 업데이트 / 삭제하는 API"
    )
    public ResponseEntity<Product> manipulateProduct(
            @RequestParam String command,
            @RequestBody ManipulateProductRequest request) {
        Product product;

        return switch (command.toLowerCase()) {
            case "add" -> {
                product = productService.insertProduct(request);
                yield ResponseEntity.status(HttpStatus.CREATED).body(product);
            }
            case "update" -> {
                product = productService.updateProduct(request);
                yield ResponseEntity.ok(product);
            }
            case "delete" -> {
                product = productService.deleteProduct(request);
                yield ResponseEntity.ok(product);
            }
            // add, update, delete가 아닌 다른 command가 올 경우 에러 응답
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Command is invalid -> " + command);
        };
    }
}
