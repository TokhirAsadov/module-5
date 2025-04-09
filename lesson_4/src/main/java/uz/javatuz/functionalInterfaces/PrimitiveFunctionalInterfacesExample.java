package uz.javatuz.functionalInterfaces;

import java.util.function.Function;
import java.util.function.ToIntFunction;

public class PrimitiveFunctionalInterfacesExample {
    public static void main(String[] args) {
        ToIntFunction<String> length = s -> s.length();

        System.out.println(length.applyAsInt("Java")); // 4

        Function<String,Integer> length2 = s -> s.length();
    }
}
