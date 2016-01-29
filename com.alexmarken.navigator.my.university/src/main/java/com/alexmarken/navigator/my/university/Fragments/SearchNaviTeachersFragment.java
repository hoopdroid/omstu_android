package com.alexmarken.navigator.my.university.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.alexmarken.navigator.my.university.engine.Navigator;
import com.alexmarken.navigator.my.university.engine.navigator.naviPerson;
import com.alexmarken.navigator.my.university.util.Boxlists.SearchPersonAdapter;
import com.alexmarken.navigator.my.university.util.Boxlists.SearchPersonListItemObject;
import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.R;

import java.util.ArrayList;


public class SearchNaviTeachersFragment extends Fragment {
    public View rootView;

    private AppCompatActivity mainActivity;
    private EditText spPersonName;
    private ListView lvPersonResult;

    private Navigator navigator;
    private SearchPersonAdapter boxAdapter;

    public static SearchNaviTeachersFragment newInstance() {
        return new SearchNaviTeachersFragment();
    }

    public SearchNaviTeachersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_teachers, container, false);

        mainActivity = NavigatorLibrary.mainActivity;
        navigator = NavigatorLibrary.engine.getUniversityById("omgups").getNavigator();

        spPersonName = (EditText) (rootView.findViewById(R.id.spPersonName));

        boxAdapter = new SearchPersonAdapter(rootView.getContext(), new ArrayList<SearchPersonListItemObject>());
        lvPersonResult = (ListView) (rootView.findViewById(R.id.lvPersonResult));
        lvPersonResult.setAdapter(boxAdapter);

        lvPersonResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchPersonListItemObject item = (SearchPersonListItemObject) (boxAdapter.getItem(position));
            }
        });

        spPersonName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boxAdapter = new SearchPersonAdapter(rootView.getContext(), findPerson(spPersonName.getText().toString()));
                lvPersonResult.setAdapter(boxAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    private ArrayList<SearchPersonListItemObject> findPerson(String arg0) {
        ArrayList<SearchPersonListItemObject> objlist = new ArrayList<SearchPersonListItemObject>();

        if (arg0.length() > 0) {
            arg0.toLowerCase();
            ArrayList<naviPerson> listing = navigator.getTeachers().getTeacherslist();

            for (int i = 0; i < listing.size(); i++) {
                String name = listing.get(i).getName();
                String descip = listing.get(i).getDescip();
                String dir = listing.get(i).getDirecting();

                if ((name.toLowerCase().indexOf(arg0) > -1) || (descip.toLowerCase().indexOf(arg0) > -1)|| (dir.toLowerCase().indexOf(arg0) > -1)) {

                    objlist.add(new SearchPersonListItemObject(listing.get(i)));
                }
            }
        }

        return objlist;
    }
}