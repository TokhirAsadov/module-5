package uz.javatuz.functionalInterfaces;

import java.util.function.UnaryOperator;

public class UnaryOperatorExample {
    public static void main(String[] args) {
        UnaryOperator<Integer> square = number -> number * number;
        UnaryOperator<Integer> addingTen = number -> number +10;
        System.out.println(square.apply(5)); // 25

        System.out.println(square.compose(addingTen).apply(5));
        System.out.println(square.andThen(addingTen).apply(5));

    }
}
