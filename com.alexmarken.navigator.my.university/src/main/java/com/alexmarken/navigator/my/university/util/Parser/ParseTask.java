package com.alexmarken.navigator.my.university.util.Parser;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ParseTask extends AsyncTask<Void, Void, String> {

    HttpURLConnection urlConnection = null;
    BufferedReader reader = null;
    String resultJson = "";
    URLData uriParams;

    public ParseTask(URLData arg0) {
        uriParams = arg0;
    }

    @Override
    protected String doInBackground(Void... params) {
        // получаем данные с внешнего ресурса

        if (uriParams.taskEvents != null) {

        }

        try {
            URL url = new URL(uriParams.getUrl2Params());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }

            resultJson = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultJson;
    }

    @Override
    protected void onPostExecute(String strJson) {
        super.onPostExecute(strJson);

        if (uriParams.taskEvents != null) {
            try {
                uriParams.taskEvents.onTaskFinish(new JSONObject(strJson));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
