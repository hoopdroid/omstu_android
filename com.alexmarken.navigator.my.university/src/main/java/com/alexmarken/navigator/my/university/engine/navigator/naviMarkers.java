package com.alexmarken.navigator.my.university.engine.navigator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.R;

import java.util.ArrayList;

/**
 * Created by Alex Marken on 09.11.2015.
 */
public class naviMarkers {

    public static final int ID_MARKER_AUDITOR = 0;
    public static final int ID_MARKER_AUDITOR_LAB = 1;
    public static final int ID_MARKER_AUDITOR_LECTURE = 2;
    public static final int ID_MARKER_AUDITOR_TEACHIND = 3;
    public static final int ID_MARKER_AUDITOR_BIBL = 4;
    public static final int ID_MARKER_AUDITOR_DRESSINGROOM = 5;
    public static final int ID_MARKER_AUDITOR_SERVICEROOM = 6;
    public static final int ID_MARKER_CAMPUS = 7;
    public static final int ID_MARKER_STEPS = 8;
    public static final int ID_MARKER_POINT_A = 9;
    public static final int ID_MARKER_POINT_B = 10;

    private AppCompatActivity mainActivity;

    public class mapMarker {
        private int id;
        private int resId;
        private Bitmap image;
        private int originalHeight = 0;
        private int originalWidth = 0;

        public mapMarker(int id, int resId) {
            this.id = id;
            this.resId = resId;

            loadImage();
        }

        public int getId() {
            return id;
        }

        public Bitmap getImage() {
            return image;
        }

        public void imageResize(float h, float w) {
            loadImage();

            int width = originalWidth;
            int height = originalHeight;

            float scaleWidth = w / width;
            float scaleHeight = h / height;

            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);

            if (image != null) {
                Bitmap newImage = Bitmap.createBitmap(image, 0, 0, width, height, matrix, true);
                image = newImage;
            }
        }

        private void loadImage() {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

            image = BitmapFactory.decodeResource(mainActivity.getApplication().getResources(), resId, options);

            if (image != null) {
                originalHeight = image.getHeight();
                originalWidth = image.getWidth();
            }
        }

        public int getOriginalHeight() {
            return originalHeight;
        }

        public int getOriginalWidth() {
            return originalWidth;
        }
    }

    private ArrayList<mapMarker> markers = new ArrayList<mapMarker>();

    public naviMarkers() {
        mainActivity = NavigatorLibrary.mainActivity;

        markers.add(new mapMarker(ID_MARKER_AUDITOR, R.drawable.icon_aud));
        markers.add(new mapMarker(ID_MARKER_AUDITOR_LAB, R.drawable.icon_aud_lab));
        markers.add(new mapMarker(ID_MARKER_AUDITOR_LECTURE, R.drawable.icon_aud_lecture));
        markers.add(new mapMarker(ID_MARKER_AUDITOR_TEACHIND, R.drawable.icon_aud_teaching));
        markers.add(new mapMarker(ID_MARKER_AUDITOR_BIBL, R.drawable.icon_aud_bibl));
        markers.add(new mapMarker(ID_MARKER_AUDITOR_DRESSINGROOM, R.drawable.icon_aud_garderob));
        markers.add(new mapMarker(ID_MARKER_AUDITOR_SERVICEROOM, R.drawable.icon_aud_service));
        markers.add(new mapMarker(ID_MARKER_CAMPUS, R.drawable.icon_corpus));
        markers.add(new mapMarker(ID_MARKER_STEPS, R.drawable.icon_steps));
        markers.add(new mapMarker(ID_MARKER_POINT_A, R.drawable.navi_point_a));
        markers.add(new mapMarker(ID_MARKER_POINT_B, R.drawable.navi_point_b));
    }

    public void markersRefresh(float h, float w) {
        if (((h != 30) && (w != 30)) && (w < 90))
            for(int i = 0; i < markers.size(); i++)
                if ((markers.get(i).getImage().getHeight() != h) && (markers.get(i).getImage().getWidth() != w))
                    markers.get(i).imageResize(h, w);
    }

    public Bitmap getMarker(int id) {

        if ((id < 0) || (id > 10))
            id = 0;

        Bitmap img = null;

        for(int i = 0; (i < markers.size()) && (img == null); i++)
            if (markers.get(i).getId() == id)
                img = markers.get(i).getImage();

        return img;
    }

}
