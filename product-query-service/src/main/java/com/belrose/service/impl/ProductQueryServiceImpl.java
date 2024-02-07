package com.belrose.service.impl;

import com.belrose.dto.ProductEvent;
import com.belrose.entity.Product;
import com.belrose.repository.ProductRepository;
import com.belrose.service.ProductQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProductQueryServiceImpl implements ProductQueryService {

    private final ProductRepository repository;

    public ProductQueryServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }

    public Flux<Product> getProducts() {
        return repository.findAll()
                .switchIfEmpty(Flux.empty());
    }

    @KafkaListener(topics = "product-event-topic",groupId = "product-event-group")
    public void processProductEvents(ProductEvent productEvent) {
        Product product = productEvent.getProduct();
        switch (productEvent.getEventType()){
            case "CreateProduct" -> saveProduct(product).subscribe();
            case "UpdateProduct" -> updateProduct(product).subscribe();
            default -> throw new IllegalArgumentException(String.format("Invalid  event type: %s ", productEvent.getEventType()));
        }

    }


    public Mono<Product> saveProduct(Product product) {
       return repository.save(product)
                .onErrorMap(throwable -> {
                    // Transforming any error into a custom exception
                    log.error("Error to save product");
                    return new Exception(String.format("Failed to save product.Cause %s", throwable));
                });
    }


    public Mono<Product> updateProduct(Product product) {
        return repository.findBySku(product.getSku())
                .flatMap(existingProduct -> {
                    existingProduct.setName(product.getName());
                    existingProduct.setDescription(product.getDescription());
                    existingProduct.setPrice(product.getPrice());

                    return repository.save(existingProduct)
                            .doOnSuccess(productUpdated ->
                                    log.info("Product successfully updated and kafka event successfully sent {}",productUpdated));
                })
                //Exception throwing when product not found
                .switchIfEmpty(Mono.error(new Exception(String.format("Product not found with sku %s", product.getSku()))))
                .onErrorMap(throwable -> {
                    // Transforming any error into a custom exception
                    log.error("Failed to update product with sku");
                    return  new Exception(String.format("Failed to update product with sku %s. Cause %s",product.getSku(),throwable));
                });
    }
}
