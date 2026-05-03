package org.ip_lookup.callbaks;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;

public class Exception implements ExceptionHandler {
    @Override
    public void onException(TelegramException e) {
        if (e.response() != null) {
            e.response().errorCode();
            e.response().description();
        } else {
            e.printStackTrace();
        }
    }
}
