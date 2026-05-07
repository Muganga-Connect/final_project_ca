package com.example.mugangaconnect.ui.view;

import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.PathParser;

public final class LucideDrawable extends Drawable {
    private static final float VIEWPORT_SIZE = 24f;
    private static final float DEFAULT_STROKE_WIDTH = 2f;

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path[] paths;
    private ColorStateList tintList;
    private int alpha = 255;
    private int color = Color.BLACK;

    public LucideDrawable(String iconName) {
        String[] pathData = LucideIcons.pathsFor(iconName);
        if (pathData == null) {
            pathData = new String[0];
        }
        paths = new Path[pathData.length];
        for (int i = 0; i < pathData.length; i++) {
            paths[i] = PathParser.createPathFromPathData(pathData[i]);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH);
        paint.setColor(color);
    }

    void setBaseColor(int color) {
        this.color = color;
        invalidateSelf();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            return;
        }

        int resolvedColor = tintList == null
                ? color
                : tintList.getColorForState(getState(), tintList.getDefaultColor());
        paint.setColor(resolvedColor);
        paint.setAlpha(alpha);

        float size = Math.min(bounds.width(), bounds.height());
        float scale = size / VIEWPORT_SIZE;
        float dx = bounds.left + (bounds.width() - size) / 2f;
        float dy = bounds.top + (bounds.height() - size) / 2f;

        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        paint.setStrokeWidth(DEFAULT_STROKE_WIDTH * scale);

        for (Path sourcePath : paths) {
            Path scaledPath = new Path();
            sourcePath.transform(matrix, scaledPath);
            canvas.drawPath(scaledPath, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        this.alpha = alpha;
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public void setTint(int tintColor) {
        tintList = ColorStateList.valueOf(tintColor);
        invalidateSelf();
    }

    @Override
    public void setTintList(@Nullable ColorStateList tint) {
        tintList = tint;
        invalidateSelf();
    }

    @Override
    protected boolean onStateChange(@NonNull int[] state) {
        invalidateSelf();
        return tintList != null && tintList.isStateful();
    }

    @Override
    public boolean isStateful() {
        return tintList != null && tintList.isStateful();
    }

    @Override
    public int getIntrinsicWidth() {
        return 24;
    }

    @Override
    public int getIntrinsicHeight() {
        return 24;
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
