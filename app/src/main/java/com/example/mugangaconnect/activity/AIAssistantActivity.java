package com.example.mugangaconnect.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.R;

public class AIAssistantActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ai_assistant);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.aiAssistantContainer, new AiAssistantFragment())
                    .commit();
        }
    }
}
