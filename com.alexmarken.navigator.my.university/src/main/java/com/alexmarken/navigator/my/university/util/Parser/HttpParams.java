package com.alexmarken.navigator.my.university.util.Parser;

public class HttpParams {
    private String name;
    private String value;

    public HttpParams(String arg0, String arg1) {
        name = arg0;
        value = arg1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
