package org.ip_lookup;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import org.ip_lookup.callbaks.Listen;

import java.io.IOException;

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
            bot.setUpdatesListener(new Listen(), new ExceptionHandler(){
                @Override
                public void onException(TelegramException e){

                }
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
