package com.example.administrator.circleclock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.circleclock.R;

import java.lang.ref.WeakReference;
import java.util.Calendar; 

public class CircleClockView extends View {
    private Paint mPaint;
    private Paint mTextPaint;
    private float mDialRadius;

    @ColorInt
    private int mHourNeedleColor;
    private float mHourNeedleWidth;
    private float mHourNeedleHeight;

    @ColorInt
    private int mMinNeedleColor;
    private float mMinNeedleWidth;
    private float mMinNeedleHeight;

    @ColorInt
    private int mSecondNeedleColor;
    private float mSecondNeedleWidth;
    private float mSecondNeedleHeight;


    @ColorInt
    private int mDialNumColor;
    private float mDialNumSize;
    private float mDialNumOffset;

    @ColorInt
    private int mDialLineColor;
    private float mDialLineWidth;
    private float mDialLineHeight;
    private float mDialLineOffset;

    @ColorInt
    private int mCenterCircleColor;
    private float mCenterCircleRadius;

    @ColorInt
    private int mCenterRingColor;
    private float mCenterRingRadius;

    @ColorInt
    private int mEdgeRingColor;
    private float mEdgeRingRadius;

    @ColorInt
    private int mDialBackgroundColor;


    private float mSecondDegree;//秒针的度数
    private float mMinDegree;//秒针的度数
    private float mHourDegree;//秒针的度数
    private boolean mIsNight;

    private final static int WHAT_START_CIRCLE_TIMER = 101;

    private Handler mHandler;

    public CircleClockView(Context context) {
        this(context, null);
    }

