package uz.javatuz;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Stream;

public class StreamReductionExample {
    public static void main(String[] args) {

        //reductionByIndentityAndBiFunctionAndBiOperationAnd();

        //reductionByIdentityAndBiOperation();

        //reductionByBinaryOperation();

    }

    private static void reductionByIndentityAndBiFunctionAndBiOperationAnd() {
        BiFunction<String, String, String> biFunction = (s1, s2) -> s1.substring(0,3) +s2.substring(s2.length()-3);
        System.out.println(biFunction.apply("Murodjon", "Ali"));

        BinaryOperator<String> binaryOperator = (s1, s2) -> s1 + ", " + s2;
        String reduce = Stream.of(
                        "Murodjon",
                        "Ali",
                        "Vali",
                        "Valijon",
                        "Eshmat",
                        "Vladimir",
                        "Toshmat"
                )
                .filter(s -> s.startsWith("V"))
                .reduce(
                        "List:",
                        biFunction,
                        binaryOperator
                );

        System.out.println("REDUCTION: " + reduce);
    }

    private static void reductionByIndentityAndBiOperation() {
        Stream.of(1, 2, 3, 4, 5)
                .filter(i -> i>10)
                .reduce((s1, s2) -> s1 + s2)
                .ifPresent(System.out::println);

        Integer reduce1 = Stream.of(1, 2, 3, 4, 5)
                .filter(i -> i>10)
                .reduce(0, Integer::sum);
        System.out.println(reduce1);

        String reduce = Stream.of(
                        "Murodjon",
                        "Ali",
                        "Vali",
                        "Eshmat",
                        "Toshmat"
                )
                .reduce("Talabalar Ro`yxati: ", (s1, s2) -> s1 + ", " + s2);
        System.out.println("REDUCTION: " + reduce);
    }

    private static void reductionByBinaryOperation() {
        BinaryOperator<String> binaryOperator = (s1, s2) -> s1 + ", " + s2;
        Optional<String> reduce = Stream.of(
                        "Murodjon",
                        "Ali",
                        "Vali",
                        "Eshmat",
                        "Toshmat"
                )
                .reduce((s1,s2)->s1+", "+s2);
    //Murodjon, Ali
    //Murodjon, Ali, Vali
    //Murodjon, Ali, Vali, Eshmat
    //Murodjon, Ali, Vali, Eshmat, Toshmat
//                .reduce(binaryOperator);
        System.out.println("REDUCTION: " + reduce.get());
    }
}
