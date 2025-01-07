package com.musinsa.repository;

import com.musinsa.model.dto.db.MinAndMaxPriceDTO;
import com.musinsa.model.dto.db.MinByCategoryAndBrandDTO;
import com.musinsa.model.dto.db.MinForAllByBrandDTO;
import com.musinsa.model.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query(value = """
            SELECT
                p.category AS category,
                p.brand_name AS brand_name,
                p.price AS price
            FROM product p
            JOIN (
                SELECT p1.category, MIN(p1.price) AS min_price
                FROM product p1
                GROUP BY p1.category
            ) min_prices
            ON p.category = min_prices.category AND p.price = min_prices.min_price
            JOIN category_priority cp
            ON p.category = cp.category
            ORDER BY cp.priority
            """, nativeQuery = true)
    List<MinByCategoryAndBrandDTO> findMinimumPriceByCategoryAndBrand();

    @Query(value = """
            WITH category_min_prices AS (
                SELECT
                    p.brand_name,
                    p.category,
                    MIN(p.price) AS min_price
                FROM product p
                GROUP BY p.brand_name, p.category
            ),
            brand_totals AS (
                SELECT
                    cmp.brand_name,
                    SUM(cmp.min_price) AS total_price,
                    COUNT(DISTINCT cmp.category) AS category_count
                FROM category_min_prices cmp
                GROUP BY cmp.brand_name
                HAVING COUNT(DISTINCT cmp.category) = (SELECT COUNT(*) FROM category_priority)
            ),
            min_brands AS (
                SELECT
                    bt.brand_name,
                    bt.total_price
                FROM brand_totals bt
                WHERE bt.total_price = (SELECT MIN(total_price) FROM brand_totals)
            )
            SELECT
                cmp.brand_name,
                cmp.category,
                cmp.min_price AS price
            FROM category_min_prices cmp
            JOIN min_brands mb ON cmp.brand_name = mb.brand_name
            JOIN category_priority cp ON cmp.category = cp.category
            ORDER BY cmp.brand_name, cp.priority
            """, nativeQuery = true)
    List<MinForAllByBrandDTO> findMinimumPriceForAllByBrand();

    @Query(value = """
            WITH max_min_prices AS (
                SELECT
                    MAX(price) AS max_price,
                    MIN(price) AS min_price
                FROM product
                WHERE category = :category
            )
            SELECT
                p.brand_name,
                p.price,
                CASE
                    WHEN p.price = mmp.max_price THEN 'MAX'
                    WHEN p.price = mmp.min_price THEN 'MIN'
                END AS price_type
            FROM product p
            JOIN max_min_prices mmp
              ON p.price = mmp.max_price OR p.price = mmp.min_price
            WHERE p.category = :category
            ORDER BY p.brand_name
            """, nativeQuery = true)
    List<MinAndMaxPriceDTO> findMinimumAndMaximumPriceByCategory(@Param("category") String category);
}
