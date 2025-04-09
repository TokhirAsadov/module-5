package uz.javatuz.functionalInterfaces;

import java.util.function.Function;

public class FunctionExample {



    public static void main(String[] args) {
        Function<Integer, String> function = number -> "Mana bu sonni uqing: "+number;
        System.out.println(function.apply(10));

        Function<Integer, Integer> fun1 = number -> number*number;
        Function<Integer, Integer> fun2 = number -> number + 10;

        System.out.println("compose: "+fun1.compose(fun2).apply(4));
        System.out.println("andThen: "+fun1.andThen(fun2).apply(4));



    }
}
