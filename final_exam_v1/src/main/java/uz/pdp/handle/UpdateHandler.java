package uz.pdp.handle;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import uz.pdp.database.DB;
import uz.pdp.entity.Book;
import uz.pdp.entity.Order;
import uz.pdp.entity.UserState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateHandler {
    private static final ConcurrentHashMap<Long, UserState> userState = new ConcurrentHashMap<>();
    private static final TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));
    private static final Stack<Book> booksStack = new Stack<>();

    public static void handle(Update update) {
        if (update.message() != null) {
            Long chatId = update.message().chat().id();
            String text = update.message().text();
            UserState state = userState.getOrDefault(chatId, UserState.START);

            switch (state) {
                case START:
                    start(chatId, update);
                    break;
                case USER_MENU:
                    userMenu(chatId, update);
                    break;
                case ADMIN_MENU:
                    adminMenu(chatId, update);
                    break;
                case ADD_BOOK_NAME:
                    addBookName(chatId, text);
                    break;
                case ADD_BOOK_PRICE:
                    addBookPrice(chatId, text);
                    break;
                case ADD_BOOK_QUANTITY:
                    addBookQuantity(chatId, text);
                    break;
                case ADD_BOOK_PHOTO, ADD_BOOK_DOCUMENT:
                    addBookPhotoOrDocument(chatId, update);
                    break;
                case SHOW_BOOK_FOR_ADMIN, SHOWING_BOOK_FOR_ADMIN:
                    /*
                        bu funksiyaning vazifasi admin stati SHOW_BOOK_FOR_ADMIN
                        bulganda CallbackData qaytarish o`rniga boshqa so`rov junatsa xatolikni
                        oldini olish uchun kerak.
                    */
                    showBookForAdminUnknownCommand(chatId, text);
                    break;
                case SHOW_ORDER_FOR_ADMIN, SHOWING_ORDER_FOR_ADMIN:
                    /*
                        bu funksiyaning vazifasi admin stati SHOW_ORDER_FOR_ADMIN
                        bulganda CallbackData qaytarish o`rniga boshqa so`rov junatsa xatolikni
                        oldini olish uchun kerak.
                    */
                    showOrderForAdminUnknownCommand(chatId, text);
                    break;
                case SHOW_BOOK_FOR_USER, CHOOSE_BOOK_FOR_USER:
                    showBookForUserUnknownCommand(chatId, text);
                    break;
                case SHOW_ORDER_FOR_USER, SHOWING_ORDER_FOR_USER:
                    showOrderForUserUnknownCommand(chatId, text);
                    break;
            }
        }
        else if (update.callbackQuery() != null) {
            Long chatId = update.callbackQuery().from().id();

            UserState state = userState.get(chatId);
            switch (state){
                case SHOW_BOOK_FOR_ADMIN:
                    showBookForAdmin(chatId, update);
                    break;
                case SHOWING_BOOK_FOR_ADMIN:
                    showingBookForAdmin(chatId, update);
                    break;
                case SHOW_ORDER_FOR_ADMIN:
                    showOrderForAdmin(chatId, update);
                    break;
                case SHOWING_ORDER_FOR_ADMIN:
                    showingOrderForAdmin(chatId, update);
                    break;
                case SHOW_BOOK_FOR_USER:
                    showBookForUser(chatId, update);
                    break;
                case CHOOSE_BOOK_FOR_USER:
                    chooseBookForUser(chatId, update);
                    break;
                case SHOW_ORDER_FOR_USER:
                    showOrderForUser(chatId, update);
                    break;
                case SHOWING_ORDER_FOR_USER:
                    showingOrderForUser(chatId, update);
                    break;
                default:
                    bot.execute(new SendMessage(chatId, "Unknown command!"));
            }

        }
    }

    private static void showOrderForUserUnknownCommand(Long chatId, String text) {
        if (text.equals("\uD83D\uDD19 Back")){
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Back to user menu!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Unknown command! Please select a order.");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void showingOrderForUser(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("document_")) {
            String orderId = data.substring(9);
            Order order = DB.orders.stream()
                    .filter(b -> b.getId().toString().equals(orderId))
                    .findFirst()
                    .orElse(null);
            if (order != null) {
                SendDocument sendDocument = new SendDocument(chatId, order.getDocumentFileId());
                bot.execute(sendDocument);
                userState.put(chatId, UserState.USER_MENU);
                SendMessage sendMessage = new SendMessage(chatId, "Document sent successfully!");
                userMenuKeyboards(sendMessage);
                bot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(chatId, "Document not found!");
                sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
                bot.execute(sendMessage);
            }
        }

    }

    private static void showOrderForUser(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("b_")) {
            String orderId = data.substring(2);
            getOrderForUser(chatId, orderId);
        } else if (data.startsWith("prev_")) {
            String firstOrderId = data.substring(data.indexOf("_") + 1);
            previousOrder(chatId, update.callbackQuery(), firstOrderId);
        } else if (data.startsWith("next_")) {
            String lastOrderId = data.substring(data.indexOf("_") + 1);
            nextOrder(chatId, update.callbackQuery(), lastOrderId);
        } else if (data.equals("reject")) {
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void getOrderForUser(Long chatId, String orderId) {
        Order order = DB.orders.stream()
                .filter(b -> b.getId().toString().equals(orderId))
                .findFirst()
                .orElse(null);
        if (order != null) {
            userState.put(chatId, UserState.SHOWING_ORDER_FOR_USER);
            SendPhoto sendMessage = new SendPhoto(chatId, order.getPhotoFileId())
                    .caption("""
                        Book name: %s
                        Book price: %s
                        Book quantity: %s
                        """.formatted(order.getName(), order.getPrice(), order.getQuantity()));
            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton document = new InlineKeyboardButton("\uD83D\uDCD5 Get Document")
                    .callbackData("document_" + order.getId());
            row.add(document);
            replyMarkup.addRow(row.toArray(new InlineKeyboardButton[0]));
            sendMessage.replyMarkup(replyMarkup);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Order not found!");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void chooseBookForUser(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("i_")) {
            String orderId = data.substring(data.indexOf("_") + 1);
            DB.orders.stream().filter(order -> order.getId().toString().equals(orderId))
                    .findFirst()
                    .ifPresentOrElse(order -> {
                        if (!checkBookQuantityIsEnough(order.getBookId(), order.getQuantity()+1)) {
                            bot.execute(new AnswerCallbackQuery(update.callbackQuery().id())
                                                                .text("Book %s quantity is not enough".formatted(order.getQuantity()+1))
                                                                .showAlert(true));
                        }
                        else {
                            order.setQuantity(order.getQuantity() + 1);
                            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("➖")
                                            .callbackData("d_" + order.getId()),
                                    new InlineKeyboardButton(order.getQuantity().toString())
                                            .callbackData(order.getQuantity().toString()),
                                    new InlineKeyboardButton("➕")
                                            .callbackData("i_" + order.getId())
                            );
                            replyMarkup.addRow(
                                    new InlineKeyboardButton("Xarid qilish")
                                            .callbackData("add_" + order.getId())
                            );
                            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatId, update.callbackQuery().message().messageId())
                                    .replyMarkup(replyMarkup);
                            bot.execute(editMessageReplyMarkup);
                        }
                    }, () -> {
                        bot.execute(new SendMessage(chatId, "Book not found!"));
                    });
        }
        else if (data.startsWith("d_")) {
            String orderId = data.substring(data.indexOf("_") + 1);
            DB.orders.stream().filter(history -> history.getId().toString().equals(orderId))
                    .findFirst()
                    .ifPresentOrElse(order -> {
                        if (order.getQuantity() > 1) {
                            order.setQuantity(order.getQuantity() - 1);
                            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup(
                                    new InlineKeyboardButton("➖")
                                            .callbackData("d_" + order.getId()),
                                    new InlineKeyboardButton(order.getQuantity().toString())
                                            .callbackData(order.getQuantity().toString()),
                                    new InlineKeyboardButton("➕")
                                            .callbackData("i_" + order.getId())
                            );
                            replyMarkup.addRow(
                                    new InlineKeyboardButton("Xarid qilish")
                                            .callbackData("add_" + order.getId())
                            );
                            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup(chatId, update.callbackQuery().message().messageId())
                                    .replyMarkup(replyMarkup);
                            bot.execute(editMessageReplyMarkup);
                        }
                        else {
                            bot.execute(new AnswerCallbackQuery(update.callbackQuery().id())
                                    .text("Book quantity is 1")
                                    .showAlert(true));
                        }

                    }, () -> {
                        bot.execute(new SendMessage(chatId, "Book not found!"));
                    });
        }
        else if (data.startsWith("add_")) {
            String orderId = data.substring(data.indexOf("_") + 1);
            DB.orders.stream().filter(order -> order.getId().toString().equals(orderId))
                    .findFirst()
                    .ifPresentOrElse(order -> {
                        userState.put(chatId, UserState.USER_MENU);
                        if (checkBookQuantityIsEnough(order.getBookId(), order.getQuantity())) {
                            order.setIsSold(true);
                            bookQuantityCount(order.getBookId(), order.getQuantity());
                            bot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
                            SendMessage sendMessage = new SendMessage(chatId, "Book xarid qilindi!");
                            userMenuKeyboards(sendMessage);
                            bot.execute(sendMessage);
                        }
                        else {
                            bot.execute(new AnswerCallbackQuery(update.callbackQuery().id())
                                    .text("Book %s quantity is not enough".formatted(order.getQuantity()))
                                    .showAlert(true));
                        }
                    }, () -> {
                        bot.execute(new SendMessage(chatId, "Book not found!"));
                    });
        }
    }

    private static void bookQuantityCount(UUID bookId, Integer quantity) {
        DB.books.stream().filter(book -> book.getId().equals(bookId))
                .findFirst()
                .ifPresentOrElse(book -> {
                    book.setQuantity(book.getQuantity() - quantity);
                }, () -> {
                    bot.execute(new SendMessage(0L, "Book not found!"));
                });
    }

    private static boolean checkBookQuantityIsEnough(UUID bookId, int i) {
        Optional<Book> first = DB.books.stream().filter(book -> book.getId().equals(bookId))
                .findFirst();
        return first.filter(book -> book.getQuantity() >= i).isPresent();
    }

    private static void showBookForUserUnknownCommand(Long chatId, String text) {
        if (text.equals("\uD83D\uDD19 Back")){
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Back to user menu!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Unknown command! Please select a book.");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void showBookForUser(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("b_")) {
            String bookId = data.substring(2);
            getBookForUser(chatId, bookId);
        } else if (data.startsWith("prev_")) {
            String firstBookId = data.substring(data.indexOf("_") + 1);
            previousBook(chatId, update.callbackQuery(), firstBookId);
        } else if (data.startsWith("next_")) {
            String lastBookId = data.substring(data.indexOf("_") + 1);
            nextBook(chatId, update.callbackQuery(), lastBookId);
        } else if (data.equals("reject")) {
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void getBookForUser(Long chatId, String bookId) {
        DB.books.stream().filter(product -> product.getId().equals(UUID.fromString(bookId)))
                .findFirst()
                .ifPresentOrElse(book -> {
                    Order order = Order.builder()
                            .id(UUID.randomUUID())
                            .userChatId(chatId)
                            .bookId(book.getId())
                            .photoFileId(book.getPhotoFileId())
                            .documentFileId(book.getDocumentFileId())
                            .name(book.getName())
                            .price(book.getPrice())
                            .quantity(1)
                            .isSold(false)
                            .build();
                    DB.orders.add(order);

                    userState.put(chatId, UserState.CHOOSE_BOOK_FOR_USER);
                    InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
                    InlineKeyboardButton decrement = new InlineKeyboardButton("➖")
                            .callbackData("d_"+order.getId());
                    InlineKeyboardButton value = new InlineKeyboardButton("1")
                            .callbackData("1");
                    InlineKeyboardButton increment = new InlineKeyboardButton("➕")
                            .callbackData("i_"+order.getId());
                    replyMarkup.addRow(decrement,value,increment);
                    InlineKeyboardButton addBasket = new InlineKeyboardButton("Xarid qilish")
                            .callbackData("add_"+order.getId());
                    replyMarkup.addRow(addBasket);
                    SendPhoto sendPhoto = new SendPhoto(chatId, book.getPhotoFileId())
                            .caption("""
                                            Book name: %s
                                            Book price: %s
                                            Book quantity: %s
                                            """.formatted(book.getName(), book.getPrice(), book.getQuantity()))
                            .replyMarkup(replyMarkup);

                    bot.execute(sendPhoto);
                }, () -> {
                    bot.execute(new SendMessage(chatId, "Book not found!"));
                });
    }


    private static void userMenu(Long chatId, Update update) {
        String text = update.message().text();
        switch (text) {
            case "Show Books":
                showBooksForUser(chatId);
                break;
            case "Show History":
                showHistoryForUser(chatId);
                break;
            default:
                bot.execute(new SendMessage(chatId, "Unknown command!"));
        }
    }

    private static void showHistoryForUser(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> ordersIds = new ArrayList<>();
        DB.orders.stream()
                .filter(order -> order.getUserChatId().equals(chatId) && order.getIsSold())
                .limit(10)
                .forEach(order -> {
                    ordersIds.add(order.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(order.getName()));
                });
        if (ordersIds.isEmpty()) {
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No orders found!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            userState.put(chatId, UserState.SHOW_ORDER_FOR_USER);
            SendMessage sendMessage = new SendMessage(chatId, "History List:\n%s".formatted(stringBuilder.toString()));
            sendMessage.replyMarkup(buttons(ordersIds));
            bot.execute(sendMessage);
        }
    }

    private static void showBooksForUser(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> booksIds = new ArrayList<>();
        DB.books.stream()
                .limit(10)
                .forEach(product -> {
                    booksIds.add(product.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                });
        if (booksIds.isEmpty()) {
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No books found!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            userState.put(chatId, UserState.SHOW_BOOK_FOR_USER);
            SendMessage sendMessage = new SendMessage(chatId, "Book List:\n%s".formatted(stringBuilder.toString()));
            sendMessage.replyMarkup(buttons(booksIds));
            bot.execute(sendMessage);
        }
    }

    private static void showingOrderForAdmin(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("document_")) {
            String orderId = data.substring(9);
            Order order = DB.orders.stream()
                    .filter(b -> b.getId().toString().equals(orderId))
                    .findFirst()
                    .orElse(null);
            if (order != null) {
                SendDocument sendDocument = new SendDocument(chatId, order.getDocumentFileId());
                bot.execute(sendDocument);
                userState.put(chatId, UserState.ADMIN_MENU);
                SendMessage sendMessage = new SendMessage(chatId, "Document sent successfully!");
                adminMenuKeyboards(sendMessage);
                bot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(chatId, "Document not found!");
                sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
                bot.execute(sendMessage);
            }
        }
        else if (data.equals("reject")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            SendMessage sendMessage = new SendMessage(chatId, "Unknown command! Please select a order.");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void showOrderForAdmin(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("b_")) {
            String orderId = data.substring(2);
            getOrderForAdmin(chatId, orderId);
        } else if (data.startsWith("prev_")) {
            String firstOrderId = data.substring(data.indexOf("_") + 1);
            previousOrder(chatId, update.callbackQuery(), firstOrderId);
        } else if (data.startsWith("next_")) {
            String lastOrderId = data.substring(data.indexOf("_") + 1);
            nextOrder(chatId, update.callbackQuery(), lastOrderId);
        } else if (data.equals("reject")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void nextOrder(Long chatId, CallbackQuery callbackQuery, String lastOrderId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> ordersIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.orders.size(); coreCount++) {
            if (DB.orders.get(coreCount).getId().toString().equals(lastOrderId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.orders.stream()
                .skip(n.get()+1)
                .limit(10)
                .forEach(history -> {
                    ordersIds.add(history.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                });

        EditMessageText sendMessage = new EditMessageText(chatId,callbackQuery.message().messageId(), "Orders List:\n_____________________________________\n%s".formatted(stringBuilder));

        if (ordersIds.isEmpty()) {
            AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                    .text("No more orders available!")
                    .showAlert(true);
            bot.execute(noMoreProducts);
        } else {
            sendMessage.replyMarkup(buttons(ordersIds));
            bot.execute(sendMessage);
        }
    }

    private static void previousOrder(Long chatId, CallbackQuery callbackQuery, String firstOrderId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> ordersIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.orders.size(); coreCount++) {
            if (DB.orders.get(coreCount).getId().toString().equals(firstOrderId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.orders.stream()
                .skip(n.get()-10)
                .limit(10)
                .forEach(history -> {
                    ordersIds.add(history.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                });

        EditMessageText sendMessage = new EditMessageText(chatId,callbackQuery.message().messageId(), "Orders List:\n_____________________________________\n%s".formatted(stringBuilder));
        sendMessage.replyMarkup(buttons(ordersIds));
        bot.execute(sendMessage);
    }

    private static void getOrderForAdmin(Long chatId, String orderId) {
        Order order = DB.orders.stream()
                .filter(b -> b.getId().toString().equals(orderId))
                .findFirst()
                .orElse(null);
        if (order != null) {
            userState.put(chatId, UserState.SHOWING_ORDER_FOR_ADMIN);
            SendPhoto sendMessage = new SendPhoto(chatId, order.getPhotoFileId())
                    .caption("""
                        Book name: %s
                        Book price: %s
                        Book quantity: %s
                        """.formatted(order.getName(), order.getPrice(), order.getQuantity()));
            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton document = new InlineKeyboardButton("\uD83D\uDCD5 Get Document")
                    .callbackData("document_" + order.getId());
            row.add(document);
            replyMarkup.addRow(row.toArray(new InlineKeyboardButton[0]));
            sendMessage.replyMarkup(replyMarkup);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Order not found!");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void showOrderForAdminUnknownCommand(Long chatId, String text) {
        if (text.equals("\uD83D\uDD19 Back")){
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Back to admin menu!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Unknown command! Please select a order.");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void showingBookForAdmin(Long chatId, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("delete_")) {
            String bookId = data.substring(7);
            DB.books.removeIf(b -> b.getId().toString().equals(bookId));
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Book deleted successfully!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else if (data.startsWith("document_")) {
            String bookId = data.substring(9);
            Book book = DB.books.stream()
                    .filter(b -> b.getId().toString().equals(bookId))
                    .findFirst()
                    .orElse(null);
            if (book != null) {
                SendDocument sendDocument = new SendDocument(chatId, book.getDocumentFileId());
                bot.execute(sendDocument);
                userState.put(chatId, UserState.ADMIN_MENU);
                SendMessage sendMessage = new SendMessage(chatId, "Document sent successfully!");
                adminMenuKeyboards(sendMessage);
                bot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(chatId, "Document not found!");
                sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
                bot.execute(sendMessage);
            }
        }
    }

    private static void showBookForAdmin(Long chatId, Update update) {
        String data = update.callbackQuery().data();

        System.out.println("data- "+data);

        if (data.startsWith("b_")) {
            String bookId = data.substring(2);
            getBookForAdmin(chatId, bookId);
        } else if (data.startsWith("prev_")) {
            String firstBookId = data.substring(data.indexOf("_") + 1);
            previousBook(chatId, update.callbackQuery(), firstBookId);
        } else if (data.startsWith("next_")) {
            String lastBookId = data.substring(data.indexOf("_") + 1);
            nextBook(chatId, update.callbackQuery(), lastBookId);
        } else if (data.equals("reject")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            bot.execute(new DeleteMessage(chatId, update.callbackQuery().message().messageId()));
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void nextBook(Long chatId, CallbackQuery callbackQuery, String lastBookId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> booksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.books.stream().filter(book -> book.getQuantity()>0).count(); coreCount++) {
            if (DB.books.get(coreCount).getId().toString().equals(lastBookId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.books.stream()
                .filter(book -> book.getQuantity()>0)
                .skip(n.get()+1)
                .limit(10)
                .forEach(history -> {
                    booksIds.add(history.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                });

        EditMessageText sendMessage = new EditMessageText(chatId,callbackQuery.message().messageId(), "Book List:\n_____________________________________\n%s".formatted(stringBuilder));

        if (booksIds.isEmpty()) {
            AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                    .text("No more books available!")
                    .showAlert(true);
            bot.execute(noMoreProducts);
        } else {
            sendMessage.replyMarkup(buttons(booksIds));
            bot.execute(sendMessage);
        }
    }

    private static void previousBook(Long chatId, CallbackQuery callbackQuery, String firstBookId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> booksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.books.size(); coreCount++) {
            if (DB.books.get(coreCount).getId().toString().equals(firstBookId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.books.stream()
                .skip(n.get()-10)
                .limit(10)
                .forEach(history -> {
                    booksIds.add(history.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(history.getName()));
                });

        EditMessageText sendMessage = new EditMessageText(chatId,callbackQuery.message().messageId(), "Book List:\n_____________________________________\n%s".formatted(stringBuilder));
        sendMessage.replyMarkup(buttons(booksIds));
        bot.execute(sendMessage);
    }

    private static void getBookForAdmin(Long chatId, String bookId) {
        Book book = DB.books.stream()
                .filter(b -> b.getId().toString().equals(bookId))
                .findFirst()
                .orElse(null);
        if (book != null) {
            userState.put(chatId, UserState.SHOWING_BOOK_FOR_ADMIN);
            SendPhoto sendMessage = new SendPhoto(chatId, book.getPhotoFileId())
                    .caption("""
                        Book name: %s
                        Book price: %s
                        Book quantity: %s
                        """.formatted(book.getName(), book.getPrice(), book.getQuantity()));
            InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
            List<InlineKeyboardButton> row = new ArrayList<>();
            InlineKeyboardButton delete = new InlineKeyboardButton("\uD83D\uDDD1 Delete")
                    .callbackData("delete_" + book.getId());
            InlineKeyboardButton document = new InlineKeyboardButton("\uD83D\uDCD5 Get Document")
                    .callbackData("document_" + book.getId());
            row.add(document);
            row.add(delete);
            replyMarkup.addRow(row.toArray(new InlineKeyboardButton[0]));
            sendMessage.replyMarkup(replyMarkup);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Book not found!");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void showBookForAdminUnknownCommand(Long chatId, String text) {
        if (text.equals("\uD83D\uDD19 Back")){
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Back to admin menu!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Unknown command! Please select a book.");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("\uD83D\uDD19 Back").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void addBookPhotoOrDocument(Long chatId, Update update) {
        String text = update.message().text();
        if (text != null) {
            if (text.equals("❌ Cancel")) {
                userState.put(chatId, UserState.ADMIN_MENU);
                booksStack.pop();
                SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
                adminMenuKeyboards(sendMessage);
                bot.execute(sendMessage);
            } else {
                bot.execute(new SendMessage(chatId, "Please send a valid photo or document."));
            }
        } else if (update.message().photo() != null) {
            userState.put(chatId, UserState.ADD_BOOK_DOCUMENT);
            PhotoSize[] photo = update.message().photo();
            PhotoSize photoSize = photo[photo.length - 1];
            String fileId = photoSize.fileId();
            booksStack.peek().setPhotoFileId(fileId);
            SendMessage sendMessage = new SendMessage(chatId, """
                Please click 📎 button then send book document.
                Example: .pdf, .epub
                """);
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("❌ Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        } else if (update.message().document() != null) {
            booksStack.peek().setDocumentFileId(update.message().document().fileId());
            Book book = booksStack.pop();
            DB.books.add(book);
            booksStack.clear();
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Book added successfully!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            bot.execute(new SendMessage(chatId, "Please send a valid photo or document."));
        }
    }

    private static void addBookQuantity(Long chatId, String text) {
        if (text.equals("❌ Cancel")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            booksStack.pop();
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            booksStack.peek().setQuantity(Integer.parseInt(text));
            userState.put(chatId, UserState.ADD_BOOK_PHOTO);
            SendMessage sendMessage = new SendMessage(chatId, """
                Please click 📎 button then send book photo.
                Example: .jpeg, .png
                """);
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("❌ Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void addBookPrice(Long chatId, String text) {
        if (text.equals("❌ Cancel")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            booksStack.pop();
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            booksStack.peek().setPrice(text);
            userState.put(chatId, UserState.ADD_BOOK_QUANTITY);
            SendMessage sendMessage = new SendMessage(chatId, """
                Please send book quantity.
                Example: 10
                """);
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("❌ Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void addBookName(Long chatId, String text) {
        if (text.equals("❌ Cancel")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            userState.put(chatId, UserState.ADD_BOOK_PRICE);
            booksStack.push(
                    Book.builder()
                    .id(UUID.randomUUID())
                    .name(text)
                    .build()
            );
            SendMessage sendMessage = new SendMessage(chatId, """
                Please send book price.
                Example: 100$
                """);
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("❌ Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void adminMenu(Long chatId, Update update) {
        String text = update.message().text();
        switch (text) {
            case "Add Book":
                addBook(chatId);
                break;
            case "Show Books":
                showBooks(chatId);
                break;
            case "Show Orders":
                showOrders(chatId);
                break;
            default:
                bot.execute(new SendMessage(chatId, "Unknown command!"));
        }
    }

    private static void showOrders(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> ordersIds = new ArrayList<>();
        DB.orders.stream()
                .limit(10)
                .forEach(product -> {
                    ordersIds.add(product.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                });
        if (ordersIds.isEmpty()) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No orders found!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            userState.put(chatId, UserState.SHOW_ORDER_FOR_ADMIN);
            SendMessage sendMessage = new SendMessage(chatId, "Orders List:\n%s".formatted(stringBuilder.toString()));
            sendMessage.replyMarkup(buttons(ordersIds));
            bot.execute(sendMessage);
        }
    }

    private static void showBooks(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> booksIds = new ArrayList<>();
        DB.books.stream()
                .limit(10)
                .forEach(product -> {
                    booksIds.add(product.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(product.getName()));
                });
        if (booksIds.isEmpty()) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No books found!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
       else {
           userState.put(chatId, UserState.SHOW_BOOK_FOR_ADMIN);
           SendMessage sendMessage = new SendMessage(chatId, "Book List:\n%s".formatted(stringBuilder.toString()));
           sendMessage.replyMarkup(buttons(booksIds));
           bot.execute(sendMessage);
        }
    }

    private static InlineKeyboardMarkup buttons(List<UUID> booksIds) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        StringBuilder lastBookId = new StringBuilder();
        StringBuilder firstBookId = new StringBuilder();
        for (int i = 1; i <= booksIds.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i))
                    .callbackData("b_"+booksIds.get(i-1));
            row.add(button);
            if (i % 5 == 0) {
                markup.addRow(row.toArray(new InlineKeyboardButton[0]));
                row.clear();
            }
            if (i==booksIds.size()){
                UUID uuid = booksIds.get(i - 1);
                lastBookId.append(uuid);
            }
            if (i==1){
                UUID uuid = booksIds.get(0);
                firstBookId.append(uuid);
            }
        }
        if (!row.isEmpty()) {
            markup.addRow(row.toArray(new InlineKeyboardButton[0]));
        }
        if (!row.isEmpty()) {
            row.clear();
            InlineKeyboardButton prev = new InlineKeyboardButton("⏮\uFE0F")
                    .callbackData("prev_"+ firstBookId);
            InlineKeyboardButton reject = new InlineKeyboardButton("❌")
                    .callbackData("reject");
            InlineKeyboardButton next = new InlineKeyboardButton("⏭\uFE0F")
                    .callbackData("next_"+ lastBookId);
            row.add(prev);
            row.add(reject);
            row.add(next);
            markup.addRow(row.toArray(new InlineKeyboardButton[0]));
        }

        return markup;
    }

    private static void addBook(Long chatId) {
        userState.put(chatId, UserState.ADD_BOOK_NAME);
        SendMessage sendMessage = new SendMessage(chatId, """
            Please send book name.
            Example: "O`tkan kunlar"
            """);
        sendMessage.replyMarkup(new ReplyKeyboardMarkup("❌ Cancel").resizeKeyboard(true));
        bot.execute(sendMessage);
    }

    private static void start(Long chatID, Update update) {
        if (adminChecker(chatID)) {
            userState.put(chatID, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatID, "Welcome Admin!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            userState.put(chatID, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatID, "Welcome User!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void userMenuKeyboards(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup("Show Books","Show History");
        replyMarkup.resizeKeyboard(true);
        sendMessage.replyMarkup(replyMarkup);
    }

    private static void adminMenuKeyboards(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                new String[]{"Add Book", "Show Books"},
                new String[]{"Show Orders"}
        );
        replyMarkup.resizeKeyboard(true);
        sendMessage.replyMarkup(replyMarkup);
    }

    private static boolean adminChecker(Long chatID) {
        return chatID.equals(Long.valueOf(ResourceBundle.getBundle("settings").getString("bot.adminID")));
    }
}
