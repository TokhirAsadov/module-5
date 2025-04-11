package uz.javatuz;

import lombok.SneakyThrows;
import uz.javatuz.entity.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DeclarativeAndImperativeProgrammingExample {
    @SneakyThrows
    public static void main(String[] args) {

//        List<User> users = List.of(
//                new User("Murodjon", 16),
//                new User("Ali", 20),
//                new User("Vali", 24),
//                new User("Eshmat", 30),
//                new User("Toshmat", 44)
//        );

        List<String> ismlar = List.of("Murodjon", "Ali", "Vali", "Eshmat", "Toshmat");
        List<String> collect = ismlar.stream().map(ism -> ism.substring(0, 1)).collect(Collectors.toList());
        System.out.println(collect);
//        List<String> ismlarBoshHarflari = new ArrayList<>();
//        for (int coreCount = 0; coreCount < ismlar.size(); coreCount++) {
//            String s = ismlar.get(coreCount);
//            ismlarBoshHarflari.add(s.substring(0, 1));
//        }
//        System.out.println(ismlarBoshHarflari);

        //flatMap();


        //map(ismlar, emails);


        //matching(users);

        //peekAndForEach();

        //skip();

        //limit(random);

        //randomIntsLongsDoubles();

        //fileStream();

        //iterator();

        //streamOf();

        //arraysStream();

        //internal();

        //extracted(integers);


        //laziness();


        //imperative();

        //declarative();
    }

    private static void flatMap() {
        List<String> ismlar = List.of("Murodjon", "Ali", "Vali", "Eshmat", "Toshmat");
        List<String> ismlar2 = List.of("Bob", "Tom", "Anna", "John", "Wick");

        List<List<String>> multiList = List.of(ismlar, ismlar2);

        System.out.println(multiList.stream()
                .map(List::size)
                .count());

        multiList.stream()
                .flatMap(List::stream)
                .map(User::new)
                .forEach(System.out::println);
    }

    private static void map(List<String> ismlar, List<String> emails) {
        AtomicInteger counter = new AtomicInteger(0);
        Set<User> users = ismlar.stream()
                .map(ism -> new User(ism, 20, emails.get(counter.getAndIncrement())))
                .collect(Collectors.toSet());
        System.out.println(users);
    }

    private static void matching(List<User> users) {
        boolean allMatch = users.stream()
                .allMatch(user -> user.getAge() >= 16);
        System.out.println("allMatch = " + allMatch);

        boolean anyMatch = users.stream()
                .anyMatch(user -> user.getAge() >= 18);
        System.out.println("anyMatch = " + anyMatch);

        boolean noneMatch = users.stream()
                .noneMatch(user -> user.getAge() >= 18);
        System.out.println("noneMatch = " + noneMatch);
    }

    private static void peekAndForEach() {
        List.of("apple", "banana", "cherry")
                .stream()
                .peek(s -> System.out.println("Before filter: " + s))
                .filter(s -> s.length() > 5)
                .peek(s -> System.out.println("After filter: " + s))
                .map(String::toUpperCase)
                .forEach(s -> System.out.println("Final result: " + s));
    }

    private static void skip() {
        List.of(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10
        ).stream()
                .filter(i -> i % 2 == 0)
                .skip(4)
                .forEach(System.out::println);
    }

    private static void limit(Random random) {
        random.ints(50,1, 10_000)
                .filter(i -> i % 2 == 0)
                .skip(4)
                .limit(5)
                .forEach(System.out::println);
    }

    private static void randomIntsLongsDoubles() {
        Random random = new Random();
        random.ints(5, 10,100)
//                .limit(10)
                .forEach(System.out::println);

        random.doubles(5, 10,100)
//                .limit(10)
                .forEach(System.out::println);
    }

    private static void fileStream() throws IOException {
        Files.readAllLines(Path.of("src/main/resources/g52.txt"))
                .stream()
                .forEach(System.out::println);
    }

    private static void iterator() {
        Stream.iterate(0, i -> i + 1)
                .limit(100)
                .forEach(System.out::println);
    }

    private static void streamOf() {
        Stream.of(1, 2, 3, 4, 5)
                .filter(i -> i % 2 == 0)
                .forEach(System.out::println);
    }

    private static void arraysStream() {
        int[] arr = new int[]{1,2,3,4,5,6,7,8,9,10};
        IntStream intStream = Arrays.stream(arr)
                .filter(i -> i % 2 == 0);
        intStream.forEach(System.out::println);
    }

    private static void internal() {
        List<Integer> integers = List.of(3,1, 4, 2,  5);
        long count1 = List.of("apple", "banana", "cherry")
                .stream()
                .filter(s -> s.length() > 5)
                .count();
        System.out.println(count1);

        long count = integers.stream()
                .filter(i -> i % 2 != 0)
//                .sorted()
//                .limit(1)
                .count();
        System.out.println(count);
    }

    private static void extracted(List<Integer> integers) {
        Stream<Integer> stream = integers.stream();
        Stream<Integer> integerStream = stream.map(i -> i * 2);
        Stream<Integer> limit = integerStream.limit(1);
        limit.forEach(System.out::println);
    }

    private static void laziness() {
        Stream.iterate(0, i -> i + 1)
                .filter(integer -> integer % 2 == 0)
                .limit(100)
                .forEach(System.out::println);
    }

    private static void declarative() {
        List.of(1,2,3,4,5).stream()
                .filter(i -> i % 2 == 0)
                .forEach(System.out::println);
    }

    private static void imperative() {
        List<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(4);
        numbers.add(5);

        for (int i = 0; i < numbers.size(); i++) {
            if (numbers.get(i) % 2 == 0) {
                System.out.println(numbers.get(i));
            }
        }
    }
}
