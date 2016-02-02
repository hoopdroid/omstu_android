package com.alexmarken.navigator.my.university.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.R;
import com.alexmarken.navigator.my.university.util.Boxlists.MainAdapter;
import com.alexmarken.navigator.my.university.util.Boxlists.MainListItemObject;

import java.util.ArrayList;

public class NavigatorFragment extends Fragment {
    private static final String FRAGMENT_TITLE = "Навигатор по университету";
    private static ArrayList<MainListItemObject> MainListItems = null;

    private AppCompatActivity mainActivity;
    private ListView lvNaviMain;

    private ArrayAdapter<String> uniAdapter;

    public View rootView;

    public static NavigatorFragment newInstance() {
        return new NavigatorFragment();
    }

    public NavigatorFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_navigator_main, container, false);

        mainActivity = NavigatorLibrary.mainActivity;

        NaviMapsFragment.loadmap(-1, -1, "");

        if (MainListItems == null) {
            MainListItems = new ArrayList<MainListItemObject>();
            MainListItems.add(new MainListItemObject("Карты", "Открыть схему университета", R.drawable.main_ico_maps));
            MainListItems.add(new MainListItemObject("Поиск", "Расширенный поиск", R.drawable.main_ico_search));
            MainListItems.add(new MainListItemObject("Ректорат", "Структура ректората", R.drawable.main_ico_univer));
            MainListItems.add(new MainListItemObject("Сайт ОмГУПС", "Перейти на главный сайт ВУЗ'а", R.drawable.main_ico_site));
            MainListItems.add(new MainListItemObject("Приёмная комиссия", "Перейти на сайт приёмной комиссии", R.drawable.main_ico_site));
        }


        // Настройка lvMain
        MainAdapter boxAdapter = new MainAdapter(rootView.getContext(), MainListItems);
        lvNaviMain = (ListView) rootView.findViewById(R.id.lvNaviMain);
        lvNaviMain.setVerticalScrollBarEnabled(false);
        lvNaviMain.setAdapter(boxAdapter);
        lvNaviMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ((position == 3) || (position == 4)) {
                    String url = "http://www.omgups.ru/";

                    if (position == 4)
                        url = "http://abit.eiuk.ru/";

                    Intent intent1 = new Intent(Intent.ACTION_VIEW);
                    intent1.setData(Uri.parse(url));
                    startActivity(intent1);
                }
                else
                    NavigatorLibrary.naviMain.onNavigationItemSelected(position + 4 + 1);
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