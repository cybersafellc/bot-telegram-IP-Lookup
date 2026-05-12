package org.ip_lookup.interfaces;

import com.pengrad.telegrambot.model.Update;

public interface Routers {
    void setHandler(String route, Controllers controller);
    void setStartMessage(String message);
    void getHandler(Update update);
}
