package uz.pdp.entity;


import java.util.UUID;

public class PackageLevelExample
{
    public static void main( String[] args ) {
        User user = new User(
                UUID.randomUUID(),
                "John",
                "Doe",
                "Smith",
                "johndoe",
                "johndoe123",
                "password123",
                "nimadir@gmail.com"
        );
        System.out.println(user.getFirstName());
        System.out.println(user.getId());
        System.out.println(user.getLogin());
    }
}
