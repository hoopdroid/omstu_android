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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.engine.Navigator;
import com.alexmarken.navigator.my.university.engine.navigator.naviAuditor;
import com.alexmarken.navigator.my.university.util.Boxlists.SearchAuditoryAdapter;
import com.alexmarken.navigator.my.university.util.Boxlists.SearchAuditoryListItemObject;
import com.alexmarken.navigator.my.university.R;

import java.util.ArrayList;


public class SearchNaviAuditorFragment extends Fragment {
    public View rootView;

    private AppCompatActivity mainActivity;
    private Spinner spAudCorpus;
    private EditText spAudName;
    private ListView lvAudResult;

    private Navigator navigator;
    private ArrayAdapter<String> corpusAdapter;
    private SearchAuditoryAdapter boxAdapter;

    private int sel_stage = 0;

    public static SearchNaviAuditorFragment newInstance() {
        return new SearchNaviAuditorFragment();
    }

    public SearchNaviAuditorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_search_auditor, container, false);

        mainActivity = NavigatorLibrary.mainActivity;
        navigator = NavigatorLibrary.engine.getUniversityById("omgups").getNavigator();

        spAudCorpus = (Spinner) (rootView.findViewById(R.id.spAudCorpus));
        spAudName = (EditText) (rootView.findViewById(R.id.spAudName));

        boxAdapter = new SearchAuditoryAdapter(rootView.getContext(), new ArrayList<SearchAuditoryListItemObject>());
        lvAudResult = (ListView) (rootView.findViewById(R.id.lvAudResult));
        lvAudResult.setAdapter(boxAdapter);

        lvAudResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SearchAuditoryListItemObject item = (SearchAuditoryListItemObject) (boxAdapter.getItem(position));

                int corp = item.getCorp();
                int stage = item.getStage();
                String auditory = item.getAuditor();

                NaviMapsFragment.loadmap(corp, stage, auditory);

                NavigatorLibrary.naviMain.onNavigationItemSelected(5);
            }
        });

        ArrayList<String> corpuslist = new ArrayList<String>();

        for(int i = 1 ; i <= navigator.getCampus().getCount(); i++)
            corpuslist.add(navigator.getCampus().getCorpus(i).getName());

        corpusAdapter = new ArrayAdapter<String>(rootView.getContext(), R.layout.support_simple_spinner_dropdown_item, corpuslist);
        spAudCorpus.setAdapter(corpusAdapter);
        spAudCorpus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sel_stage = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spAudName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boxAdapter = new SearchAuditoryAdapter(rootView.getContext(), findAuditory(sel_stage, spAudName.getText().toString()));
                lvAudResult.setAdapter(boxAdapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return rootView;
    }

    private ArrayList<SearchAuditoryListItemObject> findAuditory(int arg0, String arg1) {
        ArrayList<SearchAuditoryListItemObject> objlist = new ArrayList<SearchAuditoryListItemObject>();

        if (arg1.length() > 0) {
            arg1.toLowerCase();
            ArrayList<naviAuditor> listing = navigator.getCampus().getAuditors();

            for (int i = 0; i < listing.size(); i++) {
                naviAuditor nAud = listing.get(i);
                String name = nAud.getName();
                String auditory = nAud.getAuditor();

                if ((nAud.getCorp() == arg0 + 1) && ((auditory.toLowerCase().indexOf(arg1) > -1) || (name.toLowerCase().indexOf(arg1) > -1))) {

                    objlist.add(new SearchAuditoryListItemObject(nAud.getAuditor(), nAud.getCorp(), nAud.getName(),
                            nAud.getStage(), nAud.getType(), nAud.getUrl()));
                }
            }
        }

        return objlist;
    }
}