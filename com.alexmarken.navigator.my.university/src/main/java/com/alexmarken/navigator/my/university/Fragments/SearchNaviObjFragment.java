package com.alexmarken.navigator.my.university.Fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.R;

import java.util.ArrayList;


public class SearchNaviObjFragment extends Fragment {
    private static final String FRAGMENT_TITLE = "Расширенный поиск";

    public View rootView;

    private AppCompatActivity mainActivity;
    private Spinner spSearchType;
    private ScrollView scrollView;

    private ArrayAdapter<String> typeAdapter;

    public static SearchNaviObjFragment newInstance() {
        return new SearchNaviObjFragment();
    }

    public SearchNaviObjFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_obj, container, false);

        mainActivity = NavigatorLibrary.mainActivity;

        scrollView = (ScrollView) rootView.findViewById(R.id.scrollView);
        scrollView.setVerticalScrollBarEnabled(false);

        ArrayList<String> sTypes = new ArrayList<String>();
        sTypes.add("Аудиторию");
        sTypes.add("Преподавателя");

        spSearchType = (Spinner) rootView.findViewById(R.id.spSearchType);
        typeAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, sTypes);
        spSearchType.setAdapter(typeAdapter);

        spSearchType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();
                Fragment fragment = null;

                if (position == 0) {
                    fragment = SearchNaviAuditorFragment.newInstance();
                }
                else if (position == 1) {
                    fragment = SearchNaviTeachersFragment.newInstance();
                }

                if (fragment != null) {
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    transaction.setCustomAnimations(R.anim.slide_next_in, R.anim.slide_next_out);
                    transaction.replace(R.id.frament_frameLayout, fragment).commit();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        NavigatorLibrary.naviMain.postOnAttach(FRAGMENT_TITLE);
    }
}