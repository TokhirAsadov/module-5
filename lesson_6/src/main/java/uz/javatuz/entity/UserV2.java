package uz.javatuz.entity;

import lombok.*;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class UserV2 {
    private final String name;
    @NonNull
    private int balance;
    private Integer age;

    @NonNull
    private String email;

    public UserV2(String name) {
        this.name = name;
    }
    public UserV2(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
