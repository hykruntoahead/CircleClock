package com.example.administrator.circleclock;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.circleclock.view.NumberClockView;
import com.squareup.leakcanary.RefWatcher;

public class NumberClockFragment extends Fragment {
    private NumberClockView mNumberClock;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_number_clock, container, false);
        mNumberClock = view.findViewById(R.id.ncv);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mNumberClock.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = ((MainActivity)getActivity()).refWatcher;
        refWatcher.watch(this);
    }
}
