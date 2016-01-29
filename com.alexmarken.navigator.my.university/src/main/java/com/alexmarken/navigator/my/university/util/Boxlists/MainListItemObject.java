package com.alexmarken.navigator.my.university.util.Boxlists;

public class MainListItemObject {
    private String Desr;
    private String Name;
    private int imgId;

    public MainListItemObject(String name, String descr, int imgid) {
        setName(name);
        setDesr(descr);
        setImgId(imgid);
    }

    public String getDesr() {
        return Desr;
    }

    public void setDesr(String desr) {
        Desr = desr;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }
}
