package org.ip_lookup.route;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.ip_lookup.interfaces.Controllers;
import org.ip_lookup.interfaces.Routers;

public class Route implements Routers {
    private final TelegramBot bot;
    private final Map<String, Controllers> router = new HashMap<>();
    private final Map<String, Controllers> exeChatId = new HashMap<>();
    private String startMessage;

    public Route(TelegramBot bot){
        this.bot = bot;
    }

    @Override
    public void setHandler(String route, Controllers controller){
        if(startMessage == null){
            throw new RuntimeException("Please set start message first");
        }
        this.router.put(route, controller);
    }

    @Override
    public void setStartMessage(String message) {
        this.startMessage = message;
    }

    @Override
    public void getHandler(Update update){
        String route = update.message().text();
        String chatId = String.valueOf(update.message().chat().id());

        Controllers controller = exeChatId.get(chatId);

        if(!route.contains("/")){
            if(controller == null){
                bot.execute(new SendMessage(chatId, "Please sent parameters first !"));
                bot.execute(new SendMessage(chatId, startMessage));
                return;
            }
            controller.handler(update, bot);
            return;
        }

        Controllers controllerToSet = router.get(route);
        if(controllerToSet == null){
            bot.execute(new SendMessage(chatId, "Please sent valid parameters !"));
            bot.execute(new SendMessage(chatId, startMessage));
        }else{
            exeChatId.put(chatId, controllerToSet);
            controllerToSet.start(update, bot);
        }
    }
}
