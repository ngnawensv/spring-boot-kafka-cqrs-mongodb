package com.belrose.service;

import com.belrose.dto.ProductEvent;
import com.belrose.entity.Product;
import org.springframework.kafka.annotation.KafkaListener;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductQueryService {
  Flux<Product> getProducts();
}
