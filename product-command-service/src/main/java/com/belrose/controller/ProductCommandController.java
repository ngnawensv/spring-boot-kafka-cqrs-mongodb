package com.belrose.controller;

import com.belrose.entity.Product;
import com.belrose.service.ProductCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/products")
public class ProductCommandController {

    private final ProductCommandService commandService;

    public ProductCommandController(ProductCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping
    public ResponseEntity<Mono<Product>> createProduct(@RequestBody Product product) {
        var response = commandService.createProduct(product);
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/updated")
    public ResponseEntity<Mono<Product>> updateProduct(@RequestHeader String sku, @RequestBody Product product) {
        var response =  commandService.updateProduct(sku, product);
        return ResponseEntity.ok().body(response);
    }
}
