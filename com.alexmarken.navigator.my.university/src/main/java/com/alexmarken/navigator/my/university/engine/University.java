package com.alexmarken.navigator.my.university.engine;

import android.content.Context;

public class University {

    private Context context;
    private String Name;
    private String FullName;
    private String id;
    private Navigator navigator;

    public University(Context arg0, String name, String fullname, String id) {
        context = arg0;

        setName(name);
        setFullName(fullname);

        this.id = id;

        navigator = new Navigator(context);
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public String getId() {
        return id;
    }
}
