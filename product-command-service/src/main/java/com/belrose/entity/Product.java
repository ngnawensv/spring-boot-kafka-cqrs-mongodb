package com.belrose.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "product_command")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    @Id
    private String id;
    @NotBlank(message = "sku(Stock Keeping Unit) is mandatory")
    private String sku;
    private String name;
    private String description;
    private double price;
}
