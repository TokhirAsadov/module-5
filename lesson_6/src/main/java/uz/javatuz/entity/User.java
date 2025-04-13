package uz.javatuz.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String name;
    private Integer age;
    private Double balance;
    private String email;

    public User(String name) {
        this.name = name;
    }
    public User(String name, Double balance) {
        this.name = name;
        this.balance = balance;
    }
    public User(String name, Integer age) {
        this.name = name;
        this.age = age;
    }
}
