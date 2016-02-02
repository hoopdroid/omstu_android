package com.alexmarken.navigator.my.university.engine.navigator;

/**
 * Created by Alex Marken on 01.12.2015.
 */
public class naviPerson {
    private String degree;
    private String descip;
    private String directing;
    private String name;
    private String position;
    private String rank;

    public naviPerson(String deg, String des, String dir, String name, String pos, String rank) {
        setDegree(deg);
        setDescip(des);
        setDirecting(dir);
        setName(name);
        setPosition(pos);
        setRank(rank);
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getDescip() {
        return descip;
    }

    public void setDescip(String descip) {
        this.descip = descip;
    }

    public String getDirecting() {
        return directing;
    }

    public void setDirecting(String directing) {
        this.directing = directing;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }
}
