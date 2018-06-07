package com.example.administrator.circleclock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.circleclock.view.CircleClockView;
import com.squareup.leakcanary.RefWatcher;

public class CircleClockFragment extends Fragment {
    private CircleClockView mCircleClockView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_circle_clock, container, false);
        mCircleClockView = view.findViewById(R.id.circle_clock);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCircleClockView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ((MainActivity)getActivity()).refWatcher;
        refWatcher.watch(this);
    }
}
