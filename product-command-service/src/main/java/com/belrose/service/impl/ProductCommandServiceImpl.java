package com.belrose.service.impl;

import com.belrose.dto.ProductEvent;
import com.belrose.entity.Product;
import com.belrose.repository.ProductRepository;
import com.belrose.service.ProductCommandService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ProductCommandServiceImpl implements ProductCommandService {

    private final ProductRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public ProductCommandServiceImpl(ProductRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public Mono<Product> createProduct(Product product) {
        return repository.save(product)
                .doOnSuccess(savedProduct -> {
                    // Sending Kafka event after successful save
                    sendKafkaEvent(savedProduct, "CreateProduct");
                    log.info("Product successfully saved and kafka event successfully sent");
                })
                .onErrorMap(throwable -> {
                    // Transforming any error into a custom exception
                    log.error("Error to save product");
                    return new Exception(String.format("Failed to save product.Cause %s", throwable));
                });
    }


    public Mono<Product> updateProduct(String sku, Product updatedProduct) {
        return repository.findBySku(sku)
                .flatMap(existingProduct -> {
                    existingProduct.setName(updatedProduct.getName());
                    existingProduct.setDescription(updatedProduct.getDescription());
                    existingProduct.setPrice(updatedProduct.getPrice());

                    return repository.save(existingProduct)
                            .doOnSuccess(product -> {
                                sendKafkaEvent(product, "UpdateProduct");
                                log.info("Product successfully updated and kafka event successfully sent");
                            });
                })
                //Exception throwing when product not found
                .switchIfEmpty(Mono.error(new Exception(String.format("Product not found with sku %s", sku))))
                .onErrorMap(throwable -> {
                    // Transforming any error into a custom exception
                    log.error("Failed to update product with sku");
                    return new Exception(String.format("Failed to update product with sku %s. Cause %s", sku, throwable));
                });
    }

    private void sendKafkaEvent(Product product, String eventType) {
        try {
            ProductEvent productEvent = ProductEvent.builder()
                    .eventType(eventType)
                    .product(product)
                    .build();
            kafkaTemplate.send("product-event-topic", productEvent);
        } catch (Exception e) {
            // Log the error or handle it accordingly
            log.error(String.format("Error to send kafka event %s", e.getMessage()));
            // You can choose to rethrow the exception or ignore it based on your requirements
        }
    }

}
