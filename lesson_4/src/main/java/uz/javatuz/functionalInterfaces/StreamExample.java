package uz.javatuz.functionalInterfaces;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StreamExample {
    public static void main(String[] args) {

        List<Student> list = Arrays.asList(
                new Student("Murodjon", 16),
                new Student("AkmaLjon", 23),
                new Student("Kibriyo", 19),
                new Student("Azizbek", 22)
        );
        List<Integer> list2 = Arrays.asList(1, 2, 3, 4, 5);
        filter2(list2);


//        m3(list);


        //List<Student> list = Arrays.asList(
        //                new Student("Murodjon", 16),
        //                new Student("AkmaLjon", 23),
        //                new Student("Kibriyo", 19),
        //                new Student("Azizbek", 22)
        //        );
        // m2(list);


//        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        //filter(list);

    }

    private static void m3(List<Student> list) {
        Function<Student, String> function = student -> student.getName()+" " + student.getAge();

        Set<String> collect = list.stream()
                .map(function)
                .collect(Collectors.toSet());
        System.out.println(collect);
    }

    private static void m2(List<Student> list) {
        Consumer<Student> consumer = student -> {
            System.out.println("Student name: " + student.getName());
            System.out.println("Student age: " + student.getAge());
            System.out.println("---------------------");
        };
        list.stream().forEach(consumer);
    }

    private static void filter(List<Integer> list) {
        // filter and collect and limit
        Predicate<Integer> isEven = number -> number % 2 == 0;
        List<Integer> evenInters = list.stream()
//                .filter(isEven)
                .filter(number -> number % 2 == 0)
                .limit(1)
                .collect(Collectors.toList());
        System.out.println(evenInters);
    }

    private static void filter2(List<Integer> list) {
        // filter and collect and limit
        Predicate<Integer> isEven = number -> number % 2 == 0;
        long count = list.stream()
//                .filter(isEven)
                .filter(number -> number % 2 == 0)
                .count();
        System.out.println(count);
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Student {
    private String name;
    private Integer age;
}
