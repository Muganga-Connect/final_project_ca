package com.example.mugangaconnect;

import android.app.Application;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.os.Bundle;

import com.example.mugangaconnect.utils.SessionManager;
import com.google.firebase.FirebaseApp;

/**
 * Global Application class for MugangaConnect
 * Handles Firebase initialization and Theme management
 */
public class MugangaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase globally
        FirebaseApp.initializeApp(this);

        // Apply saved Dark Mode preference
        SessionManager session = new SessionManager(this);
        AppCompatDelegate.setDefaultNightMode(session.isDarkMode() ? 
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }

    private static abstract class ActivityLifecycleAdapter implements ActivityLifecycleCallbacks {
        @Override public void onActivityCreated(android.app.Activity activity, android.os.Bundle savedInstanceState) {}
        @Override public void onActivityStarted(android.app.Activity activity) {}
        @Override public void onActivityResumed(android.app.Activity activity) {}
        @Override public void onActivityPaused(android.app.Activity activity) {}
        @Override public void onActivityStopped(android.app.Activity activity) {}
        @Override public void onActivitySaveInstanceState(android.app.Activity activity, android.os.Bundle outState) {}
        @Override public void onActivityDestroyed(android.app.Activity activity) {}
    }
}
