package uz.pdp.handle;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.*;
import uz.pdp.database.DB;
import uz.pdp.entity.Homework;
import uz.pdp.entity.User;
import uz.pdp.entity.UserState;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdateHandler {
    private static final ConcurrentHashMap<Long, UserState> userState = new ConcurrentHashMap<>();
    private static final TelegramBot bot = new TelegramBot(ResourceBundle.getBundle("settings").getString("bot.token"));
    private static final Stack<Homework> userHomeworkStack = new Stack<>();
    private static final Stack<Homework> adminHomeworkStack = new Stack<>();

    public static void handle(Update update) {
        if (update.message() != null) {
            Long chatId = update.message().chat().id();
            String text = update.message().text();
            UserState state = userState.getOrDefault(chatId, UserState.START);

            switch (state) {
                case START:
                    start(chatId,update);
                    break;
                case USER_MENU:
                    userMenu(chatId,text);
                    break;
                case SEND_HOMEWORK_DESCRIPTION:
                    getHomeworkDescription(chatId,text);
                    break;
                case SEND_HOMEWORK_ZIP_FILE:
                    sendHomeworkZipFile(chatId,update);
                    break;
                case ADMIN_MENU:
                    adminMenu(chatId,text);
                    break;
                case GIVE_BALL:
                    giveBall(chatId,text);
                    break;
                case GIVE_ADMIN_FEEDBACK:
                    giveAdminFeedback(chatId,text);
                    break;
                case CHECK_HOMEWORK:
                    chooseHomeworkUnknownCommands(chatId,text);
                    break;
                case SHOW_OLD_HOMEWORK:
                    chooseOldHomeworkUnknownCommands(chatId,text);
                    break;
                case SHOW_OLD_HOMEWORK_FOR_USER:
                    chooseOldHomeworkForUserUnknownCommands(chatId,text);
                    break;
            }

        } else if (update.callbackQuery() != null) {
            Long chatID = update.callbackQuery().from().id();
            switch (userState.get(chatID)){
                case CHECK_HOMEWORK:
                    chooseHomework(chatID, update);
                    break;
                case SHOW_OLD_HOMEWORK:
                    showOldHomeworkForAdmin(chatID, update);
                    break;
                case SHOW_OLD_HOMEWORK_FOR_USER:
                    showOldHomeworkForUser(chatID, update);
                    break;
            }
        }

    }

    private static void chooseOldHomeworkForUserUnknownCommands(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, "Unknown command. Please choose old homework to show again.");
        bot.execute(sendMessage);
    }

    private static void showOldHomeworkForUser(Long chatID, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("h_")) {
            getOldHomeworkForUserToShow(chatID, update);
        } else if (data.startsWith("prev_")) {
            previousOldHomeworkForUser(chatID, update.callbackQuery());
        } else if (data.startsWith("next_")) {
            nextOldHomeworkForUser(chatID, update.callbackQuery());
        } else if (data.equals("reject")) {
            userState.put(chatID, UserState.USER_MENU);
            bot.execute(new DeleteMessage(chatID, update.callbackQuery().message().messageId()));
            SendMessage sendMessage = new SendMessage(chatID, "Homework rejected");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void nextOldHomeworkForUser(Long chatID, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        String lastHomeworkId = data.substring(data.indexOf("_") + 1);
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.homeworks.stream().filter(homework -> homework.getUserChatId().equals(chatID) && homework.getCheckTime()!=null).count(); coreCount++) {
            if (DB.homeworks.get(coreCount).getId().toString().equals(lastHomeworkId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.homeworks.stream()
                .filter(homework -> homework.getUserChatId().equals(chatID) && homework.getCheckTime()!=null)
                .skip(n.get()+1)
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });

        if (homeworksIds.isEmpty()){
            AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                    .text("No more homeworks available!")
                    .showAlert(true);
            bot.execute(noMoreProducts);
        }
        else {
            EditMessageText sendMessage = new EditMessageText(chatID, callbackQuery.message().messageId(), "Checked Homework List:\n_____________________________________\n%s".formatted(stringBuilder));
            sendMessage.replyMarkup(buttons(homeworksIds));
            bot.execute(sendMessage);
        }
    }

    private static void previousOldHomeworkForUser(Long chatID, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        String firstHomeworkId = data.substring(data.indexOf("_") + 1);
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.homeworks.stream().filter(homework -> homework.getUserChatId().equals(chatID) && homework.getCheckTime()!=null).count(); coreCount++) {
            if (DB.homeworks.get(coreCount).getId().toString().equals(firstHomeworkId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.homeworks.stream()
                .filter(homework -> homework.getUserChatId().equals(chatID) && homework.getCheckTime()!=null)
                .skip(n.get()-10)
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });

        EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "Checked Homework List:\n_____________________________________\n%s".formatted(stringBuilder));
        sendMessage.replyMarkup(buttons(homeworksIds));
        bot.execute(sendMessage);
    }

    private static void getOldHomeworkForUserToShow(Long chatID, Update update) {
        String data = update.callbackQuery().data();
        UUID homeworkId = UUID.fromString(data.substring(2));
        Homework homework = DB.homeworks.stream()
                .filter(h -> h.getId().equals(homeworkId))
                .findFirst()
                .orElse(null);
        if (homework != null) {
            DB.users.stream().filter(user -> user.getChatID().equals(homework.getUserChatId()))
                    .findFirst()
                    .ifPresent(user -> {
                        userState.put(chatID, UserState.USER_MENU);
                        bot.execute(new DeleteMessage(chatID, update.callbackQuery().message().messageId()));
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

                        String sendTimeFormatted = homework.getSendTime().format(formatter);
                        String checkTimeFormatted = homework.getCheckTime() != null
                                ? homework.getCheckTime().format(formatter)
                                : "Not checked yet";

                        SendDocument sendDocument = new SendDocument(chatID, homework.getZipFileId());
                        sendDocument.caption("""
                                Homework: %s
                                Student: %s %s
                                Student username: %s
                                Ball: %s
                                Teacher feedback: %s
                                Sent at: %s
                                Check at: %s
                                """.formatted(
                                homework.getDescription(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getUsername(),
                                homework.getBall(),
                                homework.getTeacherDescription(),
                                sendTimeFormatted,
                                checkTimeFormatted
                        ));

                        sendDocument.replyMarkup(new ReplyKeyboardMarkup("Sent Homework","Show Old Homeworks").resizeKeyboard(true));
                        bot.execute(sendDocument);
                    });

        }
        else {
            SendMessage sendMessage = new SendMessage(chatID, "Homework not found!");
            bot.execute(sendMessage);
        }
    }

    private static void sendHomeworkZipFile(Long chatId, Update update) {
        String text = update.message().text();
        System.out.println(text);
        if (text!=null){
            if (update.message().text().equals("Cancel")) {
                userState.put(chatId, UserState.USER_MENU);
                userHomeworkStack.pop();
                SendMessage sendMessage = new SendMessage(chatId, "Cancelled");
                userMenuKeyboards(sendMessage);
                bot.execute(sendMessage);
            } else {
                SendMessage sendMessage = new SendMessage(chatId, "Please send your homework as a .zip file");
                sendMessage.replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
                bot.execute(sendMessage);
            }
        }
        else if (update.message().document() != null) {
            System.out.println("Homework zip file received");
            if (!update.message().document().fileName().endsWith(".zip")) {
                SendMessage sendMessage = new SendMessage(chatId, "Please send your homework as a .zip file");
                sendMessage.replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
                bot.execute(sendMessage);
                return;
            }
            Homework homework = userHomeworkStack.pop();
            homework.setZipFileId(update.message().document().fileId());
            homework.setSendTime(LocalDateTime.now());
            DB.homeworks.add(homework);
            userState.put(chatId, UserState.USER_MENU);

            Long adminChatId = getAdminChatId();
            userState.put(adminChatId, UserState.ADMIN_MENU);
            SendMessage sendMessage1 = new SendMessage(adminChatId, """
                    New homework sent from student: %s %s
                    Please click Check Homework button to check it!
                    """.formatted(update.message().chat().firstName(), update.message().chat().lastName()));
            adminMenuKeyboards(sendMessage1);
            bot.execute(sendMessage1);

            SendMessage sendMessage = new SendMessage(chatId, "Homework sent successfully to Teacher");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static Long getAdminChatId() {
        return Long.valueOf(ResourceBundle.getBundle("settings").getString("bot.adminID"));
    }

    private static void getHomeworkDescription(Long chatId, String text) {
        if (text.equals("Cancel")) {
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            Homework homework = new Homework();
            homework.setDescription(text);
            homework.setUserChatId(chatId);
            homework.setId(UUID.randomUUID());
            userHomeworkStack.push(homework);
            userState.put(chatId, UserState.SEND_HOMEWORK_ZIP_FILE);
            SendMessage sendMessage = new SendMessage(chatId, "Please click 📎 button then send your homework as a .zip file");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        }
    }

    private static void userMenu(Long chatId, String text) {
        switch (text) {
            case "Send Homework":
                sendHomeworkDescription(chatId);
                break;
            case "Show Old Homeworks":
                showOldHomeworksForUser(chatId);
                break;
            default:
                SendMessage sendMessage1 = new SendMessage(chatId, "Invalid command");
                bot.execute(sendMessage1);
                break;
        }
    }

    private static void showOldHomeworksForUser(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();
        DB.homeworks.stream()
                .filter(homework -> homework.getUserChatId().equals(chatId))
//                .filter(homework -> homework.getCheckTime() != null)
                .sorted(Comparator.comparing(Homework::getSendTime).reversed())
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });
        if (homeworksIds.isEmpty()) {
            userState.put(chatId, UserState.USER_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No old homeworks found!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            userState.put(chatId, UserState.SHOW_OLD_HOMEWORK_FOR_USER);
            SendMessage sendMessage = new SendMessage(chatId, "Old Homework List:\n%s".formatted(stringBuilder.toString()));
            sendMessage.replyMarkup(buttons(homeworksIds));
            bot.execute(sendMessage);
        }
    }

    private static void sendHomeworkDescription(Long chatId) {
        userState.put(chatId, UserState.SEND_HOMEWORK_DESCRIPTION);
        SendMessage sendMessage = new SendMessage(chatId, "Please send homework theme or description");
        sendMessage.replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
        bot.execute(sendMessage);
    }

    private static void chooseOldHomeworkUnknownCommands(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, "Unknown command. Please choose old homework to show again.");
        bot.execute(sendMessage);
    }

    private static void showOldHomeworkForAdmin(Long chatID, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("h_")) {
            getOldHomeworkForAdminToShow(chatID, data);
        } else if (data.startsWith("prev_")) {
            previousOldHomework(chatID, update.callbackQuery());
        } else if (data.startsWith("next_")) {
            nextOldHomework(chatID, update.callbackQuery());
        } else if (data.equals("reject")) {
            userState.put(chatID, UserState.ADMIN_MENU);
            bot.execute(new DeleteMessage(chatID, update.callbackQuery().message().messageId()));
            SendMessage sendMessage = new SendMessage(chatID, "Homework rejected");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void nextOldHomework(Long chatID, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        String lastHomeworkId = data.substring(data.indexOf("_") + 1);
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.homeworks.stream().filter(homework -> homework.getCheckTime() != null).count(); coreCount++) {
            if (DB.homeworks.get(coreCount).getId().toString().equals(lastHomeworkId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.homeworks.stream()
                .filter(homework -> homework.getCheckTime() != null)
                .skip(n.get()+1)
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });

        if (homeworksIds.isEmpty()){
            AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                    .text("No more homeworks available!")
                    .showAlert(true);
            bot.execute(noMoreProducts);
        }
        else {
            EditMessageText sendMessage = new EditMessageText(chatID, callbackQuery.message().messageId(), "Checked Homework List:\n_____________________________________\n%s".formatted(stringBuilder));
            sendMessage.replyMarkup(buttons(homeworksIds));
            bot.execute(sendMessage);
        }
    }

    private static void previousOldHomework(Long chatID, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        String firstHomeworkId = data.substring(data.indexOf("_") + 1);
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.homeworks.stream().filter(homework -> homework.getCheckTime() != null).count(); coreCount++) {
            if (DB.homeworks.get(coreCount).getId().toString().equals(firstHomeworkId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.homeworks.stream()
                .filter(homework -> homework.getCheckTime() != null)
                .skip(n.get()-10)
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });

        EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "Checked Homework List:\n_____________________________________\n%s".formatted(stringBuilder));
        sendMessage.replyMarkup(buttons(homeworksIds));
        bot.execute(sendMessage);
    }

    private static void getOldHomeworkForAdminToShow(Long chatID, String data) {
        UUID homeworkId = UUID.fromString(data.substring(2));
        Homework homework = DB.homeworks.stream()
                .filter(h -> h.getId().equals(homeworkId))
                .findFirst()
                .orElse(null);
        if (homework != null) {
            DB.users.stream().filter(user -> user.getChatID().equals(homework.getUserChatId()))
                    .findFirst()
                    .ifPresent(user -> {
                        userState.put(chatID, UserState.ADMIN_MENU);
                        SendDocument sendDocument = new SendDocument(chatID, homework.getZipFileId());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        String sendTimeFormatted = homework.getSendTime().format(formatter);
                        String checkTimeFormatted = homework.getCheckTime() != null
                                ? homework.getCheckTime().format(formatter)
                                : "Not checked yet";
                        sendDocument.caption("""
                            Homework: %s
                            Student: %s %s
                            Student username: %s
                            BALL: %s
                            Teacher feedback: %s
                            Sent at: %s
                            Check at: %s
                            """.formatted(
                                homework.getDescription(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getUsername(),
                                homework.getBall(),
                                homework.getTeacherDescription(),
                                sendTimeFormatted,
                                checkTimeFormatted
                        ));
                        sendDocument.replyMarkup(new ReplyKeyboardMarkup("Check Homework", "Show Old Homeworks").resizeKeyboard(true));
                        bot.execute(sendDocument);
                    });

        }
        else {
            SendMessage sendMessage = new SendMessage(chatID, "Homework not found!");
            bot.execute(sendMessage);
        }
    }

    private static void chooseHomeworkUnknownCommands(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage(chatId, "Unknown command. Please choose homework to check.");
        bot.execute(sendMessage);
    }

    private static void giveAdminFeedback(Long chatId, String text) {
        if (text.equals("Cancel")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            adminHomeworkStack.pop();
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else if (text.length() > 5) {
            Homework homeworkStack = adminHomeworkStack.pop();
            DB.homeworks.stream().filter(homework -> homework.getId().equals(homeworkStack.getId()))
                    .findFirst()
                    .ifPresent(homework -> {
                        homework.setTeacherDescription(text);
                        homework.setCheckTime(LocalDateTime.now());
                        userState.put(chatId, UserState.ADMIN_MENU);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        SendMessage sendMessageForStudent = new SendMessage(homework.getUserChatId(),
                                """
                                Your homework has been checked!
                                -------------------------------
                                Homework: %s
                                Ball: %s
                                Teacher Feedback: %s
                                Checked at: %s
                                """.formatted(
                                        homework.getDescription(),
                                        homework.getBall(),
                                        text,
                                        homework.getCheckTime().format(formatter)
                                        ));
                        bot.execute(sendMessageForStudent);

                        SendMessage sendMessage = new SendMessage(chatId, "Homework checked and Feedback sent to student");
                        adminMenuKeyboards(sendMessage);
                        bot.execute(sendMessage);
                    });
        }
        else {
            SendMessage sendMessage = new SendMessage(chatId, "Feedback must be more than 5 characters");
            bot.execute(sendMessage);
        }
    }

    private static void giveBall(Long chatId, String text) {
        if (text.matches("[1-5]")) {
            userState.put(chatId, UserState.GIVE_ADMIN_FEEDBACK);
            Homework homework = adminHomeworkStack.peek();
            homework.setBall(Integer.parseInt(text));
            SendMessage sendMessage = new SendMessage(chatId, "Please write a feedback for the student");
            sendMessage.replyMarkup(new ReplyKeyboardMarkup("Cancel").resizeKeyboard(true));
            bot.execute(sendMessage);
        } else if (text.equals("Cancel")) {
            userState.put(chatId, UserState.ADMIN_MENU);
            adminHomeworkStack.pop();
            SendMessage sendMessage = new SendMessage(chatId, "Cancelled");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            SendMessage sendMessage = new SendMessage(chatId, "Invalid ball.");
            ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                    new String[]{"1", "2", "3", "4", "5"},
                    new String[]{"Cancel"}
            ).resizeKeyboard(true);
            sendMessage.replyMarkup(replyMarkup);
            bot.execute(sendMessage);
        }
    }

    private static void chooseHomework(Long chatID, Update update) {
        String data = update.callbackQuery().data();
        if (data.startsWith("h_")) {
            getHomeworkForAdminToCheck(chatID, data);
        } else if (data.startsWith("prev_")) {
            previousHomework(chatID, update.callbackQuery());
        } else if (data.startsWith("next_")) {
            nextHomework(chatID, update.callbackQuery());
        } else if (data.equals("reject")) {
            userState.put(chatID, UserState.ADMIN_MENU);
            bot.execute(new DeleteMessage(chatID, update.callbackQuery().message().messageId()));
            SendMessage sendMessage = new SendMessage(chatID, "Homework rejected");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void nextHomework(Long chatID, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        String lastHomeworkId = data.substring(data.indexOf("_") + 1);
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.homeworks.stream().filter(homework -> homework.getCheckTime() == null).count(); coreCount++) {
            if (DB.homeworks.get(coreCount).getId().toString().equals(lastHomeworkId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.homeworks.stream()
                .filter(homework -> homework.getCheckTime() == null)
                .skip(n.get()+1)
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });

        if (homeworksIds.isEmpty()){
            AnswerCallbackQuery noMoreProducts = new AnswerCallbackQuery(callbackQuery.id())
                    .text("No more homeworks available!")
                    .showAlert(true);
            bot.execute(noMoreProducts);
        }
        else {
            EditMessageText sendMessage = new EditMessageText(chatID, callbackQuery.message().messageId(), "Unchecked Homework List:\n_____________________________________\n%s".formatted(stringBuilder));
            sendMessage.replyMarkup(buttons(homeworksIds));
            bot.execute(sendMessage);
        }
    }

    private static void previousHomework(Long chatID, CallbackQuery callbackQuery) {
        String data = callbackQuery.data();
        String firstHomeworkId = data.substring(data.indexOf("_") + 1);
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();

        AtomicInteger n = new AtomicInteger(0);
        for (int coreCount = 0; coreCount < DB.homeworks.stream().filter(homework -> homework.getCheckTime() == null).count(); coreCount++) {
            if (DB.homeworks.get(coreCount).getId().toString().equals(firstHomeworkId)) {
                n.set(coreCount);
                break;
            }
        }
        DB.homeworks.stream()
                .filter(homework -> homework.getCheckTime() == null)
                .skip(n.get()-10)
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });

        EditMessageText sendMessage = new EditMessageText(chatID,callbackQuery.message().messageId(), "Unchecked Homework List:\n_____________________________________\n%s".formatted(stringBuilder));
        sendMessage.replyMarkup(buttons(homeworksIds));
        bot.execute(sendMessage);
    }

    private static void getHomeworkForAdminToCheck(Long chatID, String data) {
        UUID homeworkId = UUID.fromString(data.substring(2));
        Homework homework = DB.homeworks.stream()
                .filter(h -> h.getId().equals(homeworkId))
                .findFirst()
                .orElse(null);
        if (homework != null) {
            DB.users.stream().filter(user -> user.getChatID().equals(homework.getUserChatId()))
                    .findFirst()
                    .ifPresent(user -> {
                        System.out.println("Homework found------------");
                        adminHomeworkStack.push(homework);
                        userState.put(chatID, UserState.GIVE_BALL);
                        SendDocument sendDocument = new SendDocument(chatID, homework.getZipFileId());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                        sendDocument.caption("""
                            Homework: %s
                            Student: %s %s
                            Student username: %s
                            Sent at: %s
                            -------------
                            Open .zip file to check homework
                            Give ball (1,2,3,4,5)
                            """.formatted(
                                    homework.getDescription(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getUsername(),
                                homework.getSendTime().format(formatter)
                        ));
                        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup(
                                new String[]{"1", "2", "3", "4", "5"},
                                new String[]{"Cancel"}
                        ).resizeKeyboard(true);
                        sendDocument.replyMarkup(replyMarkup);
                        bot.execute(sendDocument);
                    });

        }
        else {
            SendMessage sendMessage = new SendMessage(chatID, "Homework not found!");
            bot.execute(sendMessage);
        }
    }

    private static void adminMenu(Long chatId,String text) {
        switch (text) {
            case "Check Homework":
                checkHomework(chatId);
                break;
            case "Show Old Homeworks":
                showOldHomeworks(chatId);
                break;
            default:
                SendMessage sendMessage = new SendMessage(chatId, "Invalid command");
                bot.execute(sendMessage);
                break;
        }
    }

    private static void showOldHomeworks(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();
        DB.homeworks.stream()
                .filter(homework -> homework.getCheckTime() != null)
                .sorted(Comparator.comparing(Homework::getSendTime).reversed())
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });
        if (homeworksIds.isEmpty()) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No old homeworks found!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            userState.put(chatId, UserState.SHOW_OLD_HOMEWORK);
            SendMessage sendMessage = new SendMessage(chatId, "Old Homework List:\n%s".formatted(stringBuilder.toString()));
            sendMessage.replyMarkup(buttons(homeworksIds));
            bot.execute(sendMessage);
        }
    }

    private static void checkHomework(Long chatId) {
        StringBuilder stringBuilder = new StringBuilder();
        AtomicInteger count= new AtomicInteger();
        List<UUID> homeworksIds = new ArrayList<>();
        DB.homeworks.stream()
                .filter(homework -> homework.getCheckTime() == null)
                .sorted(Comparator.comparing(Homework::getSendTime).reversed())
                .limit(10)
                .forEach(homework -> {
                    homeworksIds.add(homework.getId());
                    stringBuilder.append((count.incrementAndGet())+".%s\n".formatted(homework.getDescription()));
                });
        if (homeworksIds.isEmpty()) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "No homeworks found!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
        else {
            userState.put(chatId, UserState.CHECK_HOMEWORK);
            SendMessage sendMessage = new SendMessage(chatId, "Unchecked Homework List:\n%s".formatted(stringBuilder.toString()));
            sendMessage.replyMarkup(buttons(homeworksIds));
            bot.execute(sendMessage);
        }
    }

    private static InlineKeyboardMarkup buttons(List<UUID> homeworksIds) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        StringBuilder lastHomeworkId = new StringBuilder();
        StringBuilder firstHomeworkId = new StringBuilder();
        for (int i = 1; i <= homeworksIds.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton(String.valueOf(i))
                    .callbackData("h_"+homeworksIds.get(i-1));
            row.add(button);
            if (i % 5 == 0) {
                markup.addRow(row.toArray(new InlineKeyboardButton[0]));
                row.clear();
            }
            if (i==homeworksIds.size()){
                UUID uuid = homeworksIds.get(i - 1);
                lastHomeworkId.append(uuid);
            }
            if (i==1){
                UUID uuid = homeworksIds.get(0);
                firstHomeworkId.append(uuid);
            }
        }
        if (!row.isEmpty()) {
            markup.addRow(row.toArray(new InlineKeyboardButton[0]));
        }
        if (!row.isEmpty()) {
            row.clear();
            InlineKeyboardButton prev = new InlineKeyboardButton("⏮\uFE0F")
                    .callbackData("prev_"+ firstHomeworkId);
            InlineKeyboardButton reject = new InlineKeyboardButton("❌")
                    .callbackData("reject");
            InlineKeyboardButton next = new InlineKeyboardButton("⏭\uFE0F")
                    .callbackData("next_"+ lastHomeworkId);
            row.add(prev);
            row.add(reject);
            row.add(next);
            markup.addRow(row.toArray(new InlineKeyboardButton[0]));
        }

        return markup;
    }

    private static void start(Long chatId,Update update) {
        if (adminChecker(chatId)) {
            userState.put(chatId, UserState.ADMIN_MENU);
            SendMessage sendMessage = new SendMessage(chatId, "Welcome Admin!");
            adminMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        } else {
            userState.put(chatId, UserState.USER_MENU);
            User user1 = DB.users.stream().filter(user -> user.getChatID().equals(chatId))
                    .findFirst()
                    .orElse(null);
            if (user1 == null) {
                User user = new User();
                user.setChatID(chatId);
                user.setFirstName(update.message().chat().firstName());
                user.setLastName(update.message().chat().lastName());
                user.setUsername(update.message().chat().username());
                DB.users.add(user);
            }
            SendMessage sendMessage = new SendMessage(chatId, "Welcome User!");
            userMenuKeyboards(sendMessage);
            bot.execute(sendMessage);
        }
    }

    private static void userMenuKeyboards(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup("Send Homework", "Show Old Homeworks");
        replyMarkup.resizeKeyboard(true);
        sendMessage.replyMarkup(replyMarkup);
    }

    private static void adminMenuKeyboards(SendMessage sendMessage) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup("Check Homework", "Show Old Homeworks");
        replyMarkup.resizeKeyboard(true);
        sendMessage.replyMarkup(replyMarkup);
    }

    private static boolean adminChecker(Long chatID) {
        return chatID.equals(Long.valueOf(ResourceBundle.getBundle("settings").getString("bot.adminID")));
    }
}
