package uz.pdp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Homework {
    private UUID id;
    private Long userChatId;
    private String description;
    private String zipFileId;
    private Integer ball;
    private String teacherDescription;
    private LocalDateTime sendTime;
    private LocalDateTime checkTime;
}
