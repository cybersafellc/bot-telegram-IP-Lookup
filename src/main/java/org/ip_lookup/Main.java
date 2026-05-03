package org.ip_lookup;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

public class Main {
    static void main() {
        String apikey = System.getenv("API_KEY_BOT_TELE1");
        System.out.println(apikey);
        TelegramBot bot = new TelegramBot(System.getenv("API_KEY_BOT_TELE1"));
        bot.setUpdatesListener(updates -> {
            for(Update update : updates){
                if(update.message() != null){
                    long chatId = update.message().chat().id();
                    SendResponse response = bot.execute(new SendMessage(chatId, "H"));
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                e.response().errorCode();
                e.response().description();
            } else {
                // probably network error
                e.printStackTrace();
            }
        });

    }
}
