package com.skobbler.sdkdemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.skobbler.sdkdemo.R;
import com.skobbler.sdkdemo.activity.MapActivity;

public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);

        ((MapActivity) getActivity()).initialize(view);

        return view;
    }
}
