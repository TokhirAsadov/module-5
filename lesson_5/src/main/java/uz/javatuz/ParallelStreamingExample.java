package uz.javatuz;

import java.util.List;
import java.util.stream.Stream;

public class ParallelStreamingExample {
    public static void main(String[] args) {
        Stream.of(
                        "Murodjon",
                        "Vali3",
                        "Ali",
                        "Vali",
                        "Eshmat",
                        "Toshmat"
                )
                .parallel()
                .filter(s -> s.length()>3)
                .forEach(System.out::println);

        List.of(
                        "Murodjon",
                        "Vali3",
                        "Ali",
                        "Vali",
                        "Eshmat",
                        "Toshmat"
                )
                .parallelStream()
                .filter(s -> s.length()>3)
                .forEach(System.out::println);

    }
}
