package com.example.mugangaconnect.utils;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleHelper {

    public static final String LANG_ENGLISH = "en";
    public static final String LANG_FRENCH = "fr";
    public static final String LANG_KINYARWANDA = "rw";

    /**
     * Call in every Activity's attachBaseContext.
     * Reads the saved language and wraps the context so Android
     * loads the correct values-xx/strings.xml before setContentView.
     */
    public static Context applyLocale(Context context) {
        String lang = new SessionManager(context).getLanguage();
        return wrap(context, lang);
    }

    /** Wraps a context with the given locale. */
    public static Context wrap(Context context, String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration config = new Configuration(context.getResources().getConfiguration());
        config.setLocale(locale);
        return context.createConfigurationContext(config);
    }
}
