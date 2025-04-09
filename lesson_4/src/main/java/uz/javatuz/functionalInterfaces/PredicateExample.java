package uz.javatuz.functionalInterfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class PredicateExample {

    public static List<String> behaviorFunc(List<String> list, Predicate<String> predicate) {
        List<String> result = new ArrayList<>();
        for (String s : list) {
            if (predicate.test(s)){
                result.add(s);
            }
        }

        return result;
    }

    public static void main(String[] args) {

        Predicate<String> uzunligiBeshdanKattaStringlarArrayiniQaytarish = t -> t.length() > 5;
        Predicate<String> jBilanBoshlanadiganStringlarArrayiniQaytarish = t -> t.startsWith("J");

        List<String> list = Arrays.asList("Java", "Python", "JavaScript", "Javac", "C++", "C#", "JavaFX", "JavaEE");

        List<String> list1 = behaviorFunc(list, uzunligiBeshdanKattaStringlarArrayiniQaytarish);
        List<String> list2 = behaviorFunc(list, jBilanBoshlanadiganStringlarArrayiniQaytarish);

        System.out.println(list1);
        System.out.println(list2);

        //



        //testAndOrNegateMethods();
    }

    private static void testAndOrNegateMethods() {
        Predicate<Integer> isEven = number -> number % 2 == 0;
        Predicate<Integer> undanKattaJuftSonlar = number -> number > 10;

        // and :: if ( isEven && undanKattaJuftSonlar)
        Predicate<Integer> and = isEven.and(undanKattaJuftSonlar);
        System.out.println("and: "+and.test(12));
        System.out.println("and: "+and.test(11));

        // or :: if ( isEven || undanKattaJuftSonlar)
        Predicate<Integer> or = isEven.or(undanKattaJuftSonlar);
        System.out.println("or: "+or.test(12));
        System.out.println("or: "+or.test(7));

        Predicate<Integer> negate = isEven.negate();

        System.out.println("negate: "+negate.test(5));

//        System.out.println(undanKattaJuftSonlar.test(4));
//        System.out.println(isEven.test(5));
    }
}
