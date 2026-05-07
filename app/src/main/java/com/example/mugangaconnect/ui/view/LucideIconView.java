package com.example.mugangaconnect.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.graphics.PathParser;

import com.example.mugangaconnect.R;

public class LucideIconView extends AppCompatImageView {
    private static final float VIEWPORT_SIZE = 24f;
    private static final float STROKE_WIDTH = 2f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path[] iconPaths = new Path[0];
    private ColorStateList iconTint;
    private int iconColor = Color.BLACK;
    private ColorFilter iconColorFilter;

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
        setWillNotDraw(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);

        if (attrs == null) {
            return;
        }

        TypedArray lucideAttrs = context.obtainStyledAttributes(attrs, R.styleable.LucideIconView);
        String iconName = lucideAttrs.getString(R.styleable.LucideIconView_lucideIcon);
        lucideAttrs.recycle();

        if (iconName != null) {
            setLucideIcon(iconName);
        }

        ColorStateList imageTint = getSupportImageTintList();
        if (imageTint == null) {
            imageTint = getImageTintList();
        }
        if (imageTint != null) {
            setImageTintList(imageTint);
        }
    }

    public void setLucideIcon(String iconName) {
        String[] pathData = LucideIcons.pathsFor(iconName);
        if (pathData == null) {
            iconPaths = new Path[0];
            invalidate();
            return;
        }

        iconPaths = new Path[pathData.length];
        for (int i = 0; i < pathData.length; i++) {
            iconPaths[i] = PathParser.createPathFromPathData(pathData[i]);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (iconPaths.length == 0) {
            super.onDraw(canvas);
            return;
        }

        int availableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int availableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        if (availableWidth <= 0 || availableHeight <= 0) {
            return;
        }

        int resolvedColor = iconTint == null
                ? iconColor
                : iconTint.getColorForState(getDrawableState(), iconTint.getDefaultColor());
        float size = Math.min(availableWidth, availableHeight);
        float scale = size / VIEWPORT_SIZE;
        float dx = getPaddingLeft() + (availableWidth - size) / 2f;
        float dy = getPaddingTop() + (availableHeight - size) / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);

        paint.setColor(resolvedColor);
        paint.setAlpha(getImageAlpha());
        paint.setColorFilter(iconColorFilter);
        paint.setStrokeWidth(STROKE_WIDTH * scale);

        for (Path iconPath : iconPaths) {
            Path transformedPath = new Path();
            iconPath.transform(matrix, transformedPath);
            canvas.drawPath(transformedPath, paint);
        }
    }

    @Override
    public void setImageTintList(@Nullable ColorStateList tint) {
        super.setImageTintList(tint);
        iconTint = tint;
        invalidate();
    }

    @Override
    public void setSupportImageTintList(@Nullable ColorStateList tint) {
        super.setSupportImageTintList(tint);
        iconTint = tint;
        invalidate();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        super.setColorFilter(colorFilter);
        iconColorFilter = colorFilter;
        invalidate();
    }

    @Override
    public void setImageAlpha(int alpha) {
        super.setImageAlpha(alpha);
        invalidate();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invalidate();
    }
}
