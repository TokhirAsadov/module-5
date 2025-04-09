package uz.pdp.entity;

import lombok.Value;

import java.util.UUID;

@Value
public class Product {
    private UUID id;
    private String name;
    private Double price;
}
