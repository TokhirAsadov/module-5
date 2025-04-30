package com.pdp;

import com.pdp.database.DB;
import com.pdp.entity.History;
import com.pdp.entity.Product;
import com.pdp.entity.User;
import com.pdp.entity.UserState;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateHandler {

    private static ConcurrentHashMap<Long, UserState> userStates = new ConcurrentHashMap<>();
    private static Stack<Product> productsStack = new Stack<>();

    public static void handle(Update update) {
        TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));

        if (update.message() != null) {
            System.out.println(update.message().text());
            System.out.println(update.message().chat().id());
            Long chatID = update.message().from().id();
            if (userStates.get(chatID)==null) {
                userStates.put(chatID,UserState.START);
            }

            switch (userStates.get(chatID)){
                case START -> {
                    start(bot, update);
                }
                case LOGIN -> {
                    login(bot, update);
                }
                case PASSWORD -> {
                    password(bot, update);
                }
                case ADMIN_MENU -> {
                    adminMenu(bot, update);
                }
                case ADD_PRODUCT_NAME -> {
                    addProductName(bot, update);
                }
                case ADD_PRODUCT_PRICE -> {
                    addProductPrice(bot, update);
                }
                case ADD_PRODUCT_QUANTITY -> {
                    addProductQuantity(bot, update);
                }
                case ADD_PRODUCT_PHOTO -> {
                    addProductPhoto(bot, update);
                }
                case CHOOSE_PRODUCT -> {
                    chooseProductSecondWay(bot, update);
                }
                case SHOW_PRODUCT -> {
                    showProductSecondWay(bot, update);
                }
                case USER_MENU -> {
                    userMenu(bot, update);
                }

            }


        } else if (update.callbackQuery() != null) {
            CallbackQuery callbackQuery = update.callbackQuery();
            Long chatID = callbackQuery.from().id();
            String data = callbackQuery.data();

            switch (userStates.get(chatID)){
                case CHOOSE_PRODUCT -> {
                    chooseProduct(bot, callbackQuery);
                }
                case SHOW_PRODUCT -> {
                    showProduct(bot, callbackQuery);
                }
                case CHOOSE_PRODUCT_FOR_USER -> {
                    chooseProductForUser(bot, callbackQuery);
                }
                case SHOW_PRODUCT_FOR_USER -> {
                    showProductForUser(bot, callbackQuery);
                }
                case CHOOSE_HISTORY_FOR_USER -> {
                    chooseHistoryForUser(bot, callbackQuery);
                }
            }
        }
    }

    private static void chooseHistoryForUser(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        Long chatID = callbackQuery.from().id();
        System.out.println("data: "+data);
        if (data.startsWith("prev_")) {
            String firstHistoryId = data.substring(data.indexOf("_") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger count= new AtomicInteger();
            List<UUID> historiesIds = new ArrayList<>();

            AtomicInteger n = new AtomicInteger(0);
            for (int coreCount = 0; coreCount < DB.histories.size(); coreCount++) {
                if (DB.histories.get(coreCount).getId().toString().equals(firstHistoryId)) {
                    n.set(coreCount);
                    break;
                }
            }
            System.out.println("---- "+n.get());
            DB.histories.stream()
                    .skip(n.get()-2)
                    .limit(2)
                    .forEach(history -> {
                        historiesIds.add(history.getId());
                        stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                    });

            EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "You can see YOUR HISTORY:\n_____________________________________\n%s".formatted(stringBuilder));
            sendMessage.replyMarkup(buttons(historiesIds));
            bot.execute(sendMessage);
        }
        else if (data.startsWith("next_")) {
            String lastHistoryId = data.substring(data.indexOf("_") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger count= new AtomicInteger();
            List<UUID> historiesIds = new ArrayList<>();

            AtomicInteger n = new AtomicInteger(0);
            for (int coreCount = 0; coreCount < DB.histories.size(); coreCount++) {
                if (DB.histories.get(coreCount).getId().toString().equals(lastHistoryId)) {
                    n.set(coreCount);
                    break;
                }
            }
            System.out.println("---- "+n.get());
            DB.histories.stream()
                    .skip(n.get()+1)
                    .limit(2)
                    .forEach(history -> {
                        historiesIds.add(history.getId());
                        stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                    });

            EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "You can see YOUR HISTORY:\n_____________________________________\n%s".formatted(stringBuilder));

            if (historiesIds.size() == 0) {
                AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                        .text("No more history")
                        .showAlert(true);
                bot.execute(noMoreProducts);
            } else {
                sendMessage.replyMarkup(buttons(historiesIds));
                bot.execute(sendMessage);
            }
        }
        else if (data.equals("reject")) {
            System.out.println("reject ------------------");
            DB.users.stream().filter(user -> user.getLogin().equals("admin") && user.getChatId().equals(chatID))
                    .findFirst()
                    .ifPresentOrElse(user -> {
                        userStates.put(chatID, UserState.ADMIN_MENU);

                    },()->{
                        userStates.put(chatID, UserState.USER_MENU);
                    });
            bot.execute(new DeleteMessage(chatID, callbackQuery.message().messageId()));
        }
        else {
            String historyId = data.substring(data.indexOf("_") + 1);
            DB.histories.stream().filter(product -> product.getId().equals(UUID.fromString(historyId)))
                    .findFirst()
                    .ifPresentOrElse(history -> {

                        userStates.put(chatID, UserState.USER_MENU);

                        SendPhoto sendPhoto = new SendPhoto(chatID, history.getFileId())
                                .caption("""
                                            Product name: %s
                                            Product price: %s
                                            Product quantity: %s
                                            """.formatted(history.getName(), history.getPrice(), history.getQuantity()))
                                .replyMarkup(
                                        new ReplyKeyboardMarkup("Show Products","History").resizeKeyboard(true)
                                );

                        bot.execute(sendPhoto);
                    }, () -> {
                        bot.execute(new SendMessage(chatID, "Product not found!"));
                    });
        }
    }

    private static void showProductForUser(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        Long chatID = callbackQuery.from().id();
        System.out.println("data: "+data);
        if (data.startsWith("d_")) {
            String historyId = data.substring(data.indexOf("_") + 1);
            DB.histories.stream().filter(history -> history.getId().toString().equals(historyId))
                    .findFirst()
                    .ifPresentOrElse(history -> {
                        if (history.getQuantity() > 1) {
                            history.setQuantity(history.getQuantity() - 1);
                            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("➖")
                                            .callbackData("d_" + history.getId()),
                                    new InlineKeyboardButton(history.getQuantity().toString())
                                            .callbackData(history.getQuantity().toString()),
                                    new InlineKeyboardButton("➕")
                                            .callbackData("i_" + history.getId())
                            );
                            replyMarkup.addRow(
                                    new InlineKeyboardButton("Xarid qilish")
                                            .callbackData("add_" + history.getId())
                            );
                            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatID, callbackQuery.message().messageId())
                                    .replyMarkup(replyMarkup);
                            bot.execute(editMessageReplyMarkup);
                        }
                        else {
                            bot.execute(new AnswerCallbackQuery(callbackQuery.id())
                                    .text("Product quantity is 1")
                                    .showAlert(true));
                        }

                    }, () -> {
                        bot.execute(new SendMessage(chatID, "Product not found!"));
                    });
        }
        else if (data.startsWith("i_")) {
            String historyId = data.substring(data.indexOf("_") + 1);
            DB.histories.stream().filter(history -> history.getId().toString().equals(historyId))
                    .findFirst()
                    .ifPresentOrElse(history -> {
                        history.setQuantity(history.getQuantity() + 1);
                        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup(
                                new InlineKeyboardButton("➖")
                                        .callbackData("d_" + history.getId()),
                                new InlineKeyboardButton(history.getQuantity().toString())
                                        .callbackData(history.getQuantity().toString()),
                                new InlineKeyboardButton("➕")
                                        .callbackData("i_" + history.getId())
                        );
                        replyMarkup.addRow(
                                new InlineKeyboardButton("Xarid qilish")
                                        .callbackData("add_" + history.getId())
                        );
                        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatID, callbackQuery.message().messageId())
                                .replyMarkup(replyMarkup);
                        bot.execute(editMessageReplyMarkup);
                    }, () -> {
                        bot.execute(new SendMessage(chatID, "Product not found!"));
                    });

        } else if (data.startsWith("add_")) {
            String historyId = data.substring(data.indexOf("_") + 1);
            DB.histories.stream().filter(history -> history.getId().toString().equals(historyId))
                    .findFirst()
                    .ifPresentOrElse(history -> {

                        userStates.put(chatID, UserState.USER_MENU);
                        history.setIsSold(true);
                        bot.execute(new DeleteMessage(chatID, callbackQuery.message().messageId()));
                        SendMessage sendMessage = new SendMessage(chatID, "Xarid qilindi!");
                        sendMessage.replyMarkup(new ReplyKeyboardMarkup("Show Products", "History").resizeKeyboard(true));
                        bot.execute(sendMessage);

                    }, () -> {
                        bot.execute(new SendMessage(chatID, "Product not found!"));
                    });
        }
    }

    private static void chooseProductForUser(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        Long chatID = callbackQuery.from().id();
        System.out.println("data: "+data);
        if (data.startsWith("prev_")) {
            String firstProductId = data.substring(data.indexOf("_") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger count= new AtomicInteger();
            List<UUID> productIds = new ArrayList<>();

            AtomicInteger n = new AtomicInteger(0);
            for (int coreCount = 0; coreCount < DB.products.size(); coreCount++) {
                if (DB.products.get(coreCount).getId().toString().equals(firstProductId)) {
                    n.set(coreCount);
                    break;
                }
            }
            System.out.println("---- "+n.get());
            DB.products.stream()
                    .skip(n.get()-2)
                    .limit(2)
                    .forEach(product -> {
                        productIds.add(product.getId());
                        stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                    });

            EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "You can see products:\n_____________________________________\n%s".formatted(stringBuilder));
            System.out.println(productIds);
            sendMessage.replyMarkup(buttons(productIds));
            bot.execute(sendMessage);
        }
        else if (data.startsWith("next_")) {
            String lastProductId = data.substring(data.indexOf("_") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger count= new AtomicInteger();
            List<UUID> productIds = new ArrayList<>();

            AtomicInteger n = new AtomicInteger(0);
            for (int coreCount = 0; coreCount < DB.products.size(); coreCount++) {
                if (DB.products.get(coreCount).getId().toString().equals(lastProductId)) {
                    n.set(coreCount);
                    break;
                }
            }
            System.out.println("---- "+n.get());
            DB.products.stream()
                    .skip(n.get()+1)
                    .limit(2)
                    .forEach(product -> {
                        productIds.add(product.getId());
                        stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                    });

            EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "You can see products:\n_____________________________________\n%s".formatted(stringBuilder));
            System.out.println(productIds);

            if (productIds.size() == 0) {
                AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                        .text("No more products")
                        .showAlert(true);
                bot.execute(noMoreProducts);
            } else {
                sendMessage.replyMarkup(buttons(productIds));
                bot.execute(sendMessage);
            }
        }
        else if (data.equals("reject")) {
            System.out.println("reject ------------------");
            DB.users.stream().filter(user -> user.getLogin().equals("admin") && user.getChatId().equals(chatID))
                    .findFirst()
                    .ifPresentOrElse(user -> {
                        userStates.put(chatID, UserState.ADMIN_MENU);

                    },()->{
                        userStates.put(chatID, UserState.USER_MENU);
                    });
            DB.users.stream().filter(user -> !user.getLogin().equals("admin") && user.getChatId().equals(chatID))
                    .findFirst()
                    .ifPresent(user -> {
                        userStates.put(chatID, UserState.USER_MENU);
                    });
            bot.execute(new DeleteMessage(chatID, callbackQuery.message().messageId()));
        }
        else {
            String productId = data.substring(data.indexOf("_") + 1);
            DB.products.stream().filter(product -> product.getId().equals(UUID.fromString(productId)))
                    .findFirst()
                    .ifPresentOrElse(product -> {
                        History history = History.builder()
                                .id(UUID.randomUUID())
                                .userId(chatID)
                                .productId(product.getId())
                                .fileId(product.getFileId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .quantity(1)
                                .isSold(false)
                                .build();
                        DB.histories.add(history);

                        userStates.put(chatID, UserState.SHOW_PRODUCT_FOR_USER);
                        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                        InlineKeyboardButton decrement = new InlineKeyboardButton("➖")
                                .callbackData("d_"+history.getId());
                        InlineKeyboardButton value = new InlineKeyboardButton("1")
                                .callbackData("1");
                        InlineKeyboardButton increment = new InlineKeyboardButton("➕")
                                .callbackData("i_"+history.getId());
                        replyMarkup.addRow(decrement,value,increment);
                        InlineKeyboardButton addBasket = new InlineKeyboardButton("Xarid qilish")
                                .callbackData("add_"+history.getId());
                        replyMarkup.addRow(addBasket);
                        SendPhoto sendPhoto = new SendPhoto(chatID, product.getFileId())
                                .caption("""
                                            Product name: %s
                                            Product price: %s
                                            Product quantity: %s
                                            """.formatted(product.getName(), product.getPrice(), product.getQuantity()))
                                .replyMarkup(replyMarkup);

                        bot.execute(sendPhoto);
                    }, () -> {
                        bot.execute(new SendMessage(chatID, "Product not found!"));
                    });
        }
    }

    private static void userMenu(TelegramBot bot, Update update) {
        Long chatID = update.message().from().id();
        String text = update.message().text().trim();
        switch (text){
            case "Show Products" -> {
                StringBuilder stringBuilder = new StringBuilder();
                AtomicInteger count= new AtomicInteger();
                List<UUID> productIds = new ArrayList<>();
                DB.products.stream()
                        .limit(2)
                        .forEach(product -> {
                            productIds.add(product.getId());
                            stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                        });


                if (productIds.size()==0) {
                    userStates.put(chatID, UserState.USER_MENU);
                    SendMessage sendMessage = new SendMessage(chatID, "No products found!");
                    ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup("Show Products","History");
                    replyMarkup.resizeKeyboard(true);
                    sendMessage.replyMarkup(replyMarkup);
                    bot.execute(sendMessage);
                }  else {
                    userStates.put(chatID, UserState.CHOOSE_PRODUCT_FOR_USER);
                    SendMessage sendMessage = new SendMessage(chatID, "You can see products:\n_____________________________________\n%s".formatted(stringBuilder));
                    System.out.println(productIds);
                    sendMessage.replyMarkup(buttons(productIds));
                    bot.execute(sendMessage);
                }
            }
            case "History" -> {
                userStates.put(chatID, UserState.SHOW_HISTORY);
                StringBuilder stringBuilder = new StringBuilder();
                AtomicInteger count= new AtomicInteger();
                List<UUID> historiesIds = new ArrayList<>();
                DB.histories.stream()
                        .limit(2)
                        .forEach(history -> {
                            historiesIds.add(history.getId());
                            stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                        });


                if (historiesIds.size()==0) {
                    userStates.put(chatID, UserState.USER_MENU);
                    SendMessage sendMessage = new SendMessage(chatID, "No histories found!");
                    ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup("Show Products","History");
                    replyMarkup.resizeKeyboard(true);
                    sendMessage.replyMarkup(replyMarkup);
                    bot.execute(sendMessage);
                }  else {
                    userStates.put(chatID, UserState.CHOOSE_HISTORY_FOR_USER);
                    SendMessage sendMessage = new SendMessage(chatID, "You can see YOUR HISTORIES:\n_____________________________________\n%s".formatted(stringBuilder));
                    sendMessage.replyMarkup(buttons(historiesIds));
                    bot.execute(sendMessage);
                }
            }
        }
    }

    private static void showProductSecondWay(TelegramBot bot, Update update) {
        if (update.message().text().equals("\uD83D\uDD19 Back")){
            userStates.put(update.message().from().id(), UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(update.message().from().id(), "Admin Menu:");
            ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Add Product", "Show Products"},
                    new String[]{"Show Users", "Show Orders"},
                    new String[]{"Settings"}
            );
            replyMarkup.resizeKeyboard(true);
            sendMessage.replyMarkup(replyMarkup);
            bot.execute(sendMessage);
        } else {
            bot.execute(new SendMessage(update.message().from().id(), "Please, click edit or delete button")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new String[]{"\uD83D\uDD19 Back"}
                    ).resizeKeyboard(true)));
        }
    }

    private static void showProduct(TelegramBot bot, CallbackQuery callbackQuery) {
        Long chatID = callbackQuery.from().id();
        String data = callbackQuery.data();
        DB.users.stream().filter(user -> user.getLogin().equals("admin") && user.getChatId().equals(chatID))
                .findFirst()
                .ifPresentOrElse(
                        (user)->{
                            String productId = data.substring(data.indexOf("_") + 1);
                            if (data.startsWith("edit_")){
                                //todo edit product--------------------------
                            } else if (data.startsWith("delete_")) {
                                DB.products.stream().filter(product -> product.getId().equals(UUID.fromString(productId)))
                                        .findFirst()
                                        .ifPresentOrElse(product -> {
                                            DB.products.remove(product);
                                            userStates.put(chatID, UserState.ADMIN_MENU);
                                            bot.execute(new SendMessage(chatID, "Product deleted successfully!"));
                                        }, () -> {
                                            bot.execute(new SendMessage(chatID, "Product not found!"));
                                        });
                            }
                        },
                        ()->{
                            bot.execute(new SendMessage(chatID, "Product not found!"));
                        }
                );
    }

    private static void chooseProductSecondWay(TelegramBot bot, Update update) {
        String text = update.message().text();
        Long chatID = update.message().from().id();
        if (text.equals("\uD83D\uDD19Back")){
            userStates.put(chatID, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatID, "You can see admin menu:");
            ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                    new String[]{"Add Product", "Show Products"},
                    new String[]{"Show Users", "Show Orders"},
                    new String[]{"Settings"}
            );
            replyMarkup.resizeKeyboard(true);
            sendMessage.replyMarkup(replyMarkup);
            bot.execute(sendMessage);
        }
        else {
            SendMessage sendMessage = new SendMessage(chatID, "Please choose product")
                    .replyMarkup(new ReplyKeyboardMarkup(
                            new String[]{"\uD83D\uDD19Back"}
                    ).resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void chooseProduct(TelegramBot bot, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        Long chatID = callbackQuery.from().id();
        System.out.println("data: "+data);
        if (data.startsWith("prev_")) {
            String firstProductId = data.substring(data.indexOf("_") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger count= new AtomicInteger();
            List<UUID> productIds = new ArrayList<>();

            AtomicInteger n = new AtomicInteger(0);
            for (int coreCount = 0; coreCount < DB.products.size(); coreCount++) {
                if (DB.products.get(coreCount).getId().toString().equals(firstProductId)) {
                    n.set(coreCount);
                    break;
                }
            }
            System.out.println("---- "+n.get());
            DB.products.stream()
                    .skip(n.get()-2)
                    .limit(2)
                    .forEach(product -> {
                        productIds.add(product.getId());
                        stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                    });

            EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "You can see products:\n_____________________________________\n%s".formatted(stringBuilder));
            System.out.println(productIds);
            sendMessage.replyMarkup(buttons(productIds));
            bot.execute(sendMessage);
        } else if (data.startsWith("next_")) {
            String lastProductId = data.substring(data.indexOf("_") + 1);
            StringBuilder stringBuilder = new StringBuilder();
            AtomicInteger count= new AtomicInteger();
            List<UUID> productIds = new ArrayList<>();

            AtomicInteger n = new AtomicInteger(0);
            for (int coreCount = 0; coreCount < DB.products.size(); coreCount++) {
                if (DB.products.get(coreCount).getId().toString().equals(lastProductId)) {
                    n.set(coreCount);
                    break;
                }
            }
            System.out.println("---- "+n.get());
            DB.products.stream()
                    .skip(n.get()+1)
                    .limit(2)
                    .forEach(product -> {
                        productIds.add(product.getId());
                        stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                    });

            EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "You can see products:\n_____________________________________\n%s".formatted(stringBuilder));
            System.out.println(productIds);

            if (productIds.size() == 0) {
                AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                        .text("No more products")
                        .showAlert(true);
                bot.execute(noMoreProducts);
            } else {
                sendMessage.replyMarkup(buttons(productIds));
                bot.execute(sendMessage);
            }
        } else if (data.equals("reject")) {
                DB.users.stream().filter(user -> user.getLogin().equals("admin") && user.getChatId().equals(chatID))
                        .findFirst()
                        .ifPresentOrElse(user -> {
                            userStates.put(chatID, UserState.ADMIN_MENU);

                        },()->{
                            userStates.put(chatID, UserState.USER_MENU);
                        });
                bot.execute(new DeleteMessage(chatID, callbackQuery.message().messageId()));
        } else {
                String productId = data.substring(data.indexOf("_") + 1);
                System.out.println(productId);
                DB.products.stream().filter(product -> product.getId().equals(UUID.fromString(productId)))
                        .findFirst()
                        .ifPresentOrElse(product -> {
                            userStates.put(chatID, UserState.SHOW_PRODUCT);

                            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                            InlineKeyboardButton edit = new InlineKeyboardButton("✏️ Edit")
                                    .callbackData("edit_"+product.getId());
                            InlineKeyboardButton delete = new InlineKeyboardButton("\uD83D\uDDD1 Delete")
                                    .callbackData("delete_"+product.getId());
                            replyMarkup.addRow(edit,delete);
                            SendPhoto sendPhoto = new SendPhoto(chatID, product.getFileId())
                                    .caption("""
                                            Product name: %s
                                            Product price: %s
                                            Product quantity: %s
                                            """.formatted(product.getName(), product.getPrice(), product.getQuantity()))
                                    .replyMarkup(replyMarkup);

                            bot.execute(sendPhoto);
                        }, () -> {
                            bot.execute(new SendMessage(chatID, "Product not found!"));
                        });
        }

    }

    private static void addProductPhoto(TelegramBot bot, Update update) {
        Long chatID = update.message().from().id();
        String text = update.message().text();
        if (text !=null ) {
            if (text.equals("Cancel")) {
                userStates.put(chatID, UserState.ADMIN_MENU);
                productsStack.pop();
                SendMessage sendMessage = new SendMessage(chatID, "You have cancelled the operation.");
                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                        new String[]{"Add Product", "Show Products"},
                        new String[]{"Show Users", "Show Orders"},
                        new String[]{"Settings"}
                );
                replyMarkup.resizeKeyboard(true);
                sendMessage.replyMarkup(replyMarkup);
                bot.execute(sendMessage);
            } else {
                bot.execute(new SendMessage(chatID, "Please, send product photo")
                        .replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true)));
            }
        } else if (update.message().photo() != null) {
            PhotoSize[] photo = update.message().photo();
            PhotoSize photoSize = photo[photo.length - 1];
            String fileId = photoSize.fileId();
            productsStack.peek()
                    .setFileId(fileId);
            Product pop = productsStack.pop();
            DB.products.add(pop);

            userStates.put(chatID, UserState.ADMIN_MENU);
            productsStack.clear();

            SendPhoto sendPhoto = new SendPhoto(chatID, fileId)
                    .caption("""
                            Product name: %s
                            Product price: %s
                            Product quantity: %s
                            """.formatted(pop.getName(), pop.getPrice(), pop.getQuantity()));
            bot.execute(sendPhoto);
            SendMessage sendMessage = new SendMessage(chatID, "%s product is created successfully!.".formatted(pop.getName()));
            sendMessage.replyMarkup(new ReplyKeyboardMarkup(
                    new String[]{"Add Product", "Show Products"},
                    new String[]{"Show Users", "Show Orders"},
                    new String[]{"Settings"}
            ).resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void addProductQuantity(TelegramBot bot, Update update) {
        Long chatID = update.message().from().id();
        String text = update.message().text();
        if (text.equals("Cancel")) {
            userStates.put(chatID, UserState.ADMIN_MENU);
            productsStack.pop();
            SendMessage sendMessage = new SendMessage(chatID, "You have cancelled the operation.");
                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                        new String[]{"Add Product", "Show Products"},
                        new String[]{"Show Users", "Show Orders"},
                        new String[]{"Settings"}
                );
                replyMarkup.resizeKeyboard(true);
                sendMessage.replyMarkup(replyMarkup);
                bot.execute(sendMessage);
        } else {
            userStates.put(chatID, UserState.ADD_PRODUCT_PHOTO);
            productsStack.peek()
                    .setQuantity(Integer.parseInt(text));
            SendMessage sendMessage = new SendMessage(chatID, "Please send product photo")
                    .replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void addProductPrice(TelegramBot bot, Update update) {
        Long chatID = update.message().from().id();
        String text = update.message().text();
        if (text.equals("Cancel")) {
            userStates.put(chatID, UserState.ADMIN_MENU);
            productsStack.pop();
            SendMessage sendMessage = new SendMessage(chatID, "You have cancelled the operation.");
                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                        new String[]{"Add Product", "Show Products"},
                        new String[]{"Show Users", "Show Orders"},
                        new String[]{"Settings"}
                );
                replyMarkup.resizeKeyboard(true);
                sendMessage.replyMarkup(replyMarkup);
                bot.execute(sendMessage);
        } else {
            userStates.put(chatID, UserState.ADD_PRODUCT_QUANTITY);
            productsStack.peek()
                    .setPrice(Double.parseDouble(text));
            SendMessage sendMessage = new SendMessage(chatID, "Please enter product quantity:")
                    .replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void addProductName(TelegramBot bot, Update update) {
        Long chatID = update.message().from().id();
        userStates.put(chatID, UserState.ADD_PRODUCT_PRICE);
        productsStack.push(Product.builder()
                        .id(UUID.randomUUID())
                .name(update.message().text())
                .build());
        SendMessage sendMessage = new SendMessage(chatID, "Please enter product price:")
                .replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
        bot.execute(sendMessage);
    }


    public static void adminMenu(TelegramBot bot, Update update){
        Long chatID = update.message().from().id();
        DB.users.stream().filter(user -> user.getLogin().equals("admin"))
                .findFirst()
                .ifPresentOrElse(user -> {
                    if (user.getChatId().equals(chatID)){
                        String text = update.message().text();
                        adminMenuAction(bot, update, text);
                    }
                }, () -> {
                    bot.execute(new SendMessage(chatID,"Something went wrong! Please try again..."));
                });
    }

    private static void adminMenuAction(TelegramBot bot, Update update, String text) {
        switch (text){
            case "Add Product" -> {
                userStates.put(update.message().from().id(), UserState.ADD_PRODUCT_NAME);
                SendMessage sendMessage = new SendMessage(update.message().from().id(), "Please enter product name:");
                sendMessage.replyMarkup(new ReplyKeyboardRemove(true));
                bot.execute(sendMessage);
            }
            case "Show Products" -> {
                StringBuilder stringBuilder = new StringBuilder();
                AtomicInteger count= new AtomicInteger();
                List<UUID> productIds = new ArrayList<>();
                DB.products.stream()
                        .limit(2)
                        .forEach(product -> {
                            productIds.add(product.getId());
                            stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                        });


                if (productIds.size()==0) {
                    userStates.put(update.message().from().id(), UserState.ADMIN_MENU);
                    SendMessage sendMessage = new SendMessage(update.message().from().id(), "No products found!");
                    ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                            new String[]{"Add Product", "Show Products"},
                            new String[]{"Show Users", "Show Orders"},
                            new String[]{"Settings"}
                    );
                    replyMarkup.resizeKeyboard(true);
                    sendMessage.replyMarkup(replyMarkup);
                    bot.execute(sendMessage);
                }  else {
                    userStates.put(update.message().from().id(), UserState.CHOOSE_PRODUCT);
                    SendMessage sendMessage = new SendMessage(update.message().from().id(), "You can see products:\n_____________________________________\n%s".formatted(stringBuilder));
                    System.out.println(productIds);
                    sendMessage.replyMarkup(buttons(productIds));
                    bot.execute(sendMessage);
                }

            }
            case "Show Users" -> {
                userStates.put(update.message().from().id(), UserState.SHOW_PRODUCTS);
                //todo-----------------------------------------------------------------------------
                bot.execute(new SendMessage(update.message().from().id(), "You can see users:"));
            }
            case "Show Orders" -> {
                userStates.put(update.message().from().id(), UserState.SHOW_ORDERS);
                //todo-----------------------------------------------------------------------------
                bot.execute(new SendMessage(update.message().from().id(), "You can see ORDERS:"));
            }
            case "Settings" -> {
                userStates.put(update.message().from().id(), UserState.SETTINGS);
                //todo-----------------------------------------------------------------------------
                bot.execute(new SendMessage(update.message().from().id(), "You can see SETTINGS:"));
            }
        }
    }

    private static InlineKeyboardMarkup buttons(List<UUID> productIds) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        StringBuilder lastProductId = new StringBuilder();
        StringBuilder firstProductId = new StringBuilder();
        for (int i = 1; i <= productIds.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i))
                    .callbackData("product_"+productIds.get(i-1));
            row.add(button);
            if (i % 5 == 0) {
                markup.addRow(row.toArray(new InlineKeyboardButton[0]));
                row.clear();
            }
            if (i==productIds.size()){
                UUID uuid = productIds.get(i - 1);
                lastProductId.append(uuid);
            }
            if (i==1){
                UUID uuid = productIds.get(i - 1);
                firstProductId.append(uuid);
            }
        }
        if (!row.isEmpty()) {
            markup.addRow(row.toArray(new InlineKeyboardButton[0]));
        }
        if (!row.isEmpty()) {
            row.clear();
            InlineKeyboardButton prev = new InlineKeyboardButton("⏮\uFE0F")
                    .callbackData("prev_"+ firstProductId);
            InlineKeyboardButton reject = new InlineKeyboardButton("❌")
                    .callbackData("reject");
            InlineKeyboardButton next = new InlineKeyboardButton("⏭\uFE0F")
                    .callbackData("next_"+ lastProductId);
            row.add(prev);
            row.add(reject);
            row.add(next);
            markup.addRow(row.toArray(new InlineKeyboardButton[0]));
        }

        return markup;
    }


    public static void start(TelegramBot bot, Update update){


        Long chatID = update.message().from().id();

//        bot.execute(new SetMyCommands(
//                new BotCommand("/start", "Botni boshlash"),
//                new BotCommand("/help", "Yordam olish"),
//                new BotCommand("/about", "Bot haqida")
//        ));

        Optional<User> admin = DB.users.stream().filter(user -> user.getLogin().equals("admin"))
                .findFirst();
        if (admin.isPresent()) {
            User user = admin.get();
            if (user.getChatId()==null) {
                userStates.put(chatID, UserState.LOGIN);
                bot.execute(new SendMessage(chatID,"Welcome to the bot! Please enter your login:"));
            } else if (user.getChatId().equals(chatID)) {
                userStates.put(chatID, UserState.ADMIN_MENU);
                SendMessage sendMessage = new SendMessage(chatID, "Welcome Admin! You can see product menu:");
                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                        new String[]{"Add Product", "Show Products"},
                        new String[]{"Show Users", "Show Orders"},
                        new String[]{"Settings"}
                );
                replyMarkup.resizeKeyboard(true);
                sendMessage.replyMarkup(replyMarkup);
                bot.execute(sendMessage);
            } else {
                userStates.put(chatID, UserState.USER_MENU);
                DB.users.stream().filter(user1->user.getChatId().equals(chatID))
                        .findFirst()
                        .ifPresentOrElse(user1 -> {
                            System.out.println("User already exists");
                            },
                            () -> {
                                DB.users.add(User.builder()
                                        .chatId(chatID)
                                        .build());
                            }
                        );
                SendMessage sendMessage = new SendMessage(chatID, "Welcome User!");
                ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup("Show Products","History");
                replyMarkup.resizeKeyboard(true);
                sendMessage.replyMarkup(replyMarkup);
                bot.execute(sendMessage);
            }
        }

    }

    public static void login(TelegramBot bot, Update update){
        Long chatID = update.message().from().id();
        DB.users.stream().filter(user -> user.getLogin().equals(update.message().text().trim()))
                .findFirst()
                .ifPresentOrElse(user -> {
                    userStates.put(chatID, UserState.PASSWORD);
                    bot.execute(new SendMessage(chatID,"Please enter your password:"));
                }, () -> {
                    bot.execute(new SendMessage(chatID,"User not found! Please enter your login:"));
                });
    }

    public static void password(TelegramBot bot, Update update){
        Long chatID = update.message().from().id();
        DB.users.stream().filter(user -> user.getPassword().equals(update.message().text().trim()))
                .findFirst()
                .ifPresentOrElse(user -> {
                    userStates.put(chatID, UserState.ADMIN_MENU);
                    user.setChatId(chatID);
                    SendMessage sendMessage = new SendMessage(chatID, "Welcome Admin! You can see menu:");
                    ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                            new String[]{"Add Product", "Show Products"},
                            new String[]{"Show Users", "Show Orders"},
                            new String[]{"Settings"}
                    );
                    replyMarkup.resizeKeyboard(true);
                    sendMessage.replyMarkup(replyMarkup);
                    bot.execute(sendMessage);
                }, () -> {
                    bot.execute(new SendMessage(chatID,"Password does not match. Please enter your password:"));
                });
    }
}