    public CircleClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new MyHandler(this);
        initView(context, attrs);
    }

    private void initView(Context context, @Nullable AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleClockView);

        //Hour - Needle
        mHourNeedleColor = ta.getColor(R.styleable.CircleClockView_hour_needle_color,
                Color.parseColor("#333333"));
        mHourNeedleHeight = ta.getDimensionPixelSize(R.styleable.CircleClockView_hour_needle_height,
                80);
        mHourNeedleWidth = ta.getDimensionPixelSize(R.styleable.CircleClockView_hour_needle_width,
                20);

        //Min - Needle
        mMinNeedleColor = ta.getColor(R.styleable.CircleClockView_min_needle_color,
                Color.parseColor("#333333"));
        mMinNeedleHeight = ta.getDimensionPixelSize(R.styleable.CircleClockView_min_needle_height,
                130);
        mMinNeedleWidth = ta.getDimensionPixelSize(R.styleable.CircleClockView_min_needle_width,
                14);

        //second - needle
        mSecondNeedleColor = ta.getColor(R.styleable.CircleClockView_second_needle_color,
                Color.parseColor("#FF7676"));
        mSecondNeedleHeight = ta.getDimensionPixelSize(R.styleable.CircleClockView_second_needle_height,
                160);
        mSecondNeedleWidth = ta.getDimensionPixelSize(R.styleable.CircleClockView_second_needle_width,
                4);

        //dial num
        mDialNumSize = ta.getDimensionPixelSize(R.styleable.CircleClockView_dial_num_textSize,
                50);
        mDialNumColor = ta.getColor(R.styleable.CircleClockView_dial_num_textColor,
                Color.BLACK);
        mDialNumOffset = ta.getDimensionPixelSize(R.styleable.CircleClockView_dial_num_offset,
                50);

        //dial line
        mDialLineColor = ta.getColor(R.styleable.CircleClockView_dial_line_color,
                Color.parseColor("#333333"));
        mDialLineWidth = ta.getDimensionPixelSize(R.styleable.CircleClockView_dial_line_width,
                8);
        mDialLineHeight = ta.getDimensionPixelSize(R.styleable.CircleClockView_dial_line_height,
                24);
        mDialLineOffset = ta.getDimensionPixelSize(R.styleable.CircleClockView_dial_line_offset,
                48);

        //center circle
        mCenterCircleColor = ta.getColor(R.styleable.CircleClockView_circle_center_color,
                Color.parseColor("#ff7676"));
        mCenterCircleRadius = ta.getDimensionPixelSize(R.styleable.CircleClockView_circle_center_radius,
                13);

        //center ring
        mCenterRingColor = ta.getColor(R.styleable.CircleClockView_ring_center_color,
                Color.parseColor("#0870bf"));
        mCenterRingRadius = ta.getDimensionPixelSize(R.styleable.CircleClockView_ring_center_radius,
                7);

        //edge ring
        mEdgeRingColor = ta.getColor(R.styleable.CircleClockView_ring_edge_color,
                Color.parseColor("#157ccb"));
        mEdgeRingRadius = ta.getDimensionPixelSize(R.styleable.CircleClockView_ring_edge_radius,
                24);

        // dial background
        mDialBackgroundColor = ta.getColor(R.styleable.CircleClockView_dial_background_color,
                Color.WHITE);
        ta.recycle();
        initPaint();
        this.post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }


    private void initPaint() {
        mPaint = new Paint();
        //抗锯齿
        mPaint.setAntiAlias(true);

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mDialNumSize);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(mDialNumColor);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int paddingLeft = getPaddingLeft();
        int paddingRight = getPaddingRight();

        mDialRadius = w / 2 - mEdgeRingRadius - paddingLeft - paddingRight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int w = measureSize(widthMeasureSpec);
        int h = measureSize(heightMeasureSpec);

        if (w != h) {
            h = w;
        }
        setMeasuredDimension(w, h);
    }

    //测量宽度的方法
    private int measureSize(int measureSpec) {
        int result;
        int specSize = MeasureSpec.getSize(measureSpec);
        int specMode = MeasureSpec.getMode(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 300;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(getWidth() / 2,
                getHeight() / 2);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mDialBackgroundColor);
        canvas.drawCircle(0, 0, mDialRadius, mPaint);

        //圆形边框
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mEdgeRingRadius);
        mPaint.setColor(mEdgeRingColor);
        canvas.drawCircle(0, 0, mDialRadius, mPaint);


        mPaint.setStrokeCap(Paint.Cap.ROUND);

        canvas.save();
        for (int i = 0; i < 12; i++) {
            if (i % 3 != 0) {
                Log.d("iiii=", i + "");
                mPaint.setColor(mDialLineColor);
                mPaint.setStrokeWidth(mDialLineWidth);
                if (i != 1 && i % 3 == 1) {
                    canvas.rotate(30);
                }
                canvas.rotate(30);
                canvas.drawLine(0, -(mDialRadius - mDialLineOffset), 0,
                        -(mDialRadius - (mDialLineOffset + mDialLineHeight)), mPaint);
            }
        }
        canvas.restore();


        canvas.save();
        for (int i = 0; i < 12; i++) {
            if (i % 3 == 0) {
                if (i == 0) {
                    drawNum(canvas, i * 30, 12 + "", mTextPaint);
                } else {
                    drawNum(canvas, i * 30, i + "", mTextPaint);
                }
            }
        }
        canvas.restore();


        //秒针
        canvas.save();
        mPaint.setColor(mSecondNeedleColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mSecondNeedleWidth);
        canvas.rotate(mSecondDegree);
        //其实坐标点(0,0)  终点坐标(0,-190)，这里190为秒针长度
        canvas.drawLine(0, 0, 0, -mSecondNeedleHeight, mPaint);
        canvas.restore();

        //分针
        canvas.save();
        mPaint.setColor(mMinNeedleColor);
        mPaint.setStrokeWidth(mMinNeedleWidth);
        canvas.rotate(mMinDegree);
        canvas.drawLine(0, 0,
                0, -mMinNeedleHeight, mPaint);
        canvas.restore();

        //时z
        canvas.save();
        mPaint.setColor(mHourNeedleColor);
        mPaint.setStrokeWidth(mHourNeedleWidth);
        canvas.rotate(mHourDegree);
        canvas.drawLine(0, 0,
                0, -mHourNeedleHeight, mPaint);
        canvas.restore();


        //圆心
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mCenterCircleColor);
        canvas.drawCircle(0, 0, mCenterCircleRadius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCenterRingRadius);
        mPaint.setColor(mCenterRingColor);
        canvas.drawCircle(0, 0, mCenterCircleRadius, mPaint);

    }

    private void drawNum(Canvas canvas, int degree,
                         String text, Paint paint) {
        Rect textBound = new Rect();
        //将文字装入textBound矩形中
        paint.getTextBounds(text, 0, text.length(), textBound);
        canvas.rotate(degree);
        //这里50是坐标中心距离时钟最外框的距离
        canvas.translate(0, mDialNumOffset - mDialRadius);
        canvas.rotate(-degree);
        canvas.drawText(text, -textBound.width() / 2,
                textBound.height() / 2, paint);
        canvas.rotate(degree);
        canvas.translate(0, mDialRadius - mDialNumOffset);
        canvas.rotate(-degree);
    }


    /**
     * 开启定时器
     */
    public void start() {
        setTime();
        if (mHandler != null && !mHandler.hasMessages(WHAT_START_CIRCLE_TIMER)) {
            mHandler.sendEmptyMessageDelayed(WHAT_START_CIRCLE_TIMER, 1000);
        }
    }

    /**
     * 设置时间
     */
    public void setTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);

        if (hour >= 24 || hour < 0 || min >= 60 || min < 0 || second >= 60 || second < 0) {
            Toast.makeText(getContext(), "时间不合法", Toast.LENGTH_SHORT).show();
            return;
        }
        if (hour >= 12) {
            //添加一个变量，用于记录是否为下午。
            mIsNight = true;
            mHourDegree = (hour + min * 1.0f / 60f + second * 1.0f / 3600f - 12) * 30f;
        } else {
            mIsNight = false;
            mHourDegree = (hour + min * 1.0f / 60f + second * 1.0f / 3600f) * 30f;
        }

        mMinDegree = (min + second * 1.0f / 60f) * 6f;
        mSecondDegree = second * 6f;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_START_CIRCLE_TIMER);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private float getTimeTotalSecond() {
        float mTotalSecond;
        if (mIsNight) {
            mTotalSecond = mHourDegree * 120 + 12 * 3600;
            return mTotalSecond;
        } else {
            mTotalSecond = mHourDegree * 120;
            return mTotalSecond;
        }
    }

    public int getHour() {
        return (int) (getTimeTotalSecond() / 3600);
    }

    public int getMin() {
        return (int) ((getTimeTotalSecond() - getHour() * 3600) / 60);
    }

    public int getSecond() {//获取秒钟
        return (int) (getTimeTotalSecond() - getHour() * 3600 - getMin() * 60);
    }


    private static class MyHandler extends Handler {
        private final WeakReference<CircleClockView> mViewWp;

        private MyHandler(CircleClockView view) {
            mViewWp = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_START_CIRCLE_TIMER
                    && mViewWp != null && mViewWp.get() != null) {
                if (mViewWp.get().mSecondDegree == 360) {
                    mViewWp.get().mSecondDegree = 0;
                }

                if (mViewWp.get().mMinDegree == 360) {
                    mViewWp.get().mMinDegree = 0;
                }

                if (mViewWp.get().mHourDegree == 360) {
                    mViewWp.get().mHourDegree = 0;
                }

                //秒针走完一圈是60秒，而一圈是360度，那么我们可以算出一秒钟，
                // 其实就是360度/60秒 = 6度
                mViewWp.get().mSecondDegree = mViewWp.get().mSecondDegree + 6;

                /**
                 * 分钟走一圈是60分钟，而圆的一圈是360度，那么一分钟，
                 *其实就是分针走了360度/60分钟 = 6度，而一分钟等于60秒，
                 *所以对应一秒钟就是，6度/60秒 = 0.1度/秒，
                 *即每隔一秒钟就让分针的度数加0.1度
                 */

                mViewWp.get().mMinDegree = mViewWp.get().mMinDegree + 0.1f;
                /**
                 * 时针走一圈是12小时，那么每小时就走360度/12小时，
                 * 而每小时等于3600秒，所以每秒钟也就是
                 * 360度/(12*3600秒) = 1/120度/秒
                 */
                mViewWp.get().mHourDegree = mViewWp.get().mHourDegree + 1.0f / 240;

                mViewWp.get().postInvalidate();
                this.sendEmptyMessageDelayed(WHAT_START_CIRCLE_TIMER, 1000);
            }
        }
    }
}
