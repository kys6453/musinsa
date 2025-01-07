package com.musinsa.integartion;

import com.musinsa.model.dto.db.MinByCategoryAndBrandDTO;
import com.musinsa.model.dto.db.MinForAllByBrandDTO;
import com.musinsa.model.dto.request.ManipulateProductRequest;
import com.musinsa.model.dto.response.MinAndMaxByCategoryResponse;
import com.musinsa.model.dto.response.MinByCategoryAndBrandResponse;
import com.musinsa.model.dto.response.MinForAllByBrandResponse;
import com.musinsa.model.dto.response.component.MinForAllByBrandElement;
import com.musinsa.model.dto.response.component.PriceDTO;
import com.musinsa.model.entity.Product;
import com.musinsa.repository.ProductRepository;
import com.musinsa.service.ProductService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class IntegrationTest {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void test_find_minimum_price_by_category_and_brand() {
        MinByCategoryAndBrandResponse response = productService.findMinimumPriceByCategoryAndBrand();

        MinByCategoryAndBrandResponse expectedResponse = new MinByCategoryAndBrandResponse(
                List.of(
                    new MinByCategoryAndBrandDTO("top", "C", 10000),
                    new MinByCategoryAndBrandDTO("outer", "E", 5000),
                    new MinByCategoryAndBrandDTO("pants", "D", 3000),
                    new MinByCategoryAndBrandDTO("sneakers", "A", 9000),
                    new MinByCategoryAndBrandDTO("sneakers", "G", 9000),
                    new MinByCategoryAndBrandDTO("bag", "A", 2000),
                    new MinByCategoryAndBrandDTO("hat", "D", 1500),
                    new MinByCategoryAndBrandDTO("socks", "I", 1700),
                    new MinByCategoryAndBrandDTO("accessory", "F", 1900)
                ), 43100
        );

        assertEquals(expectedResponse.getProductList(), response.getProductList());
        assertEquals(expectedResponse.getTotalSum(), response.getTotalSum());
    }

    @Test
    public void test_find_minimum_price_for_all_by_brand() {
        MinForAllByBrandResponse response = productService.findMinimumPriceForAllByBrand();

        MinForAllByBrandResponse expectedResponse = new MinForAllByBrandResponse(
                List.of(
                      new MinForAllByBrandElement(
                              "D",
                              List.of(
                                      new MinForAllByBrandDTO("D", "top", 10100),
                                      new MinForAllByBrandDTO("D", "outer", 5100),
                                      new MinForAllByBrandDTO("D", "pants", 3000),
                                      new MinForAllByBrandDTO("D", "sneakers", 9500),
                                      new MinForAllByBrandDTO("D", "bag", 2500),
                                      new MinForAllByBrandDTO("D", "hat", 1500),
                                      new MinForAllByBrandDTO("D", "socks", 2400),
                                      new MinForAllByBrandDTO("D", "accessory", 2000)

                              ),
                              36100
                      )
                )
        );

        assertEquals(expectedResponse.getMinPrice(), response.getMinPrice());
    }

    @Test
    public void test_find_minimum_and_maximum_price_by_category() {
        String category = "top";

        MinAndMaxByCategoryResponse response = productService.findMinimumAndMaximumPriceByCategory(category);

        MinAndMaxByCategoryResponse expectedResponse = new MinAndMaxByCategoryResponse(
                category,
                List.of(
                        new PriceDTO("C", 10000)
                ),
                List.of(
                        new PriceDTO("I", 11400)
                )
        );

        assertEquals(response.getCategory(), expectedResponse.getCategory());
        assertEquals(response.getMinPriceByCategory(), expectedResponse.getMinPriceByCategory());
        assertEquals(response.getMaxPriceByCategory(), expectedResponse.getMaxPriceByCategory());
    }

    @Test
    public void test_insert_product() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setBrandName("Z");
        request.setPrice(123123);
        request.setCategory("top");

        Product productToBeInserted = productService.insertProduct(request);

        // Initial dataset has 72 products
        Product productInserted = productRepository.findById(productToBeInserted.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        assertEquals(productInserted, productToBeInserted);
    }

    @Test
    public void test_update_product() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);
        request.setPrice(123123);

        Product productToBeUpdated = productService.updateProduct(request);

        Product productUpdated = productRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        assertEquals(productToBeUpdated, productUpdated);
    }

    @Test
    public void test_delete_product() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);

        // Data using query below
        // INSERT INTO product (brand_name, category, price) VALUES ('A', 'top', 11200);
        Product productTryingToDelete = new Product();
        productTryingToDelete.setId(1L);
        productTryingToDelete.setBrandName("A");
        productTryingToDelete.setCategory("top");
        productTryingToDelete.setPrice(11200);

        Product productToBeDeleted = productService.deleteProduct(request);

        Optional<Product> productDeleted = productRepository.findById(request.getId());

        assertTrue(productDeleted.isEmpty());
        assertEquals(productTryingToDelete, productToBeDeleted);
    }
}
