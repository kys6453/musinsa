package com.musinsa.service;

import com.musinsa.model.dto.db.MinAndMaxPriceDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public MinByCategoryAndBrandResponse findMinimumPriceByCategoryAndBrand() {
        List<MinByCategoryAndBrandDTO> products = productRepository.findMinimumPriceByCategoryAndBrand();

        if (products.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No products are found to get minimum price by category and brand"
            );
        }

        int totalSum = 0;

        for (MinByCategoryAndBrandDTO product : products) {
            totalSum += product.getPrice();
        }

        return new MinByCategoryAndBrandResponse(products, totalSum);
    }

    public MinForAllByBrandResponse findMinimumPriceForAllByBrand() {
        List<MinForAllByBrandDTO> products = productRepository.findMinimumPriceForAllByBrand();

        if (products.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "All brands have insufficient categories of clothing available"
            );
        }

        // 최저가가 여러 브랜드일 수 있기 때문에 List로 담아서 return
        List<MinForAllByBrandElement> responseElements = products.stream()
                .collect(Collectors.groupingBy(MinForAllByBrandDTO::getBrandName))
                .entrySet().stream()
                .map(entry -> {
                    String brandName = entry.getKey();
                    List<MinForAllByBrandDTO> productsByBrand = entry.getValue();

                    Integer totalSum = productsByBrand.stream()
                            .mapToInt(MinForAllByBrandDTO::getPrice)
                            .sum();
                    return new MinForAllByBrandElement(brandName, productsByBrand, totalSum);
                })
                .toList();

        return new MinForAllByBrandResponse(responseElements);
    }

    public MinAndMaxByCategoryResponse findMinimumAndMaximumPriceByCategory(String category) {
        List<MinAndMaxPriceDTO> minAndMaxProduct = productRepository.findMinimumAndMaximumPriceByCategory(category);

        if (minAndMaxProduct.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "There is no product for the category -> " + category
            );
        }

        List<PriceDTO> minPrices = minAndMaxProduct.stream()
                .filter(dto -> "min".equalsIgnoreCase(dto.getPriceType()))
                .map(dto -> new PriceDTO(dto.getBrandName(), dto.getPrice()))
                .toList();
        List<PriceDTO> maxPrices = minAndMaxProduct.stream()
                .filter(dto -> "max".equalsIgnoreCase(dto.getPriceType()))
                .map(dto -> new PriceDTO(dto.getBrandName(), dto.getPrice()))
                .toList();

        return new MinAndMaxByCategoryResponse(
                category, minPrices, maxPrices
        );
    }

    public Product insertProduct(ManipulateProductRequest request) {
        // Insert 할 때 ID 값이 있으면 안된다. (ID는 자동채번)
        if (request.getId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You should not specify an id to insert a product, id will be generated automatically"
            );
        }

        if (isNullOrBlank(request.getBrandName())
                || isNullOrBlank(request.getCategory())
                || request.getPrice() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You should specify all values for [brand_name(string), category(string), price(int)] to insert a product"
            );
        }

        // 가격은 항상 음수값이 되면 안된다.
        if (request.getPrice() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than or equal to 0 -> " + request.getPrice());
        }

        Product product = new Product();
        product.setBrandName(request.getBrandName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());

        return productRepository.save(product);
    }

    public Product updateProduct(ManipulateProductRequest request) {
        Product product;
        Long id = request.getId();

        if (id != null) {
            // 해당하는 id의 product가 없을 경우 에러 응답
            product = productRepository.findById(id)
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND, "Product of the specified id does not exist -> " + id
                            ));
            if (request.getBrandName() != null) product.setBrandName(request.getBrandName());
            if (request.getCategory() != null) product.setCategory(request.getCategory());
            if (request.getPrice() != null) {
                // 가격은 항상 음수값이 되면 안된다.
                if (request.getPrice() < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price must be greater than or equal to 0 -> " + request.getPrice());
                }
                product.setPrice(request.getPrice());
            }
            return productRepository.save(product);
        } else {
            // id를 명시하지 않았을 경우 에러 응답 (update 할 때는 id가 꼭 필요하다)
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "You need to specify an existing id to update a product"
            );
        }
    }

    public Product deleteProduct(ManipulateProductRequest request) {
        Long id = request.getId();
        // id를 명시하지 않았을 경우 에러 응답 (delete 할 때는 id가 꼭 필요하다)
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND, "Product of the specified id does not exist -> " + id
                        ));
        productRepository.deleteById(id);
        return product;
    }

    private boolean isNullOrBlank(String str) {
        return str == null || str.isBlank();
    }
}
