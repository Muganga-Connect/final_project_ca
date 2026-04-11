package com.example.mugangaconnect;

import android.os.Bundle;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

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
        if (bottomBar != null) {
            View decorView = getWindow().getDecorView();
            ViewGroup rootView = decorView.findViewById(android.R.id.content);
            Drawable windowBackground = decorView.getBackground();

            bottomBar.setupWith(rootView, new RenderScriptBlur(this))
                    .setFrameClearDrawable(windowBackground)
                    .setBlurRadius(18f);

            bottomBar.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
            bottomBar.setClipToOutline(true);
        }
    }
}