package com.example.administrator.circleclock;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


public class MainActivity extends AppCompatActivity {
    private TextView mTvChangeClock;
    private FrameLayout mCircleFrame, mNumberFrame;
    private int mWidth;
    public RefWatcher refWatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        refWatcher = LeakCanary.install(getApplication());

        mTvChangeClock = findViewById(R.id.tv_change);
        mCircleFrame = findViewById(R.id.page_circle_clock);
        mNumberFrame = findViewById(R.id.page_num_clock);
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        assert wm != null;
        wm.getDefaultDisplay().getMetrics(dm);
        mWidth = dm.widthPixels;         // 屏幕宽度（像素）
        mNumberFrame.setTranslationX(mWidth);
    }


    public void changeClock(View view) {

        if (mCircleFrame.getTranslationX() == 0) {
            mCircleFrame.animate().translationX(-mWidth).setDuration(1500).start();
            mNumberFrame.animate().translationX(0).setDuration(1500).start();
            mTvChangeClock.setText("切换至家用挂钟");
        } else {
            mCircleFrame.animate().translationX(0).setDuration(1500).start();
            mNumberFrame.animate().translationX(mWidth).setDuration(1500).start();
            mTvChangeClock.setText("切换至电子时钟");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCircleFrame.clearAnimation();
        mNumberFrame.clearAnimation();
    }
}
