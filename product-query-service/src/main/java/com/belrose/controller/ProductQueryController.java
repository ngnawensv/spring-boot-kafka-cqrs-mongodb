package com.belrose.controller;

import com.belrose.entity.Product;
import com.belrose.service.ProductQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RequestMapping("/products")
@RestController
public class ProductQueryController {

    private final ProductQueryService queryService;

    public ProductQueryController(ProductQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<Flux<Product>> fetchAllProducts(){
        var response = queryService.getProducts();
        return ResponseEntity.ok().body(response);
    }


}
