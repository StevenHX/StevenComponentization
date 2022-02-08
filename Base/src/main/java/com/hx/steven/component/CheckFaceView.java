package com.hx.steven.component;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.hx.steven.R;

/**
 * 人脸识别控件
 */
public class CheckFaceView extends View {
    /**
     * 画笔
     */
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * 文字画笔
     */
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 字体大小
     */
    private float mTextSize = 60f;
    /**
     * 内圈bitmap
     */
    private Bitmap mInnerCircleBitmap = null;
    /**
     * 外圈bitmap
     */
    private Bitmap mOutCircleBitmap = null;
    /**
     * 旋转角度
     */
    private float mDegress = 0;
    /**
     * 动画
     */
    private ValueAnimator valueAnimator;

    public CheckFaceView(Context context) {
        this(context,null);
    }

    public CheckFaceView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public CheckFaceView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);

        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.BLACK);

        valueAnimator = ValueAnimator.ofFloat(0f,360f);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.RESTART);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(6000);
        valueAnimator.addUpdateListener(animation -> {
            mDegress = (float) animation.getAnimatedValue();
            postInvalidate();
        });
        valueAnimator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircleMask(canvas);
        drawBitmapCircle(canvas);
        canvas.drawText("请把脸移入圈内",getWidth() / 2f, (float) (getWidth() * 1.2),mTextPaint);
    }

    /**
     * 绘制圆圈遮罩
     * @param canvas
     */
    private void drawCircleMask(Canvas canvas) {
        canvas.save();
        canvas.drawRect(new Rect(0,0,getWidth(),getHeight()),mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(getWidth() / 2f, getWidth() / 2f, getWidth() / 3f, mPaint);
        mPaint.setXfermode(null);
        canvas.restore();
    }

    /**
     * 绘制外部圆圈
     * @param canvas
     */
    private void drawBitmapCircle(Canvas canvas) {
        if(mInnerCircleBitmap == null){
            int dstWidthAndHeight = (int) (getWidth() / 1.5f + getWidth() / 1.5f / 4);
            mInnerCircleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.checkinnercircle);
            mInnerCircleBitmap = Bitmap.createScaledBitmap(mInnerCircleBitmap,dstWidthAndHeight,dstWidthAndHeight,true);
        }
        if(mOutCircleBitmap == null){
            int dstWidthAndHeight = (int) (getWidth() / 1.5f + getWidth() / 1.5f / 4);
            mOutCircleBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.checheckoutcircle);
            mOutCircleBitmap = Bitmap.createScaledBitmap(mOutCircleBitmap,dstWidthAndHeight,dstWidthAndHeight,true);
        }
        int left = (getWidth() - mInnerCircleBitmap.getWidth()) / 2;
        int top = (int) (getWidth() / 2 - getWidth() / 3 - getWidth() / 1.5f / 8);

        canvas.save();
        canvas.rotate(mDegress,getWidth() / 2f, getWidth() / 2f);
        canvas.drawBitmap(mInnerCircleBitmap,left,top,mPaint);
        canvas.restore();

        canvas.save();
        canvas.rotate(-mDegress,getWidth() / 2f, getWidth() / 2f);
        canvas.drawBitmap(mOutCircleBitmap,left,top,mPaint);
        canvas.restore();
    }

    public void resumeAnim(){
        if(valueAnimator == null){
            return;
        }
        if(valueAnimator.isStarted()){
            valueAnimator.resume();
        }else {
            valueAnimator.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(valueAnimator != null){
            valueAnimator.cancel();
        }
    }

    public void pauseAnim(){
        if(valueAnimator != null && valueAnimator.isRunning()){
            valueAnimator.pause();
        }
    }
}
