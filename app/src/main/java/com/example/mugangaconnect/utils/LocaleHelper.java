package com.example.mugangaconnect.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    public static final String LANG_ENGLISH     = "en";
    public static final String LANG_FRENCH      = "fr";
    public static final String LANG_KINYARWANDA = "rw";

    public static Context applyLocale(Context context) {
        String lang = new SessionManager(context).getLanguage();
        return wrap(context, lang);
    }

    public static Context wrap(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }
}
