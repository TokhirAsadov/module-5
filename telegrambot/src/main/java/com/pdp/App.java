package com.pdp;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Hello world!
 *
 */
public class App{

    private static final ResourceBundle settings = ResourceBundle.getBundle("settings");

    private static final ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    private static final ThreadLocal<TelegramBotUpdateHandler> telegramBotUpdateHandler = ThreadLocal.withInitial(TelegramBotUpdateHandler::new);

    @SneakyThrows
    public static void main(String[] args ) {
        TelegramBot bot = new TelegramBot(settings.getString("bot.token"));

        bot.setUpdatesListener((updates)->{
            for (Update update : updates) {
                CompletableFuture.runAsync(()->{
                    telegramBotUpdateHandler.get().handler(update);
                },executorService);
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;

        }, Throwable::printStackTrace);

        //extracted(bot);

        //sendAudio(bot);

        //sendPhoto(bot);

        //sendMessage(bot);
    }

    private static void extracted(TelegramBot bot) {
        bot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> list) {
                for (Update update : list){
                    Message message = update.message();
                    String text = message.text();
                    Chat chat = message.chat();
                    Long chatID = chat.id();
                    SendMessage sendMessage = new SendMessage(chatID, "Replied to: " + text);
                    bot.execute(sendMessage);
                }
                return CONFIRMED_UPDATES_ALL;
            }
        }, new ExceptionHandler() {
            @Override
            public void onException(TelegramException e) {

            }
        });
    }

    private static void sendAudio(TelegramBot bot) throws IOException {
        SendAudio sendAudio = new SendAudio("7567495333", Files.readAllBytes(Path.of("C:\\Users\\guval\\Downloads\\Telegram Desktop\\Abrobey_Bolalik_zo’r_bo’lardi_yiqilsam_yugurib_otam_kelardi.mp3")));
        sendAudio.caption("Bolalik..");
        bot.execute(sendAudio);
    }

    private static void sendPhoto(TelegramBot bot) throws IOException {
        SendPhoto sendPhoto = new SendPhoto("7567495333", Files.readAllBytes(Path.of("D:\\life rool.jpg")));
        sendPhoto.caption("Buni hayot uchun shior qilib ol.");
        bot.execute(sendPhoto);
    }

    private static void sendMessage(TelegramBot bot) {
        SendMessage sendMessage = new SendMessage("7567495333", "Hello From Simple test bot");

        KeyboardButton contact = new KeyboardButton("Contact");
        contact.requestContact(true);
        KeyboardButton[] row1 = {
                contact,
                new KeyboardButton("Location"),
        };
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(row1);
        replyMarkup.addRow(new KeyboardButton("Settings"));
        replyMarkup.resizeKeyboard(true);
        sendMessage.replyMarkup(replyMarkup);

//        sendMessage.replyMarkup(new ReplyKeyboardMarkup(new String[][]{
//                {"contact","location"},
//                {"settings"},
//        }));
        bot.execute(sendMessage);
    }
}
