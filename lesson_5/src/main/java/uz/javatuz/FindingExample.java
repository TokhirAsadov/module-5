package uz.javatuz;

import java.util.Optional;
import java.util.stream.Stream;

public class FindingExample {
    public static void main(String[] args) {
        findAny();

        //findFunction();

    }

    private static void findAny() {
        Optional<String> first = Stream.of(
                        "Murodjon",
                        "Ali",
                        "Vali",
                        "Vali2",
                        "Vali4",
                        "Vali3",
                        "Vali6",
                        "Vali5",
                        "Eshmat",
                        "Toshmat"
                )
                .filter(s -> s.startsWith("V"))
                .findAny();
        System.out.println("GET: "+first.get());
    }

    private static void findFunction() {
        Optional<String> first = Stream.of(
                        "Murodjon",
                        "Ali",
                        "Vali",
                        "Eshmat",
                        "Toshmat"
                ).filter(s -> s.startsWith("W"))
                .findFirst();

//        System.out.println("GET: "+first.get());
        first.ifPresentOrElse(
                s->{
                    System.out.println("First element: " + s);
                },
                ()->{
                    System.out.println("----------- No element found ---------");
                }
        );
    }
}
