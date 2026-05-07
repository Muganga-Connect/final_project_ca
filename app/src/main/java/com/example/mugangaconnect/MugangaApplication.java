package com.example.mugangaconnect;

import android.app.Application;

import com.google.firebase.FirebaseApp;

/**
 * Global Application class for MugangaConnect
 * Handles Firebase initialization before any Activities are created
 */
public class MugangaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Firebase globally before any Activity starts
        FirebaseApp.initializeApp(this);
    }
}
