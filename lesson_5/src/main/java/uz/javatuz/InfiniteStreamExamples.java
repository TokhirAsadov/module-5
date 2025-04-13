package uz.javatuz;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;

public class InfiniteStreamExamples {
    public static void main(String[] args) {

        //streamRandomIntsLongsDoubles();
        //streamGenerate();

        //streamIterate();
    }

    private static void streamRandomIntsLongsDoubles() {
        Random random = new Random();
        random.ints(10,0,20)
                .forEach(System.out::println);

        random.longs(10,0,20)
                .forEach(System.out::println);
        random.doubles(10,0,20)
                .forEach(System.out::println);
    }

    private static void streamGenerate() {
        Stream<UUID> generate = Stream.generate(UUID::randomUUID)
                        .limit(10);

        Optional<String> reduce = generate.map(UUID::toString)
                .reduce(
                        (s1, s2) -> s1 + ", " + s2
                );

        String reduce2 = generate.map(UUID::toString)
                .reduce(
                        "list:",
                        (s1, s2) -> s1 + ", " + s2
                );
        reduce.ifPresentOrElse(
                s -> System.out.println("REDUCE: " + s),
                () -> System.out.println("----------- No element found ---------")
        );
    }

    private static void streamIterate() {
        Stream.iterate(1, i -> i <= 25, i -> i + 1)
                .forEach(System.out::println);

        Stream.iterate(0, i -> i + 1)
                .takeWhile(i -> i < 10)
                .forEach(System.out::println);
    }
}
