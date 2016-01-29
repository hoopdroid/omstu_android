package com.alexmarken.navigator.my.university.engine.navigator;

/**
 * Created by Alex Marken on 06.11.2015.
 */
public class naviAuditor {
    private String auditor;
    private int corp;
    private String name;
    private int stage;
    private String type;
    private String url;
    private int x;
    private int y;

    public naviAuditor(String auditor, int corp, String name, int stage, String type, String url, int x, int y) {
        this.auditor = auditor;
        this.corp = corp;
        this.name = name;
        this.stage = stage;
        this.type = type;
        this.url = url;
        this.x = x;
        this.y = y;
    }

    public String getAuditor() {
        return auditor;
    }

    public int getCorp() {
        return corp;
    }

    public String getName() {
        return name;
    }

    public int getStage() {
        return stage;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }
}
