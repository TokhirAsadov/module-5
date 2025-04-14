package uz.javatuz;

import com.github.javafaker.*;

import java.util.Locale;

public class App {
    public static void main( String[] args ) {

        Faker faker = new Faker(/*Locale.forLanguageTag("ru")*/);
        Name name = faker.name();
        System.out.println(name.fullName());

        Address address = faker.address();
        System.out.println(address.fullAddress());

        PhoneNumber phoneNumber = faker.phoneNumber();
        System.out.println(phoneNumber.phoneNumber());

        Country country = faker.country();
        System.out.println(country.name());

        HarryPotter harryPotter = faker.harryPotter();
        for (int i = 0; i < 10; i++) {
            System.out.println(harryPotter.book());
        }


//        for (int i = 0; i < 150; i++) {
//            System.out.println(country.name());
//        }


//        boolean b = MailingService.sendStrMessage(
//                "guvalakat1603@gmail.com",
//                "devatpdp@gmail.com",
//                "csxaiolzaizhafeg",
//                "Test subject",
//                "Test text",
//                true
//        );
//        if (b) {
//            System.out.println("=========== Message sent successfully ===========");
//        } else {
//            System.out.println("---------- Message not sent ----------");
//        }
    }
}

//csxaiolzaizhafeg