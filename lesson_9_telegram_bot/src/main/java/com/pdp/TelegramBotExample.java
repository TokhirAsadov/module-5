package com.pdp;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendAudio;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class TelegramBotExample {


    public static void main(String[] args) throws IOException {
        TelegramBot bot = new TelegramBot("8031796169:AAF5l7wsaTL65jL7CA1UJYCjg66fTN1md8s");

        //sendMessageWithReplayKeyboard(bot);

        //sendMessageWithInlineKeyboard(bot);

        //sendDocument(bot);

        //sendPhoto(bot);
        //sendAudio(bot);
        //sendMessage(bot);
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
