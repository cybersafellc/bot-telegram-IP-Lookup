package org.ip_lookup;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.ip_lookup.callbaks.Listen;

import java.io.IOException;
import java.util.List;

public class Main {
    private static String apikey;
    public static TelegramBot bot;
    static {
        try {
            apikey = System.getenv("APIKEY_BOT");
            if(apikey.length() < 10){
                throw new IOException("telegram bot apikey not found or invalid, please set up in your system env. example : APIKEY_BOT=xxxxxxxxxxxxxxxxxxxx");
            }
            bot = new TelegramBot(apikey);
            System.out.println("[~] Bot Actived");
        } catch (Exception e) {
            System.out.println("[!] " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    static void main() {
        try {
            System.out.println("[~] Bot setup Listener Process...");
            bot.setUpdatesListener(new UpdatesListener() {
                @Override
                public int process(List<Update> updates) {
                    for(Update update : updates){
                        if(update.message() != null){
                            long chatId = update.message().chat().id();
                            String message = update.message().text();
                            System.out.println(chatId);
                            System.out.println(message);
                        }
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }
            }, new ExceptionHandler() {
                @Override
                public void onException(TelegramException e) {
                    if (e.response() != null) {
                        e.response().errorCode();
                        e.response().description();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
