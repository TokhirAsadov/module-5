package com.pdp;

import com.pdp.enums.UserState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Contact;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;

import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramBotUpdateHandler {

    private static final TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));

    public static ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();

    public static void handler(Update update){

        Contact contact1 = update.message().contact();
        System.out.println("Contact: " + contact1);

        if (update.message()!= null) {
            Long chatID = update.message().from().id();
            String text = update.message().text();
            User from = update.message().from();

            if (text.equals("/start")) {
                userStates.put(chatID, UserState.ENTERING_NAME);
                String welcomeMessage = "Welcome to the bot!\n Please enter your full name.\n <i>Example: John Doe</i>";
                bot.execute(new SendMessage(chatID, welcomeMessage));
            } else if (userStates.get(chatID) == UserState.ENTERING_NAME) {
                userStates.put(chatID, UserState.ENTERING_AGE);
                String welcomeMessage = "Please enter your age.\n <i>Example: 16</i>";
                bot.execute(new SendMessage(chatID, welcomeMessage));
            } else if (userStates.get(chatID) == UserState.ENTERING_AGE) {
                userStates.put(chatID, UserState.SHARING_PHONE);
                SendMessage sendMessage = new SendMessage(
                        chatID,
                        "Please, share your contact!"
                );
                KeyboardButton contact = new KeyboardButton("\uD83D\uDCDE Contact");
                contact.requestContact(true);
                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(contact);
                replyMarkup.resizeKeyboard(true);
                sendMessage.replyMarkup(replyMarkup);
                bot.execute(sendMessage);

            } else if (userStates.get(contact1.userId()) == UserState.SHARING_PHONE) {
                userStates.put(contact1.userId(), UserState.ENTERING_EMAIL);
                String welcomeMessage = "Please enter your gmail.\n Example: 123@gmail.com";
                bot.execute(new SendMessage(contact1.userId(), welcomeMessage));
            } else if (userStates.get(chatID) == UserState.ENTERING_EMAIL) {
                userStates.put(chatID, UserState.ENTERING_PASSWORD_FROM_MAIL);
                String welcomeMessage = "Password is sent to your gmail.\n Please check your email and enter the password.";
                bot.execute(new SendMessage(chatID, welcomeMessage));
            } else if (userStates.get(chatID) == UserState.ENTERING_PASSWORD_FROM_MAIL) {
                String welcomeMessage = "You have successfully registered!";
                bot.execute(new SendMessage(chatID, welcomeMessage));
            } else {
                String responseMessage = "You said: " + text;
                bot.execute(new SendMessage(chatID, responseMessage));
            }
        }
        else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            String data = callbackQuery.data();
            Long id = callbackQuery.from().id();

            System.out.println("Callback: " + data);
        }
    }
}
