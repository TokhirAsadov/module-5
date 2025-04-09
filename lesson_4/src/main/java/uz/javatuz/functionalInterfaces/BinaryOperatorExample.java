package uz.javatuz.functionalInterfaces;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public class BinaryOperatorExample {
    public static void main(String[] args) {
        //BiFunction<T, T, T>
        BinaryOperator<Integer> sum = (a, b) -> a + b;

        BiFunction<Integer, Integer, String> sum2 = (a, b) -> "Summa: "+(a + b);
        System.out.println(sum2.apply(2, 3));

        System.out.println(sum.apply(2, 3)); // 5

        BinaryOperator<Integer> min = BinaryOperator.minBy(Comparator.comparing(Integer::intValue));
        System.out.println("min: "+min.apply(2, 3));

        BinaryOperator<Integer> max = BinaryOperator.maxBy(Comparator.comparing(Integer::intValue));
        System.out.println("max: "+max.apply(2, 3));


        //BiFunction<T, U, R>
        BiFunction<User,List<String>, String > biFunction = (user, list) -> """
            G52 guruh a'zosi:
            Ismi: %s,
            Yoshi: %d,
            O`qigan modullari: %s
            """.formatted(user.getName(), user.getAge(), list);

        User user = new User("Murodullaxon", 16);
        List<String> list = Arrays.asList("Modul1", "Modul2", "Modul3");

        System.out.println(biFunction.apply(user, list));


        BinaryOperator<User> createNewUser = (user1, user2) -> new User(user1.getName(), user2.getAge());

        System.out.println(createNewUser.apply(user, new User("AkmaLjon",23)));

        //m1();
    }

    private static void m1() {
        BiFunction<Integer, String, List<String>> biFunction = (a,b) ->{
            System.out.println(a);
            System.out.println(b);
            List<String> list = Arrays.asList(a.toString(), b);
            return list;
        };
        System.out.println(biFunction.apply(52, "Java"));
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class User {
    private String name;
    private Integer age;
}
