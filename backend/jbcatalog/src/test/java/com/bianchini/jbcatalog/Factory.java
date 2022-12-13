package com.bianchini.jbcatalog;

import com.bianchini.jbcatalog.dto.ProductDto;
import com.bianchini.jbcatalog.entities.Category;
import com.bianchini.jbcatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product create(){
        Product product = new Product(1L,"Phone", "Good Phone", 1000.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDto createDto(){
        Product product = create();
        return new ProductDto(product, product.getCategories());
    }

    public static Category creteCategory(){
        Category category = new Category(1L, "Books");
        return category;
    }
}
