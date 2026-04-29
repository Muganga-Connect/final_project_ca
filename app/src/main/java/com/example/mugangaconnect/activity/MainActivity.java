package com.example.mugangaconnect.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mugangaconnect.activity.BottomNavHelper;
import com.example.mugangaconnect.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        BottomNavHelper.setup(this, BottomNavHelper.Screen.DASHBOARD);
    }
}