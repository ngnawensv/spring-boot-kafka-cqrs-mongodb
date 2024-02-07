package com.belrose.repository;

import com.belrose.entity.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends ReactiveMongoRepository<Product,String> {
     Mono<Product> findBySku(String sku);
}
