package org.ip_lookup.controllers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.ip_lookup.Main;
import org.ip_lookup.interfaces.Controllers;

public class StartControllers implements Controllers {
    private String message = Main.startMessage;

    @Override
    public void handler(Update update, TelegramBot bot) {
        long chatId = update.message().chat().id();
        bot.execute(new SendMessage(chatId, message));
    }

    @Override
    public void start(Update update, TelegramBot bot) {
        long chatId = update.message().chat().id();
        bot.execute(new SendMessage(chatId, message));
    }
}
