package com.pdp;

import com.pdp.enums.UserState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.*;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.GetFileResponse;

import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramBotUpdateHandler {

    private static final TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));

    public static ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, List<String>> userPhotos = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, List<String>> userDocuments = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Long, List<String>> userAudios = new ConcurrentHashMap<>();

    public static void handler(Update update){

        if (update.message() != null
                &&
                update.message().audio() !=null
                &&
                userStates.get(update.message().from().id())==UserState.SHARING_AUDIO
        ){
            Long chatID = update.message().from().id();
            Audio audio = update.message().audio();
            String fileId = audio.fileId();
            if (userAudios.get(chatID) == null){
                userAudios.put(chatID, List.of(fileId));
            } else {
                userAudios.get(chatID).add(fileId);
            }

            String message = "Thank your for AUDIO/MUSIC submission.\n " +
                    "Please enter your name. \n " +
                    "Example: Ali Aliyev";

            userStates.put(chatID, UserState.ENTERING_NAME);
            bot.execute(new SendMessage(chatID, message));



            GetFile getFile = new GetFile(fileId);
            GetFileResponse fileResponse = bot.execute(getFile);
            String filePath = fileResponse.file().filePath();
            String downloadUrl = "https://api.telegram.org/file/bot" + bot.getToken() + "/" + filePath;
            System.out.println("Download URL: " + downloadUrl);

        }

        if (update.message() != null
        &&
                update.message().document() !=null
                &&
                userStates.get(update.message().from().id())==UserState.SHARING_DOCUMENT
        ){
            Long chatID = update.message().from().id();
            Document document = update.message().document();
            String fileId = document.fileId();
            if (userDocuments.get(chatID) == null){
                userDocuments.put(chatID, List.of(fileId));
            } else {
                userDocuments.get(chatID).add(fileId);
            }

            if (userPhotos.get(chatID) == null){
                userPhotos.put(chatID, List.of(fileId));
            } else {
                userPhotos.get(chatID).add(fileId);
            }

            String message = "Thank your for document submission.\n " +
                    "Please enter your name. \n " +
                    "Example: Ali Aliyev";

            userStates.put(chatID, UserState.ENTERING_NAME);
            bot.execute(new SendMessage(chatID, message));



            GetFile getFile = new GetFile(fileId);
            GetFileResponse fileResponse = bot.execute(getFile);
            String filePath = fileResponse.file().filePath();
            String downloadUrl = "https://api.telegram.org/file/bot" + bot.getToken() + "/" + filePath;
            System.out.println("Download URL: " + downloadUrl);

        }

        if (update.message() != null
            &&
                update.message().photo() != null
                &&
                userStates.get(update.message().from().id())==UserState.SHARING_PHOTO
        ){
            Long chatID = update.message().from().id();
            PhotoSize[] photo = update.message().photo();
            PhotoSize photoSize = photo[photo.length - 1];
            String fileId = photoSize.fileId();

            if (userPhotos.get(chatID) == null){
                userPhotos.put(chatID, List.of(fileId));
            } else {
                userPhotos.get(chatID).add(fileId);
            }

            String message = "Thank your for photo submission.\n " +
                    "Please enter your name. \n " +
                    "Example: Ali Aliyev";

            userStates.put(chatID, UserState.ENTERING_NAME);
            bot.execute(new SendMessage(chatID, message));



            GetFile getFile = new GetFile(fileId);
            GetFileResponse fileResponse = bot.execute(getFile);
            String filePath = fileResponse.file().filePath();
            String downloadUrl = "https://api.telegram.org/file/bot" + bot.getToken() + "/" + filePath;
            System.out.println("Download URL: " + downloadUrl);

        }

        if(
                update.message()!=null
                &&
                update.message().contact()!=null
                &&
                userStates.get(update.message().from().id())==UserState.SHARING_PHONE
        ){
            Long chatID = update.message().from().id();
            userStates.put(chatID, UserState.ENTERING_EMAIL);
            String message = "Please, enter your email. \n Example: abc@gmail.com";
            SendMessage sendMessage = new SendMessage(chatID, message);
            sendMessage.replyMarkup(new ReplyKeyboardRemove(true));
            bot.execute(sendMessage);
        }

        if (update.message()!= null) {
            Long chatID = update.message().from().id();
            String text = update.message().text();
            User from = update.message().from();

            if (text.equals("/start")) {
                userStates.put(chatID, UserState.LANGUAGE);
                String welcomeMessage = "Welcome to the bot!\n Please CHOOSE LANGUAGE.";

                SendMessage sendMessage = new SendMessage(chatID, welcomeMessage);
                InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                replyMarkup.addRow(
                        new InlineKeyboardButton("\uD83C\uDDFA\uD83C\uDDFF Uzbek").callbackData("uz")
                );
                replyMarkup.addRow(
                        new InlineKeyboardButton("\uD83C\uDDEC\uD83C\uDDE7 English").callbackData("en"),
                        new InlineKeyboardButton("\uD83C\uDDF7\uD83C\uDDFA Russian").callbackData("ru")
                );

                sendMessage.replyMarkup(
                        replyMarkup
                );
                bot.execute(sendMessage);
            } else if (userStates.get(chatID) == UserState.ENTERING_NAME) {

                // send photo
//                List<String> photosIds = userPhotos.get(chatID);
//                photosIds.forEach(fileId -> {
//                    bot.execute(new SendPhoto(chatID, fileId).caption("Your photo:"));
//                });

                // send document
//                List<String> photosIds = userDocuments.get(chatID);
//                photosIds.forEach(fileId -> {
//                    bot.execute(new SendDocument(chatID, fileId).caption("Your document"));
//                });

                List<String> audiosIds = userAudios.get(chatID);
                audiosIds.forEach(fileId -> {
                    bot.execute(new SendAudio(chatID, fileId).caption("Your MUSIC"));
                });

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

            } else if (userStates.get(chatID) == UserState.SHARING_PHONE && text != null) {
                bot.execute(new SendMessage(chatID, "Please share your contact using the button below."));
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
            Long chatID = callbackQuery.from().id();

            if (userStates.get(chatID) == UserState.LANGUAGE){
//                userStates.put(chatID, UserState.ENTERING_NAME);
//                userStates.put(chatID, UserState.SHARING_PHOTO);
//                userStates.put(chatID, UserState.SHARING_DOCUMENT);
                userStates.put(chatID, UserState.SHARING_AUDIO);
//                String message = "Your chose language: " + data+".\n Please enter your name. \n Example: Ali Aliyev";
//                String message = "Your chose language: " + data+".\n Please share your photo.";
                String message = "Your chose language: " + data+".\n Please share your FAMOUS MUSIC.";
                bot.execute(new DeleteMessage(chatID, callbackQuery.message().messageId()));
                bot.execute(new SendMessage(chatID, message));
            }

            System.out.println("Callback: " + data);
        }
    }
}
