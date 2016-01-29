package com.alexmarken.navigator.my.university.util.Boxlists;

public class SearchAuditoryListItemObject {
    private String auditor;
    private int corp;
    private String name;
    private int stage;
    private String type;
    private String url;

    public SearchAuditoryListItemObject(String aud, int corp, String name, int stage, String type, String url) {
        setAuditor(aud);
        setCorp(corp);
        setName(name);
        setStage(stage);
        setType(type);
        setUrl(url);
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public int getCorp() {
        return corp;
    }

    public void setCorp(int corp) {
        this.corp = corp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }
}
