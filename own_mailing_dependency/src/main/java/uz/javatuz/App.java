package uz.javatuz;

import uz.javatuz.service.MailingService;

public class App {
    public static void main( String[] args ) {
        boolean bool = MailingService.sendMessage(
                "guvalakat1603@gmail.com",
                "devatpdp@gmail.com",
                "csxaiolzaizhafeg",
                "Test subject from dependency",
                "Test text from dependency",
                true
        );

        if (bool) {
            System.out.println("************* Message *********************");
        } else {
            System.out.println("--------------- Error -----------------");
        }
    }
}
