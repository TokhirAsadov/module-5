package uz.javatuz;

import uz.javatuz.entity.UserV2;

import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ToXmlCollector implements Collector<UserV2, StringBuilder, String> {

    @Override
    public Supplier<StringBuilder> supplier() {
        return StringBuilder::new; // Bo‘sh StringBuilder yaratadi
    }

    @Override
    public BiConsumer<StringBuilder, UserV2> accumulator() {
        return (sb, user) -> { // Har bir UserV2’ni XML qatoriga qo‘shadi
            sb.append("<user>")
                    .append("<name>").append(user.getName()).append("</name>")
                    .append("<age>").append(user.getAge()).append("</age>")
                    .append("</user>");
        };
    }

    @Override
    public BinaryOperator<StringBuilder> combiner() {
        return (sb1, sb2) -> { // Ikkita StringBuilder’ni birlashtiradi
            sb1.append(sb2);
            return sb1;
        };
    }

    @Override
    public Function<StringBuilder, String> finisher() {
        return sb -> "<users>" + sb.toString() + "</users>"; // Yakuniy XML hosil qiladi
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(); // Hech qanday maxsus xususiyat yo‘q
    }

    public static void main(String[] args) {
        List<UserV2> users = List.of(
                new UserV2("Ali", 25),
                new UserV2("Bob", 30)
        );

        String xml = users.stream()
                .collect(new ToXmlCollector());
        System.out.println(xml);
        // Natija:
        // <users><user><name>Ali</name><age>25</age></user><user><name>Bob</name><age>30</age></user></users>
    }
}