package org.ip_lookup.callbaks;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.ip_lookup.Main;

import java.util.List;

public class Listen  implements UpdatesListener{
    @Override
    public int process(List<Update> updates) {
        for(Update update : updates){
            if(update.message() != null){
               SendResponse response = Main.bot.execute(new SendMessage(update.message().chat().id(), "hello man"));
               return 0;
            }
        }
        return 0;
    }
}
