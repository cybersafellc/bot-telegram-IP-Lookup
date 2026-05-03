package org.ip_lookup;

import com.pengrad.telegrambot.TelegramBot;
import org.ip_lookup.Secret;
import org.ip_lookup.callbaks.Exception;
import org.ip_lookup.callbaks.Listen;

public class Main {
    private static String apikey = new Secret().getTeleBotApiKey();
    public static TelegramBot bot = new TelegramBot(apikey);
    static void main() {
        bot.setUpdatesListener(new Listen(), new Exception());
    }
}
