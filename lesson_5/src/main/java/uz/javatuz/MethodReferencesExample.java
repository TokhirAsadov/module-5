package uz.javatuz;

import uz.javatuz.entity.User;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Hello world!
 *
 */
public class MethodReferencesExample
{
    public static int addTen(int i) {
        return i + 10;
    }

    public double divideInteger(int i) {
        return (double) i / 2;
    }


    public static void main( String[] args ) {






        //twoArgumentConstructor();


        //constructorMethodRefereance(list);


//        new User();


        //objectReference(list, app);


        //staticMethodReference(list);

        //m1();
    }

    private static void twoArgumentConstructor() {
        BiFunction<String, Integer, User> biFunction = User::new;

        Map<String,Integer> map = Map.of(
                "Murodjon", 16,
                "Javohir", 25,
                "Abdulaziz", 30
        );
        map.forEach(biFunction::apply);
    }

    private static void constructorMethodRefereance(List<String> list) {
        Set<User> users = list.stream()
                .map(User::new)
                .collect(Collectors.toSet());
        System.out.println(users);
    }

    private static void objectReference(List<Integer> list, MethodReferencesExample methodReferencesExample) {
        Set<Double> collect = list.stream()
//                .map(i -> methodReferencesExample.divideInteger(i))
                .map(methodReferencesExample::divideInteger)
                .collect(Collectors.toSet());
        System.out.println(collect);
    }

    private static void staticMethodReference(List<Integer> list) {
        Set<Integer> collect = list.stream()
                .map(MethodReferencesExample::addTen)
                .collect(Collectors.toSet());

        System.out.println(collect);
    }

    private static void m1() {
        Consumer<String> consumer = (s) -> System.out.println("Hello, " + s);
        List<String> list = Arrays.asList("Java", "Python", "JavaScript");
//        for (String s : list) {
//            consumer.accept(s);
//        }
        list.forEach(System.out::println);
    }
}
