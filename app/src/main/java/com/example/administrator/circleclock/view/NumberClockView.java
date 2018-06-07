package com.example.administrator.circleclock.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.circleclock.R;
import com.example.administrator.circleclock.SpanUtils;

import java.lang.ref.WeakReference;
import java.util.Calendar;

public class NumberClockView extends LinearLayout {
    private final static int WHAT_START_NUM_TIMER = 100;

    private Handler mHandler;

    private TextView mTvDay;
    private TextView mTvTime;
    private boolean mIsNight;

    public NumberClockView(Context context) {
        this(context, null);
    }

    public NumberClockView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mHandler = new MyHandler(this);
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_num_clock, this);
        mTvDay = findViewById(R.id.tv_day_type);
        mTvTime = findViewById(R.id.tv_time_num);
        this.post(new Runnable() {
            @Override
            public void run() {
                start();
            }
        });
    }

    public void start() {
        refreshTime();
        if (mHandler != null && !mHandler.hasMessages(WHAT_START_NUM_TIMER)) {
            mHandler.sendEmptyMessageDelayed(WHAT_START_NUM_TIMER,
                    (60 - Calendar.getInstance().get(Calendar.SECOND)) * 1000);
        }
    }

    public void refreshTime() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int min = Calendar.getInstance().get(Calendar.MINUTE);
        if (hour >= 24 || hour < 0 || min >= 60 || min < 0) {
            Toast.makeText(getContext(), "时间不合法", Toast.LENGTH_SHORT).show();
            return;
        }

        //添加一个变量，用于记录是否为下午。
        mIsNight = hour > 12;

        String dayType = mIsNight ? "下午" : "上午";
        int dayHour = mIsNight ? hour - 12 : hour;
        @SuppressLint("DefaultLocale")
        String timeHour = dayHour > 9 ? String.valueOf(dayHour) : String.format("0%d", dayHour);
        @SuppressLint("DefaultLocale")
        String timeMin = min > 9 ? String.valueOf(min) : String.format("0%d", min);
        mTvDay.setText(dayType);
        mTvTime.setText(new SpanUtils()
                .append(timeHour + ":" + timeMin)
                .setTypeface(Typeface.createFromAsset(getContext().getApplicationContext().getAssets(),
                        "font/2C018_1.TTF"))
                .create());
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeMessages(WHAT_START_NUM_TIMER);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<NumberClockView> mViewWp;

        private MyHandler(NumberClockView view) {
            mViewWp = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == WHAT_START_NUM_TIMER
                    && mViewWp != null && mViewWp.get() != null) {
                mViewWp.get().refreshTime();
                this.sendEmptyMessageDelayed(WHAT_START_NUM_TIMER, 60000);
            }
        }
    }

}
