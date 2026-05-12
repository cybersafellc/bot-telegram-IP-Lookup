package org.ip_lookup.controllers;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.ip_lookup.utils.Lookup;
import org.ip_lookup.interfaces.Controllers;

import java.io.IOException;

public class IpLookup implements Controllers {
    @Override
    public void handler(Update update, TelegramBot bot) {
        long chatId = update.message().chat().id();
        String ipAddress = update.message().text();
        if(!Lookup.validation(ipAddress)){
            bot.execute(new SendMessage(chatId, "Invalid format"));
            start(update, bot);
            return;
        }
        try{
            String result = Lookup.prettyJson(Lookup.lookupv2(ipAddress));
            SendMessage msg = new SendMessage(chatId, "```json\n" +
                    Lookup.escapeMarkdownV2(result) +
                    "\n```");
            msg.setParseMode(ParseMode.MarkdownV2);
            bot.execute(msg);
        } catch (IOException e) {
            bot.execute(new SendMessage(chatId, e.getMessage()));
        }
    }

    @Override
    public void start(Update update, TelegramBot bot) {
        long chatId = update.message().chat().id();
        bot.execute(new SendMessage(chatId, "Please sent valid ip address :\n\nExample : \n100.1.1.1\n\nWe are not accpet :\n- Local or private IP Address"));
    }
}
