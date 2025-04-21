package com.pdp;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramBotExample {

    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("settings");
    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    public static final ThreadLocal<TelegramBotUpdateHandler> threadLocal = new ThreadLocal<>();


    public static void main(String[] args) throws IOException {
        TelegramBot bot = new TelegramBot(resourceBundle.getString("bot.token"));


        bot.setUpdatesListener((updates) -> {
            updates.forEach(update -> {
                CompletableFuture.runAsync(()->{
                    threadLocal.get().handler(update);
                },executorService);
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);

        //getUpdatesV2(bot);

        //getUpdatesV1(bot);

        //sendMessageWithReplayKeyboard(bot);

//        sendMessageWithInlineKeyboard(bot);

        //sendDocument(bot);

        //sendPhoto(bot);
        //sendAudio(bot);
//        sendMessage(bot);
    }

    private static void getUpdatesV2(TelegramBot bot) {
        bot.setUpdatesListener((updates) -> {

            updates.forEach(update -> {

                if (update.message() != null) {
                    String text = update.message().text();
                    Long chatID = update.message().chat().id();

                    if (text.equals("/start")){
                        User from = update.message().from();
                        SendMessage sendMessage = new SendMessage(
                                chatID,
                                """
                                        Salom %s %s!
                                        Sizga qanday yordam bera olishim mumkin?
                                        \n
                                        Bot menusi:
                                        1. /start - Botni ishga tushirish
                                        2. /help - Yordam
                                        """.formatted(from.firstName(), from.lastName())
                        );
                        bot.execute(sendMessage);
                    } else if (text.equals("/help")) {
                        SendMessage sendMessage = new SendMessage(
                                chatID,
                                """
                                        Aslida hech qanday yordam mavjud emas ;)
                                        """);
                        bot.execute(sendMessage);

                    } else {
                        DeleteMessage deleteMessage = new DeleteMessage(
                                chatID,
                                update.message().messageId()
                        );

                        bot.execute(deleteMessage);
                    }

                } else if (update.callbackQuery() != null) {
                    CallbackQuery callbackQuery = update.callbackQuery();
                    String data = callbackQuery.data();
                    Long id = callbackQuery.from().id();

                    System.out.println("Callback: " + data);
                }
            });

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private static void getUpdatesV1(TelegramBot bot) {
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> list) {

                list.forEach(update -> {

                    if (update.message() != null) {
                        Chat chat = update.message().chat();
                        Long chatID = chat.id();
                        String text = update.message().text();
                        System.out.println("Message: " + text);
                        SendMessage sendMessage = new SendMessage(
                                chatID,
                                """
                                        Your sent this message:\n
                                        %s
                                        """.formatted(text)

                        );

                        bot.execute(sendMessage);
                    } else if (update.callbackQuery() != null) {
                        CallbackQuery callbackQuery = update.callbackQuery();
                        String data = callbackQuery.data();

                        Long id = callbackQuery.from().id();

                        SendMessage sendMessage = new SendMessage(
                                id,
                                """
                                        Your sent this message:\n
                                        %s
                                        """.formatted(data)

                        );
                        bot.execute(sendMessage);
                        System.out.println("Callback: " + data);
                    }
                });

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    private static void sendMessageWithReplayKeyboard(TelegramBot bot) {
        SendMessage sendMessage = new SendMessage(
                7567495333L,
                "Iltimos, contact | location ningizni share qiling.\n Yoki sozlamarga o`ting!"
        );

        KeyboardButton contact = new KeyboardButton("\uD83D\uDCDE Contact");
        contact.requestContact(true);
        KeyboardButton location = new KeyboardButton("\uD83D\uDCCD Location");
        location.requestLocation(true);
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton[]{contact, location},
                new KeyboardButton[]{new KeyboardButton("⚙\uFE0F Settings")}
        );
        replyMarkup.resizeKeyboard(true);
        sendMessage.replyMarkup(replyMarkup);
        bot.execute(sendMessage);
    }

    private static void sendMessageWithInlineKeyboard(TelegramBot bot) {
        SendMessage sendMessage = new SendMessage(
                7567495333L,
                "Iltimos tilni tanlang!"
        );
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
    }

    private static void sendDocument(TelegramBot bot) {
        SendDocument sendDocument = new SendDocument(
                7567495333L,
                new File("D:\\pdp\\docs\\Themes\\Java\\Module-5\\9. Telegram bot\\5.9. Telegram bot.pdf")
//                Files.readAllBytes(Path.of("D:\\pdp\\docs\\Themes\\Java\\Module-5\\9. Telegram bot\\5.9. Telegram bot.pdf"))
        );
        sendDocument.contentType("application/pdf");

        sendDocument.caption("Java Telegram bot\nDocument caption");

        bot.execute(sendDocument);
        System.out.println("Document sent successfully!");
    }

    private static void sendPhoto(TelegramBot bot) throws IOException {
        SendPhoto sendPhoto = new SendPhoto(
                7567495333L,
                Files.readAllBytes(Path.of("D:\\life rool.jpg"))
        );
        sendPhoto.caption("Buni hayot deydi.\nPhoto caption");
        bot.execute(sendPhoto);
        System.out.println("Photo sent successfully!");
    }

    private static void sendAudio(TelegramBot bot) throws IOException {
        SendAudio sendAudio = new SendAudio(
                7567495333L,
                Files.readAllBytes(Path.of("C:\\Users\\guval\\Downloads\\Telegram Desktop\\Abrobey_Bolalik_zo’r_bo’lardi_yiqilsam_yugurib_otam_kelardi.mp3"))
        );
        sendAudio.caption("Bolik haqida ko`cha qushig`i.\nAudio file caption");
        bot.execute(sendAudio);
        System.out.println("Audio sent successfully!");
    }

    private static void sendMessage(TelegramBot bot) {
        SendMessage sendMessage = new SendMessage(
                7567495333L,
                "Hello, this is a test message from my Java Telegram bot!"
        );
        bot.execute(sendMessage);
        System.out.println("Message sent successfully!");
    }
}
