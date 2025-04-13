package uz.javatuz;

import uz.javatuz.entity.User;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class ComparatorExample {
    public static void main( String[] args ) {

        Stream.of(
            "Ali",
            "Toshmat",
            "Boburbb",
            "Murodjon",
            "Alisher"
        ).sorted(
                Comparator.comparing(String::length)
                        .reversed()
                        .thenComparing(Comparator.naturalOrder())
                )
                .forEach(System.out::println);


        //comparingAndThenComparing();


        //m1();
    }

    private static void comparingAndThenComparing() {
        Stream.of(
                new User("Ali",3000d),
                new User("Toshmat",2500d),
                new User("Murodjon",5000d),
                new User("Alisher",5000d),
                new User("Vali",1500d)
        ).sorted(Comparator.comparing(User::getBalance)
                        .reversed()
                        .thenComparing(User::getName)
                )
                .forEach(System.out::println);
    }

    private static void m1() {
        List<String> names = List.of("Charlie", "Ali", "Bob","Abbos");

        Function<String, Integer> keyExtractor = String::length;
        Comparator<String> comparator = Comparator.comparing(keyExtractor);

        names.stream()
                .sorted(comparator)
                .forEach(System.out::println);
    }
}
