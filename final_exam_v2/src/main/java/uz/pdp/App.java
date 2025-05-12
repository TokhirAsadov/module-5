package uz.pdp;


import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import uz.pdp.database.DB;
import uz.pdp.entity.User;
import uz.pdp.handle.UpdateHandler;

import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

public class App {
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("settings");
    private static final ThreadLocal<UpdateHandler> threadLocal = ThreadLocal.withInitial(UpdateHandler::new);

    public static void main( String[] args ) {
        TelegramBot bot = new TelegramBot(resourceBundle.getString("bot.token"));
        adminAdder();
        bot.setUpdatesListener(updates -> {
            updates.forEach(update -> {
                CompletableFuture.runAsync(() -> {
                    threadLocal.get().handle(update);
                });
            });
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, Throwable::printStackTrace);
    }

    private static void adminAdder(){
        DB.users.add(User.builder()
                .chatID(Long.valueOf(resourceBundle.getString("bot.adminID")))
                .build());
    }
}
