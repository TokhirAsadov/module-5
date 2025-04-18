package uz.javatuz;

import uz.javatuz.service.MailingService;

import java.lang.reflect.Field;

public class App {
    public static void main( String[] args ) throws NoSuchFieldException, IllegalAccessException {
        // Person classi
        Class<?> clazz = Person.class;
        Person person = new Person("Ali", 25);

        // Maydonlarni olish
        Field nameField = clazz.getDeclaredField("name");
        Field ageField = clazz.getDeclaredField("age");

        // Private maydonlarga kirish uchun setAccessible(true)
        nameField.setAccessible(true);
        ageField.setAccessible(true);

        // Maydon qiymatlarini o‘qish
        System.out.println("Name: " + nameField.get(person));
        System.out.println("Age: " + ageField.get(person));

        // Maydon qiymatlarini o‘zgartirish
        nameField.set(person, "Vali");
        ageField.set(person, 30);

        System.out.println("Yangilangan: " + person);
//        boolean bool = MailingService.sendMessage(
//                "guvalakat1603@gmail.com",
//                "devatpdp@gmail.com",
//                "csxaiolzaizhafeg",
//                "Test subject from dependency",
//                "Test text from dependency",
//                true
//        );
//
//        if (bool) {
//            System.out.println("************* Message *********************");
//        } else {
//            System.out.println("--------------- Error -----------------");
//        }
    }
}
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', age=" + age + "}";
    }
}