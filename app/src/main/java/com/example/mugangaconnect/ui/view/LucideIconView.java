package com.example.mugangaconnect.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.mugangaconnect.R;

public class LucideIconView extends AppCompatImageView {
    private LucideDrawable lucideDrawable;
    private int iconColor = Color.BLACK;

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
            lucideDrawable = new LucideDrawable(iconName);
            setWillNotDraw(false);
            ColorStateList tintList = getImageTintList();
            if (tintList != null) {
                iconColor = tintList.getColorForState(getDrawableState(), tintList.getDefaultColor());
                lucideDrawable.setTintList(tintList);
            } else {
                lucideDrawable.setBaseColor(iconColor);
            }
            setImageDrawable(lucideDrawable);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (lucideDrawable == null) {
            super.onDraw(canvas);
            return;
        }

        lucideDrawable.setBounds(getPaddingLeft(), getPaddingTop(),
                getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
        lucideDrawable.setState(getDrawableState());
        lucideDrawable.draw(canvas);
    }

    @Override
    public void setImageTintList(@Nullable ColorStateList tint) {
        super.setImageTintList(tint);
        if (lucideDrawable != null) {
            lucideDrawable.setTintList(tint);
        }
        invalidate();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        if (drawable instanceof LucideDrawable) {
            lucideDrawable = (LucideDrawable) drawable;
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if (lucideDrawable != null) {
            lucideDrawable.setState(getDrawableState());
        }
    }
}
