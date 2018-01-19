package com.jack.root.baidumapdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by root on 18-1-19.
 */

@SuppressLint("AppCompatCustomView")
public class BadgeView extends ImageView {
    private Paint mTextPaint;
    private Paint mTextBgPaint;
    private String mNumText = "";
    private int mTextColor = 0XFFFFFF;
    private float mTextSize = 10.0f;

    private float mRadio = 20.0f;
    private int mTextBgColor = 0XB22222;

    private Point mPoint; // 经纬度对应的屏幕位置
    private final static String TAG = "BadgeView";

    public BadgeView(Context context) {
        super(context);

        initTextPaint();
        initTextBgPaint();
    }

    public BadgeView(Context context, Point point) {
        super(context);
        this.mPoint = point;

        initTextPaint();
        initTextBgPaint();
    }

    public BadgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BadgeView);
        mTextColor = a.getColor(R.styleable.BadgeView_textColor, 0XFFFFFFFF);
        mTextSize = a.getDimension(R.styleable.BadgeView_textSize, 10);

        mTextBgColor = a.getColor(R.styleable.BadgeView_textBgColor, 0XB22222);
        mRadio = a.getDimension(R.styleable.BadgeView_textBgRadio, 16);

        a.recycle();

        initTextPaint();
        initTextBgPaint();
    }

    private void initTextBgPaint() {
        mTextBgPaint = new Paint();
        mTextBgPaint.setStyle(Paint.Style.FILL);
        mTextBgPaint.setColor(mTextBgColor);
    }

    private void initTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (TextUtils.isEmpty(mNumText) || mRadio <= 0.0F || mPoint == null) {
            throw new RuntimeException("no attrs...");
        }

        float circleX = getWidth() / 2 + mPoint.x;
        float circleY = mPoint.y - getHeight();
        Log.d(TAG, "circleX: " + circleX);
        Log.d(TAG, "circleY: " + circleY);
        canvas.drawCircle(circleX, circleY, mRadio, mTextBgPaint);

        canvas.drawText(mNumText, circleX, circleY, mTextPaint);
    }

    public void setmNumText(String numText) {
        this.mNumText = numText;
        postInvalidate();
    }

    public void setmTextPaint(Paint mTextPaint) {
        this.mTextPaint = mTextPaint;
        postInvalidate();
    }

    public void setmTextBgPaint(Paint mTextBgPaint) {
        this.mTextBgPaint = mTextBgPaint;
        postInvalidate();
    }

    public void setmTextColor(int mTextColor) {
        this.mTextColor = mTextColor;
        postInvalidate();
    }

    public void setmTextSize(float mTextSize) {
        this.mTextSize = mTextSize;
        postInvalidate();
    }

    public void setmRadio(float mRadio) {
        this.mRadio = mRadio;
        postInvalidate();
    }

    public void setmTextBgColor(int mTextBgColor) {
        this.mTextBgColor = mTextBgColor;
        postInvalidate();
    }
}
