package com.pdp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class History {
    private UUID id;
    private Long userId;
    private UUID productId;
    private String fileId;
    private String name;
    private Double price;
    private Integer quantity;
    private Boolean isSold;
//    private Timestamp date;
}
