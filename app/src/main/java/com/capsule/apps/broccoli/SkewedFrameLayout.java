package com.capsule.apps.broccoli;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;


public class SkewedFrameLayout extends RelativeLayout {

    private static final String TAG = CustomRecyclerView.class.getSimpleName();

    private static final int AMBIENT_LIGHT = 55;
    private static final int DIFFUSE_LIGHT = 200;
    private static final float SPECULAR_LIGHT = 70;
    private static final float SHININESS = 200;
    private static final int MAX_INTENSITY = 0xFFFFFF;

    private Camera mCamera = new Camera();
    private Matrix mMatrix = new Matrix();

    private final Paint mPaint = new Paint(Paint.FILTER_BITMAP_FLAG);
    public SkewedFrameLayout(@NonNull Context context) {
        super(context);
        initialize();
    }

    public SkewedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public SkewedFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        mCamera = new Camera();
        mMatrix = new Matrix();
    }

   /*@Override
    public boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Bitmap bitmap = getChildDrawingCache(child);
        final int top = child.getTop();
        final int left = child.getLeft();

        final int childCenterY = child.getHeight() / 2;
        final int childCenterX = child.getWidth() / 2;
        final int parentCenterY = getHeight();

        final int absChildCenterY = child.getTop() + childCenterY;
        final int distanceY = parentCenterY - absChildCenterY;
        final int radius = 2 * getHeight();

        prepareMatrix(mMatrix, distanceY, radius);
        mMatrix.preTranslate(-childCenterX, -childCenterY);
        mMatrix.postTranslate(getWidth() / 2, getHeight() /2);
        mMatrix.postTranslate(left, top);

        canvas.drawBitmap(bitmap, mMatrix, mPaint);
        return true;
    }*/

    @Override
    public void onDraw(Canvas canvas) {
        canvas.translate(0, getHeight());
        canvas.skew(20f, 5f);
        canvas.translate(0, -getHeight());
        super.onDraw(canvas);
        invalidate();
    }
    private void prepareMatrix(final Matrix outMatrix, int distanceY, int radius) {
        final int distance = Math.min(radius, Math.abs(distanceY));
        Log.d(TAG, String.format("Distance: %d ,Radius: %d, DistanceY: %d", distance, radius, distanceY));

        final float translationZ = (float) Math.sqrt((radius * radius) - (distance * distance));

        double radians = Math.acos((float) distance / radius);
        double degree = 90 - (180 / Math.PI) * radians;

        mCamera.save();
        Log.d(TAG, String.format("Radius - TranslationZ: %f", radius - translationZ));
        mCamera.rotate(2, 0, 0);
        //mCamera.translate(0, 0, radius - translationZ);
        /*mCamera.rotateX((float) degree);
        if (distanceY < 0) {
            degree = 360 - degree;
        }
        mCamera.rotateY((float) degree);*/
        mCamera.getMatrix(outMatrix);
        mCamera.restore();

        //mPaint.setColorFilter(calculateLight((float) degree));
    }

    private Bitmap getChildDrawingCache(final View child) {
        Bitmap bitmap = child.getDrawingCache();
        if (bitmap == null) {
            child.setDrawingCacheEnabled(true);
            child.buildDrawingCache();
            bitmap = child.getDrawingCache();
        }
        return bitmap;
    }

    private LightingColorFilter calculateLight(final float rotation) {
        final double cosRotation = Math.cos(Math.PI * rotation / 180);
        int intensity = AMBIENT_LIGHT + (int) (DIFFUSE_LIGHT * cosRotation);
        int highlightIntensity = (int) (SPECULAR_LIGHT * Math.pow(cosRotation, SHININESS));

        if (intensity > MAX_INTENSITY) {
            intensity = MAX_INTENSITY;
        }

        if (highlightIntensity > MAX_INTENSITY) {
            highlightIntensity = MAX_INTENSITY;
        }
        final int light = Color.rgb(intensity, intensity, intensity);
        final int highlight = Color.rgb(highlightIntensity, highlightIntensity, highlightIntensity);
        return new LightingColorFilter(light, highlight);
    }
}
