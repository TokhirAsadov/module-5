package com.pdp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product {
    private String fileId;
    private String name;
    private Double price;
    private Integer quantity;
    private Integer soldCount;
}
