package com.belrose.service;

import com.belrose.entity.Product;
import com.belrose.repository.ProductRepository;
import com.belrose.service.impl.ProductCommandServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;


//@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class ProductCommandServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private ProductCommandServiceImpl productCommandService;

    @Test
    void saveProduct_Successful() {
        // Arrange
        Product product = new Product();
        product.setSku("1");
        product.setName("Test Product");
        product.setDescription("Test Product");
        product.setPrice(100.0);

        when(productRepository.save(product)).thenReturn(Mono.just(product));

        // Act
        Mono<Product> result = productCommandService.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedProduct -> savedProduct.getSku().equals("1"))
                .verifyComplete();

        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void saveProduct_Failure() {
        // Arrange
        Product product = new Product();
        product.setSku("1");
        product.setName("Test Product");
        product.setDescription("Test Product");
        product.setPrice(100.0);

        when(productRepository.save(product)).thenReturn(Mono.error(new RuntimeException("Failed to save")));

        // Act
        Mono<Product> result = productCommandService.createProduct(product);

        // Assert
        StepVerifier.create(result)
                .expectError(Exception.class)
                .verify();

        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void updateProduct_Successful() {
        // Arrange
        String id = "1";
        String sku = "1";
        Product updatedProduct = new Product();
        updatedProduct.setId(id);
        updatedProduct.setSku(sku);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(200.0);

        Product existingProduct = new Product();
        existingProduct.setId(id);
        existingProduct.setSku(sku);
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(100.0);

        when(productRepository.findBySku(sku)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(Mono.just(updatedProduct));
        when(kafkaTemplate.send(anyString(), any())).thenReturn(any());

        // Act
        Mono<Product> result = productCommandService.updateProduct(sku, updatedProduct);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedProduct -> savedProduct.getId().equals(sku)
                        && savedProduct.getName().equals("Updated Product")
                        && savedProduct.getPrice() == 200.0)
                .verifyComplete();

        verify(kafkaTemplate, times(1)).send(anyString(), any());
    }

    @Test
    void updateProduct_ProductNotFound() {
        // Arrange
        String sku = "1";
        Product updatedProduct = new Product();
        updatedProduct.setSku(sku);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(200.0);

        when(productRepository.findBySku(sku)).thenReturn(Mono.empty());

        // Act
        Mono<Product> result = productCommandService.updateProduct(sku, updatedProduct);

        // Assert
        StepVerifier.create(result)
                .expectError(Exception.class)
                .verify();

        verify(kafkaTemplate, never()).send(anyString(), any());
    }

    @Test
    void updateProduct_Failure() {
        // Arrange
        String sku = "1";
        Product updatedProduct = new Product();
        updatedProduct.setSku(sku);
        updatedProduct.setName("Updated Product");
        updatedProduct.setPrice(200.0);

        Product existingProduct = new Product();
        existingProduct.setSku(sku);
        existingProduct.setName("Existing Product");
        existingProduct.setPrice(100.0);

        when(productRepository.findBySku(sku)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(Mono.error(new RuntimeException("Failed to save")));

        // Act
        Mono<Product> result = productCommandService.updateProduct(sku, updatedProduct);

        // Assert
        StepVerifier.create(result)
                .expectError(Exception.class)
                .verify();

        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}


