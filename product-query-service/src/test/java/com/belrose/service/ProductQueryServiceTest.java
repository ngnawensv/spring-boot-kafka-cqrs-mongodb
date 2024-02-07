package com.belrose.service;

import com.belrose.dto.ProductEvent;
import com.belrose.entity.Product;
import com.belrose.repository.ProductRepository;
import com.belrose.service.impl.ProductQueryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductQueryServiceTest {
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductQueryServiceImpl productService;


    @Test
    void processProductEvents_Save_Successful() {
        Product product = new Product();
        product.setSku("1");
        product.setName("Test Product");
        product.setDescription("Test Product");
        product.setPrice(100.0);

        ProductEvent productEvent = ProductEvent.builder()
                .eventType("CreateProduct")
                .product(product)
                .build();

        when(productRepository.save(product)).thenReturn(Mono.just(product));

        // Act
        productService.processProductEvents(productEvent);

        // Assert
        //TODO
        assertEquals(product.getPrice(),100.0);
    }

    @Test
    void processProductEvents_Update_Successful() {
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

        ProductEvent productEvent = ProductEvent.builder()
                .eventType("UpdateProduct")
                .product(updatedProduct)
                .build();

        when(productRepository.findBySku(sku)).thenReturn(Mono.just(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(Mono.just(updatedProduct));

        // Act
        productService.processProductEvents(productEvent);


        // Assert -
        //TODO
        assertEquals(updatedProduct.getPrice(),200.0);
        // verify(productService, times(1)).updateProduct(updatedProduct);
        // verify(productService).updateProduct(updatedProduct);
    }

    @Test
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(Flux.empty());

        // Act
        Flux<Product> result = productService.getProducts();

        // Assert
        StepVerifier.create(result)
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void testGetAllProducts_NonEmptyList() {
        // Arrange
        Product product = new Product("1","1","Product 1", "Product 1 desc", 10.0);
        when(productRepository.findAll()).thenReturn(Flux.just(product));

        // Act
        Flux<Product> result = productService.getProducts();

        // Assert
        StepVerifier.create(result)
                .expectNext(product)
                .verifyComplete();
    }
}

