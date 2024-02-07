package com.belrose.service;

import com.belrose.entity.Product;
import reactor.core.publisher.Mono;

public interface ProductCommandService{

   Mono<Product> createProduct(Product product);
    Mono<Product> updateProduct(String sku,Product product);

}
