package org.ip_lookup.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import org.ip_lookup.interfaces.Controllers;
import org.ip_lookup.utils.Lookup;

import java.io.IOException;

import static org.ip_lookup.utils.Lookup.databases;

public class ProxyLookup implements Controllers {

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
            JsonNode node = Lookup.maper.readTree(result);
            int asn = node.get(0).get("autonomous_system_number").asInt();
            boolean isRom = Lookup.databases.get("AS" + asn);
            bot.execute(new SendMessage(chatId, ipAddress + " = " + (isRom ? "Resedentials or Mobile" : "Proxy")));
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
