package com.pdp;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.DeleteMessage;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class TelegramBotUpdateHandler {

    private final TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));
    private final ConcurrentHashMap<Long, State> userState = new ConcurrentHashMap<>();

    public void handler(Update update){
        Message message = update.message();
        CallbackQuery callbackQuery = update.callbackQuery();
        if (message!=null){

            Chat chat = message.chat();
            Long chatID = chat.id();
            String text = message.text();

            if (text.equals("/start")){

            }

            else {
                DeleteMessage deleteMessage = new DeleteMessage(chatID, message.messageId());
                bot.execute(deleteMessage);
            }

        } else {
            System.out.println(callbackQuery.data());
        }
    }
}
enum State{

}