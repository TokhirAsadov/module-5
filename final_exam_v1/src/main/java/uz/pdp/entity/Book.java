package uz.pdp.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Book {
    private UUID id;
    private String name;
    private String photoFileId;
    private String documentFileId;
    private String price;
    private Integer quantity;
}
