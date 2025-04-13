package uz.javatuz;

import uz.javatuz.entity.User;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ToArrayExample {
    public static void main(String[] args) {

        Stream<String> distinct = List.of(
                        new User("Murodjon", 16),
                        new User("Ali", 20),
                        new User("Vali", 24),
                        new User("Eshamt", 24)

                ).stream()
                .map(User::getName)
                .distinct();

        String[] ages = distinct.toArray(String[]::new);
        System.out.println(Arrays.toString(ages));

    }
}
