package uz.pdp.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "pav")
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    private UUID id;
    private String pav;
    private String period;
}
