package com.alexmarken.navigator.my.university.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.R;


public class AboutFragment extends Fragment {
    private static final String FRAGMENT_TITLE = "О приложении";

    public View rootView;

    public static AboutFragment newInstance() {
        return new AboutFragment();
    }

    public AboutFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_descr, container, false);

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        NavigatorLibrary.naviMain.postOnAttach(FRAGMENT_TITLE);
    }
}