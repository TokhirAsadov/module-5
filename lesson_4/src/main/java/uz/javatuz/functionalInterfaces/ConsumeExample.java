package uz.javatuz.functionalInterfaces;

import java.util.function.Consumer;

public class ConsumeExample {
    public static void main(String[] args) {
        Consumer<String> consumer = s -> {
            System.out.println("Get consumer: " + s);
        };

        Consumer<String> consumer2 = s -> {
            System.out.println("Logging consumer: " + s);
        };

        consumer.andThen(consumer2).accept("Java");

//        consumer.accept("Java");
    }
}
