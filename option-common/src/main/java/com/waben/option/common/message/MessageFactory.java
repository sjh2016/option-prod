package com.waben.option.common.message;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessageFactory {

    private static ResourceBundle cnRB;
    private static ResourceBundle usRB;
    private static ResourceBundle idRB;

    public static MessageFactory INSTANCE = new MessageFactory();

    private MessageFactory() {
        Locale cnLocale = new Locale("zh", "CN");
        Locale usLocale = new Locale("en", "US");
        Locale idLocale = new Locale("id", "ID");
        cnRB = ResourceBundle.getBundle("message", cnLocale);
        usRB = ResourceBundle.getBundle("message", usLocale);
        idRB = ResourceBundle.getBundle("message", idLocale);
    }

    private ResourceBundle getResourceBundle(String locale) {
        if (locale == null) {
            return usRB;
        }
        switch (locale) {
            case "zh":
                return cnRB;
            case "en":
                return usRB;
            case "id":
                return idRB;
        }
        return usRB;
    }

    public String getMessage(String key, String locale) {
        ResourceBundle rb = getResourceBundle(locale);
        return rb.getString(key);
    }

    public String getMessage(String key, String locale, String... args) {
        String message = getMessage(key, locale);
        return MessageFormat.format(message, args);
    }

    public String getMessage(String key, String locale, String countryCode) {
        Locale cnLocale = new Locale(locale, countryCode.toUpperCase());
        ResourceBundle rb = ResourceBundle.getBundle("message", cnLocale);
        return rb.getString(key);
    }

    public String getMessage(String key, String locale, String countryCode, String... args) {
        String message = getMessage(key, locale, countryCode);
        return MessageFormat.format(message, args);
    }

}
