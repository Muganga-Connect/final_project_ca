package com.example.mugangaconnect;

import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import eightbitlab.com.blurview.BlurTarget;
import eightbitlab.com.blurview.BlurView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        BlurView bottomBar = findViewById(R.id.bottomBar);
        BlurTarget blurTarget = findViewById(R.id.blurTarget);
        if (bottomBar != null && blurTarget != null) {
            View decorView = getWindow().getDecorView();
            Drawable windowBackground = decorView.getBackground();

            bottomBar.setupWith(blurTarget)
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(18f);

            bottomBar.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            bottomBar.setClipToOutline(true);
        }
    }
}