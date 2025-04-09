package uz.pdp;


import lombok.*;
import lombok.extern.java.Log;
import uz.pdp.entity.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.logging.Level;

@ToString
@Log
public class App {

    @Getter(lazy = true)
    private final UUID id = generateId();

    private UUID generateId() {
        System.out.println("Id is generating....");
        return UUID.randomUUID();
    }

    @SneakyThrows
    public static void main( String[] args ) {
        @Cleanup FileInputStream fileInputStream = new FileInputStream("src/main/resources/application.properties");
        @Cleanup FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/application.properties",true);
        log.log(Level.INFO,"info message is it from @Log");

//        builder();

//        Product product = new Product(
//                UUID.randomUUID(),
//                "Iphone 14",
//                1000d
//        );
//        System.out.println(product);


//        User user = new User("Murodjon","Asqariliyev","lello","123");
//        System.out.println(user);
//        user.setId(UUID.randomUUID());
//        System.out.println(user.getId());
//        App app = new App();
//        System.out.println("Object yaratildi...");
//        System.out.println(app.getId());
//        extracted();
//        equalsAndHashCode();

    }

    private static void builder() {
        Region region = Region.builder()
                .id(UUID.randomUUID())
                .regionName("Tashkent")
                .countOfDistricts(12)
                .build();
        System.out.println(region);

        District district = District.districtBuilder()
                .id(UUID.randomUUID())
                .regionName("Tashkent")
                .countOfDistricts(12)
                .name("yakkasaroy")
                .districtBuild();
        System.out.println(district);
    }

    private static void equalsAndHashCode() {
        Card card1 = new Card(
                UUID.randomUUID(),
                "98601201",
                "02/2025"
        );
        Card card2 = new Card(
                UUID.randomUUID(),
                "98601201",
                "08/2035"
        );
        System.out.println(card1.equals(card2));
    }

    private static void extracted() {
        User user = new User(
                UUID.randomUUID(),
                "John",
                "Doe",
                "Smith",
                "johndoe",
                "johndoe123",
                "password123",
                "asdfsaf"
        );
        System.out.println(user);
        Employee employee = new Employee(
                UUID.randomUUID(),
                "John",
                "Doe",
                "Smith",
                "johndoe",
                "johndoe123",
                "password123",
                "asdfsaf",
                "Google",
                250000d
        );

        System.out.println(employee);
    }
}
