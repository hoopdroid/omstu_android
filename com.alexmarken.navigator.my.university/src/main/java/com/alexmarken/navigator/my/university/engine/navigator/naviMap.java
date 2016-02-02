package com.alexmarken.navigator.my.university.engine.navigator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;

import com.alexmarken.navigator.my.university.NavigatorLibrary;

/**
 * Created by Alex Marken on 05.12.2015.
 */
public class naviMap {
    public String name;
    public int corpus;
    public int stage;
    public Bitmap bitmap = null;

    private AppCompatActivity mainActivity;

    public naviMap(String arg0, int corpus, int stage) {
        mainActivity = NavigatorLibrary.mainActivity;

        String appName = mainActivity.getPackageName();

        name = arg0;
        this.corpus = corpus;
        this.stage = stage;

        int id = mainActivity.getApplication().getResources().getIdentifier(arg0, "drawable", appName);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        bitmap = BitmapFactory.decodeResource(mainActivity.getApplication().getResources(), id, options);
    }
}
