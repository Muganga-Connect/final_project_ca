package com.example.mugangaconnect.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.mugangaconnect.R;

public class LucideIconView extends AppCompatImageView {
    public LucideIconView(Context context) {
        super(context);
        init(context, null);
    }

    public LucideIconView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LucideIconView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LucideIconView);
        String iconName = typedArray.getString(R.styleable.LucideIconView_lucideIcon);
        typedArray.recycle();

        if (iconName != null) {
            setImageDrawable(new LucideDrawable(iconName));
        }
    }
}
