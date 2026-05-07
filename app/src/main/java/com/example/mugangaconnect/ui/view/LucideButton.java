package com.example.mugangaconnect.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.example.mugangaconnect.R;

public class LucideButton extends AppCompatButton {
    public LucideButton(Context context) {
        super(context);
        init(context, null);
    }

    public LucideButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LucideButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LucideButton);
        String drawableEnd = typedArray.getString(R.styleable.LucideButton_lucideDrawableEnd);
        typedArray.recycle();

        if (drawableEnd != null) {
            Drawable icon = new LucideDrawable(drawableEnd);
            icon.setTint(getCurrentTextColor());
            int iconSize = Math.round(24 * getResources().getDisplayMetrics().density);
            icon.setBounds(0, 0, iconSize, iconSize);
            setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, icon, null);
        }
    }
}
