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
public class Order {
    private UUID id;
    private Long userChatId;
    private UUID bookId;
    private String photoFileId;
    private String documentFileId;
    private String name;
    private String price;
    private Integer quantity;
    private Boolean isSold;
}
