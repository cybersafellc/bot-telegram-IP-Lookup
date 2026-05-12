package org.ip_lookup.interfaces;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

public interface Controllers {
    void handler(Update update, TelegramBot bot);
    void start(Update update, TelegramBot bot);
}
