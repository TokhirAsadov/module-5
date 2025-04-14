package uz.javatuz;

import uz.javatuz.entity.User;
import uz.javatuz.entity.UserV2;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectorsExample {
    public static void main(String[] args) {

        String str = "Hello";
        str = str.concat(", G52");
        System.out.println(str);


//        String collect = Stream.of(
//                new UserV2("Ali", 15, "asdfs"),
//                new UserV2("Ali2", 15, "asdfs"),
//                new UserV2("Ali3", 15, "asdfs"),
//                new UserV2("Ali4", 15, "asdfs")
//        ).collect(new ToXmlCollector());
//        System.out.println(collect);

        ///partitioningBy();

        //groupBying();


        //joining();


        //mapping();


        //toCollection();


//        toMap();

//        System.out.println(collect);


        //toSetAndToList();


    }

    private static void partitioningBy() {
        List<String> names = List.of("Ali", "Bob", "Charlie", "Abbos");
        Map<Boolean, List<String>> collect = names.stream()
                .collect(Collectors.partitioningBy(s -> s.length() > 3));
        System.out.println(collect);
    }

    private static void groupBying() {
        List<String> names = List.of("Ali", "Bob", "Charlie");
        Map<Integer, List<String>> result = names.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println(result);
// Natija: {3=[Ali, Bob], 7=[Charlie]}
    }

    private static void joining() {
        List<String> words = List.of("Hello", "World");
        String result = words.stream()
                .collect(Collectors.joining(", "));
        System.out.println(result);
    }

    private static void mapping() {
        List<String> names = List.of("Ali", "Bob");
        List<Integer> lengths = names.stream()
                .collect(Collectors.mapping(String::length, Collectors.toList()));
        System.out.println(lengths);
// Natija: [3, 3]
    }

    private static void toCollection() {
        LinkedList<User> collect = Stream.of(
                        new User("Ali", 3000d),
                        new User("Vali", 2000d),
                        new User("Toshmat", 500d),
                        new User("Ali", 8000d),
                        new User("Eshmat", 1000d),
                        new User("G`ani", 3000d)
                )
                .collect(Collectors.toCollection(LinkedList::new));
        System.out.println(collect);
    }

    private static void toMap() {
        Function<User, String> keyExtractor = User::getName;
        Function<User, Double> valueExtractor = User::getBalance;

        Map<String, Double> collect = Stream.of(
                        new User("Ali", 3000d),
                        new User("Vali", 2000d),
                        new User("Toshmat", 500d),
                        new User("Ali", 8000d),
                        new User("Eshmat", 1000d),
                        new User("G`ani", 3000d)
                )
                .filter(user -> user.getBalance() > 10000)
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueExtractor,
                        (existingValue, newValue) ->  newValue,
                        () -> {
                            System.out.println("Map is empty");
                            return null;
                        }
                ));
    }

    private static void toSetAndToList() {
        Set<User> usersSet = Stream.of(
                        new User("Ali", 3000d),
                        new User("Vali", 2000d),
                        new User("Toshmat", 500d),
                        new User("Eshmat", 1000d),
                        new User("G`ani", 3000d)
                ).filter(user -> user.getBalance() > 1000)
                .collect(Collectors.toSet());

        List<User> usersList = Stream.of(
                        new User("Ali", 3000d),
                        new User("Vali", 2000d),
                        new User("Toshmat", 500d),
                        new User("Eshmat", 1000d),
                        new User("G`ani", 3000d)
                ).filter(user -> user.getBalance() > 1000)
                .collect(Collectors.toList());
    }
}
