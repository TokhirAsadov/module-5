package uz.javatuz;

import com.github.javafaker.Faker;
import com.github.javafaker.Lorem;
import com.github.javafaker.Name;
import com.github.javafaker.PhoneNumber;

import java.util.Locale;

public class App {
    public static void main( String[] args ) {
        Faker faker = new Faker(/*Locale.forLanguageTag("ru")*/);
//        Name name = faker.name();
//        for (int i = 0; i < 20; i++) {
//            System.out.println(name.fullName());
//        }
//        PhoneNumber phoneNumber = faker.phoneNumber();
//        for (int i = 0; i < 20; i++) {
//            System.out.println(phoneNumber.phoneNumber());
//        }

//        Lorem lorem = faker.lorem();
//        System.out.println(lorem.words(6));
//        System.out.println(lorem.sentence(6));
//        System.out.println(lorem.paragraph(3));
//        System.out.println(lorem.paragraphs(3));

        boolean b = MailingService.sendStrMessage(
                "guvalakat1603@gmail.com",
                "devatpdp@gmail.com",
                "csxaiolzaizhafeg",
                "Test subject",
                "Test text",
                true
        );
        if (b) {
            System.out.println("=========== Message sent successfully ===========");
        } else {
            System.out.println("---------- Message not sent ----------");
        }
    }
}

//csxaiolzaizhafeg