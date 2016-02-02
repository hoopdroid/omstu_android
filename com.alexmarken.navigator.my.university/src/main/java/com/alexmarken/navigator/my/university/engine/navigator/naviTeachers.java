package com.alexmarken.navigator.my.university.engine.navigator;

import java.util.ArrayList;

/**
 * Created by Alex Marken on 01.12.2015.
 */

public class naviTeachers {

    private ArrayList<naviPerson> teacherslist = new ArrayList<naviPerson>();

    public void add(String deg, String des, String dir, String name, String pos, String rank) {
        teacherslist.add(new naviPerson(deg, des, dir, name, pos, rank));
    }

    public ArrayList<naviPerson> getTeacherslist() {
        return teacherslist;
    }

    public int getCount() {
        return teacherslist.size();
    }

    public int searchObject(String name) {
        int result = -1;

        for(int i = 0; (i < getCount()) && (result == -1); i++)
            if (teacherslist.get(i).getName() == name)
                result = i;

        return result;
    }
}
