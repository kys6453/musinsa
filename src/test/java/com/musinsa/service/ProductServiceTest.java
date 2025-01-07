package com.musinsa.service;

import com.musinsa.model.dto.db.MinAndMaxPriceDTO;
import com.musinsa.model.dto.db.MinByCategoryAndBrandDTO;
import com.musinsa.model.dto.db.MinForAllByBrandDTO;
import com.musinsa.model.dto.request.ManipulateProductRequest;
import com.musinsa.model.dto.response.MinAndMaxByCategoryResponse;
import com.musinsa.model.dto.response.MinByCategoryAndBrandResponse;
import com.musinsa.model.dto.response.MinForAllByBrandResponse;
import com.musinsa.model.dto.response.component.PriceDTO;
import com.musinsa.model.entity.Product;
import com.musinsa.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private AutoCloseable mock;

    @BeforeEach
    public void setUp() {
        mock = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mock.close();
    }

    @Test
    public void test_find_minimum_price_by_category_and_brand_success() {
        List<MinByCategoryAndBrandDTO> mock = List.of(
                new MinByCategoryAndBrandDTO("top", "A", 1000),
                new MinByCategoryAndBrandDTO("pants", "B", 2000),
                new MinByCategoryAndBrandDTO("socks", "C", 3000)
        );

        when(productRepository.findMinimumPriceByCategoryAndBrand()).thenReturn(mock);

        MinByCategoryAndBrandResponse response = productService.findMinimumPriceByCategoryAndBrand();

        assertNotNull(response);
        assertEquals(6000, response.getTotalSum());

        verify(productRepository, times(1)).findMinimumPriceByCategoryAndBrand();
    }

    @Test
    public void test_find_minimum_price_by_category_and_brand_no_data() {
        when(productRepository.findMinimumPriceByCategoryAndBrand()).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.findMinimumPriceByCategoryAndBrand();
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("No products are found to get minimum price by category and brand", exception.getReason());

        verify(productRepository, times(1)).findMinimumPriceByCategoryAndBrand();
    }

    @Test
    public void test_find_minimum_price_for_all_by_brand_success() {
        List<MinForAllByBrandDTO> mock = List.of(
                new MinForAllByBrandDTO("A", "top", 1000),
                new MinForAllByBrandDTO("A", "pants", 2000),
                new MinForAllByBrandDTO("A", "socks", 3000),
                new MinForAllByBrandDTO("B", "top", 1000),
                new MinForAllByBrandDTO("B", "pants", 2000),
                new MinForAllByBrandDTO("B", "socks", 3000)
        );

        when(productRepository.findMinimumPriceForAllByBrand()).thenReturn(mock);

        MinForAllByBrandResponse response = productService.findMinimumPriceForAllByBrand();

        assertNotNull(response);
        assertEquals(2, mock.stream().map(MinForAllByBrandDTO::getBrandName).distinct().toList().size());
        Map<String, Integer> totalSums = mock.stream()
                .collect(Collectors.groupingBy(
                        MinForAllByBrandDTO::getBrandName,
                        Collectors.summingInt(MinForAllByBrandDTO::getPrice)
                ));
        assertEquals(6000, totalSums.get("A"));
        assertEquals(6000, totalSums.get("B"));

        verify(productRepository, times(1)).findMinimumPriceForAllByBrand();
    }

    @Test
    public void test_find_minimum_price_for_all_by_brand_no_date() {
        when(productRepository.findMinimumPriceForAllByBrand()).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.findMinimumPriceForAllByBrand();
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("All brands have insufficient categories of clothing available", exception.getReason());

        verify(productRepository, times(1)).findMinimumPriceForAllByBrand();
    }

    @Test
    public void test_find_minimum_and_maximum_price_by_category_success() {
        List<MinAndMaxPriceDTO> mock = List.of(
                new MinAndMaxPriceDTO("A", 1000, "MIN"),
                new MinAndMaxPriceDTO("B", 4000, "MAX"),
                new MinAndMaxPriceDTO("D", 4000, "MAX")
        );

        when(productRepository.findMinimumAndMaximumPriceByCategory("top")).thenReturn(mock);

        MinAndMaxByCategoryResponse response = productService.findMinimumAndMaximumPriceByCategory("top");

        assertNotNull(response);
        assertEquals(1, response.getMinPriceByCategory().size());
        assertEquals(2, response.getMaxPriceByCategory().size());
        assertEquals(
                List.of(new PriceDTO("A", 1000)),
                response.getMinPriceByCategory()
        );
        assertEquals(
                List.of(
                        new PriceDTO("B", 4000),
                        new PriceDTO("D", 4000)),
                response.getMaxPriceByCategory()
        );

        verify(productRepository, times(1)).findMinimumAndMaximumPriceByCategory("top");
    }

    @Test
    public void test_find_minimum_and_maximum_price_by_category_no_data() {
        when(productRepository.findMinimumAndMaximumPriceByCategory("pants")).thenReturn(List.of());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.findMinimumAndMaximumPriceByCategory("pants");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("There is no product for the category -> pants", exception.getReason());

        verify(productRepository, times(1)).findMinimumAndMaximumPriceByCategory("pants");
    }

    @Test
    public void test_insert_product_success() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setBrandName("A");
        request.setCategory("top");
        request.setPrice(1000);

        Product mock = new Product();
        mock.setId(1L);
        mock.setBrandName("A");
        mock.setCategory("top");
        mock.setPrice(1000);

        when(productRepository.save(any(Product.class))).thenReturn(mock);

        Product savedProduct = productService.insertProduct(request);

        assertNotNull(savedProduct.getId());
        assertEquals(request.getBrandName(), savedProduct.getBrandName());
        assertEquals(request.getCategory(), savedProduct.getCategory());
        assertEquals(request.getPrice(), savedProduct.getPrice());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void test_insert_product_id_not_null() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);
        request.setBrandName("A");
        request.setCategory("top");
        request.setPrice(1000);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.insertProduct(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You should not specify an id to insert a product, id will be generated automatically", exception.getReason());

        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void test_insert_product_brand_category_price_any_null() {
        ManipulateProductRequest request = new ManipulateProductRequest();

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.insertProduct(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You should specify all values for [brand_name(string), category(string), price(int)] to insert a product", exception.getReason());

        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void test_insert_product_price_negative() {
        ManipulateProductRequest request = new ManipulateProductRequest();

        int randomNum = new Random().nextInt(Integer.MAX_VALUE) * -1 - 1;

        request.setBrandName("A");
        request.setCategory("top");
        request.setPrice(randomNum);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.insertProduct(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Price must be greater than or equal to 0 -> " + randomNum, exception.getReason());

        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void test_update_product_success() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);
        request.setCategory("top");
        request.setBrandName("A");
        request.setPrice(123);

        Product mock = new Product();
        mock.setId(1L);
        mock.setCategory("top");
        mock.setBrandName("A");
        mock.setPrice(123);

        when(productRepository.save(any(Product.class))).thenReturn(mock);
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(mock));

        Product savedProduct = productService.updateProduct(request);

        assertNotNull(savedProduct.getId());
        assertEquals(request.getCategory(), mock.getCategory());
        assertEquals(request.getPrice(), mock.getPrice());
        assertEquals(request.getBrandName(), mock.getBrandName());

        verify(productRepository, times(1)).save(mock);
    }

    @Test
    public void test_update_product_id_null() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setCategory("top");
        request.setBrandName("A");
        request.setPrice(123);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(request);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("You need to specify an existing id to update a product", exception.getReason());

        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void test_update_product_id_not_found() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);
        request.setCategory("top");
        request.setBrandName("A");
        request.setPrice(123);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.updateProduct(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product of the specified id does not exist -> " + 1L, exception.getReason());

        verify(productRepository, times(0)).save(any(Product.class));
    }

    @Test
    public void test_delete_product_success() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);
        request.setCategory("top");
        request.setBrandName("A");
        request.setPrice(123);

        Product mock = new Product();
        mock.setId(1L);
        mock.setCategory("top");
        mock.setBrandName("A");
        mock.setPrice(123);

        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(mock));

        Product deletedProduct = productService.deleteProduct(request);

        assertNotNull(deletedProduct.getId());
        assertEquals(request.getCategory(), mock.getCategory());
        assertEquals(request.getPrice(), mock.getPrice());
        assertEquals(request.getBrandName(), mock.getBrandName());

        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    public void test_delete_product_id_not_found() {
        ManipulateProductRequest request = new ManipulateProductRequest();
        request.setId(1L);
        request.setCategory("top");
        request.setBrandName("A");
        request.setPrice(123);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            productService.deleteProduct(request);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("Product of the specified id does not exist -> " + 1L, exception.getReason());

        verify(productRepository, times(0)).save(any(Product.class));
    }
}
