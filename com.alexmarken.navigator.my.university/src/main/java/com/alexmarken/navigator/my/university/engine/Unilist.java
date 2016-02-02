package com.alexmarken.navigator.my.university.engine;

import android.content.Context;

import java.util.ArrayList;

public class Unilist {
    private ArrayList<University> universities = new ArrayList<University>();
    private Context context;

    public Unilist(Context arg0) {
        context = arg0;
    }

    public University add(String name, String fullname, String id) {
        universities.add(new University(context, name, fullname, id));
        return universities.get(universities.size() - 1);
    }

    public ArrayList<University> getUniversities() {
        return universities;
    }

    public ArrayList<String> getUniversitiesNames() {
        ArrayList<String> names = new ArrayList<String>();

        for(int i = 0; i < universities.size(); i++)
            names.add(universities.get(i).getName());

        return names;
    }

    public University getUniversityById(String id) {
        University uni = null;

        for(int i = 0; (i < universities.size()) && (uni == null); i++)
            if (universities.get(i).getId().equals(id))
                uni = universities.get(i);

        return uni;
    }
}
