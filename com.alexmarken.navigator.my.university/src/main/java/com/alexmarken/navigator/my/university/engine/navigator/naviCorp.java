package com.alexmarken.navigator.my.university.engine.navigator;

import java.util.ArrayList;

/**
 * Created by Alex Marken on 06.11.2015.
 */
public class naviCorp {
    private static String LOG_TAG = naviCorp.class.getName();

    public ArrayList<naviAuditor> auditors = new ArrayList<naviAuditor>();

    private boolean ground = false;
    private int id = -1;
    private String name;
    private int stages = 0;
    private int x;
    private int y;


    public naviCorp(int id, String name, int stages, int ground, int x, int y) {
        this.id = id;
        this.name = name;
        this.stages = stages;
        this.ground = ground == 1;
        this.x = x;
        this.y = y;
    }


    public ArrayList<naviAuditor> getAuditors() {
        return auditors;
    }

    public ArrayList<naviAuditor> getAuditors(int stage) {
        ArrayList<naviAuditor> result = new ArrayList<naviAuditor>();

        for(int i = 0; i < auditors.size(); i++)
            if (auditors.get(i).getStage() == stage)
                result.add(auditors.get(i));

        return result;
    }

    public int searchAuditory(String arg0) {
        int ind = -1;

        for(int i = 0; (i < auditors.size()) && (ind == -1); i++)
            if (auditors.get(i).getAuditor() != null)
                if (auditors.get(i).getAuditor().equals(arg0))
                    ind = i;

        return ind;
    }

    public String getName() {
        return name;
    }

    public int getStages() {
        return stages;
    }

    public boolean isGround() {
        return ground;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getId() {
        return id;
    }
}
