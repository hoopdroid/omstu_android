package com.alexmarken.navigator.my.university.util.Parser;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Alex Marken on 29.10.2015.
 */

public class URLData {

    public interface TaskEvents {
        void onTaskStart();
        void onTaskFinish(JSONObject obj);
    }

    private String url;
    private ArrayList<HttpParams> params = new ArrayList<HttpParams>();

    public TaskEvents taskEvents;

    public URLData(String uri) {
        url = uri;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void addParam(String name, String value) {
        params.add(new HttpParams(name, value));
    }

    public String getUrl2Params() {
        String uri = url;

        for(int i = 0; i < params.size(); i++) {
            HttpParams httpParams = params.get(i);

            if (i == 0)
                uri += "?";
            else
                uri += "&";

            uri += httpParams.getName() + "=" + httpParams.getValue();
        }

        return uri;
    }
}
