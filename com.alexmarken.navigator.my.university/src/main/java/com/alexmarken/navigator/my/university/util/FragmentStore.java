package com.alexmarken.navigator.my.university.util;


import android.support.v4.app.Fragment;

import java.util.ArrayList;

public class FragmentStore {
    private ArrayList<Integer> fragmentlist = new ArrayList<Integer>();
    private Integer activeFragment = null;
    private Integer previousFragment = null;

    public ArrayList<Fragment> fragments = new ArrayList<Fragment>();

    public FragmentStore() {

    }

    public Boolean slideFragment(int arg0) {
        // true - переход к новому
        // false - возврат

        if ((activeFragment != null) && (activeFragment == arg0)) {
            return null;
        }
        else if (activeFragment == null) {
            activeFragment = arg0;
            fragmentlist.add(activeFragment);

            return true;
        }
        else if (previousFragment == null) {
            previousFragment = activeFragment;
            activeFragment = arg0;
            fragmentlist.add(activeFragment);

            return true;
        }
        else {
            if (arg0 == previousFragment) {
                fragmentlist.remove(activeFragment);
                activeFragment = previousFragment;

                if (fragmentlist.size() > 1)
                    previousFragment = fragmentlist.get(fragmentlist.size() - 2);
                else
                    previousFragment = null;

                return false;
            }

            return true;
        }
    }

    public void addFragment(Fragment arg0, boolean restore) {
        if ((restore) && (fragments.size() > 1)) {
            fragments.remove(getActiveFragment());

            fragmentlist.remove(activeFragment);
            activeFragment = previousFragment;

            if (fragmentlist.size() > 1)
                previousFragment = fragmentlist.get(fragmentlist.size() - 2);
            else
                previousFragment = null;
        }
        else
            fragments.add(arg0);
    }

    public Fragment getActiveFragment() {
        return (fragments.size() > 0) ? fragments.get(fragments.size() - 1) : null;
    }

    public Fragment getPreviousFragment() {
        return (fragments.size() > 1) ? fragments.get(fragments.size() - 2) : null;
    }
}
