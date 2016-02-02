package com.alexmarken.navigator.my.university;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.alexmarken.navigator.my.university.Fragments.AboutFragment;
import com.alexmarken.navigator.my.university.Fragments.NaviMapsFragment;
import com.alexmarken.navigator.my.university.Fragments.NavigatorFragment;
import com.alexmarken.navigator.my.university.Fragments.SearchNaviObjFragment;
import com.alexmarken.navigator.my.university.engine.Unilist;
import com.alexmarken.navigator.my.university.util.FragmentStore;

/**
 * Created by Alex Marken on 18.01.2016.
 */
public class NavigatorLibrary {
    public static NavigatorLibrary naviMain;
    public static AppCompatActivity mainActivity;
    public static Unilist engine;
    public static android.support.v4.app.FragmentManager frManager;

    public FragmentStore fragmentStore;

    private NavigatorMainEvents events = null;

    public interface NavigatorMainEvents {
        void onSlideFragment(android.support.v4.app.FragmentTransaction transaction, android.support.v4.app.Fragment fragment);
        void postOnAttach(String title);
    }


    public NavigatorLibrary(Context context, AppCompatActivity activity, NavigatorMainEvents ev) {
        naviMain = this;

        mainActivity = activity;
        events = ev;
        frManager = mainActivity.getSupportFragmentManager();

        if (engine == null) {
            engine = new Unilist(context);
            engine.add("ОмГУПС", "Омский Государственны Университет Путей Сообщений", "omgups");
        }

        onNavigationItemSelected(-1);
    }

    public boolean onNavigationItemSelected(int position) {
        android.support.v4.app.Fragment fragment = null;
        android.support.v4.app.FragmentManager fragmentManager = mainActivity.getSupportFragmentManager();

        if (position == -1) {
            //fragment = AuthorizationFragment.newInstance();
        }
        else if (position == 0) {
            //fragment = MainFragment.newInstance(0);
        }
        else if (position == 1) {

        }
        else if (position == 2) {
            fragmentStore = new FragmentStore();
            fragment = NavigatorFragment.newInstance();
        }
        else if (position == 4) {
            fragmentStore = new FragmentStore();
            fragment = AboutFragment.newInstance();
        }
        else if (position == 5) {
            fragment = NaviMapsFragment.newInstance();
        }
        else if (position == 6) {
            fragment = SearchNaviObjFragment.newInstance();
        }

        if (fragment != null) {

            Boolean slideResult;

            if (position == -1)
                slideResult = true;
            else
                slideResult = fragmentStore.slideFragment(position);

            if (slideResult != null){
                android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

                if (slideResult)
                    transaction.setCustomAnimations(R.anim.slide_next_in, R.anim.slide_next_out);
                else
                    transaction.setCustomAnimations(R.anim.slide_prev_in, R.anim.slide_prev_out);

                if (position != -1)
                    fragmentStore.addFragment(fragment, false);

                if (events != null)
                    events.onSlideFragment(transaction, fragment);
            }
        }

        return true;
    }

    public void onBackPressed() {
        android.support.v4.app.Fragment fragment = fragmentStore.getPreviousFragment();

        if (fragment == null)
            mainActivity.onBackPressed();
        else {
            fragmentStore.addFragment(fragment, true);

            android.support.v4.app.FragmentTransaction transaction = mainActivity.getSupportFragmentManager().beginTransaction();
            transaction.setTransition(android.support.v4.app.FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            transaction.setCustomAnimations(R.anim.slide_prev_in, R.anim.slide_prev_out);

            if (events != null)
                events.onSlideFragment(transaction, fragment);
        }
    }

    public void postOnAttach(String title) {
        if (events != null)
            events.postOnAttach(title);
    }
}