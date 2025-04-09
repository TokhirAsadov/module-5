package uz.pdp.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
//@NoArgsConstructor
//@AllArgsConstructor
public class Employee extends User{
    private String company;
    private Double salary;

    public Employee(UUID id, String firstName, String lastName, String middleName, String username, String login, String password, String email, String company, Double salary) {
        super(id, firstName, lastName, middleName, username, login, password, email);
        this.company = company;
        this.salary = salary;
    }
}
