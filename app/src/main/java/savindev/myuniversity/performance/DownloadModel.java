package savindev.myuniversity.performance;

import android.net.Uri;

import java.util.ArrayList;

public class DownloadModel {
    private ArrayList<PointModel> points;
    private String ESTIMATION_POINT_NAME;
    private int ESTIMATION_POINT_NUMBER;

    public DownloadModel(ArrayList<PointModel> points, String ESTIMATION_POINT_NAME, int ESTIMATION_POINT_NUMBER) {
        this.points = points;
        this.ESTIMATION_POINT_NAME = ESTIMATION_POINT_NAME;
        this.ESTIMATION_POINT_NUMBER = ESTIMATION_POINT_NUMBER;
    }

    public ArrayList<PointModel> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<PointModel> points) {
        this.points = points;
    }

    public String getESTIMATION_POINT_NAME() {
        return ESTIMATION_POINT_NAME;
    }

    public void setESTIMATION_POINT_NAME(String ESTIMATION_POINT_NAME) {
        this.ESTIMATION_POINT_NAME = ESTIMATION_POINT_NAME;
    }

    public int getESTIMATION_POINT_NUMBER() {
        return ESTIMATION_POINT_NUMBER;
    }

    public void setESTIMATION_POINT_NUMBER(int ESTIMATION_POINT_NUMBER) {
        this.ESTIMATION_POINT_NUMBER = ESTIMATION_POINT_NUMBER;
    }
}

class PointModel {
    private int idGroup;
    private String name;
    private int ID_PROGRESS_RAITNG_FILE;

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    private Uri fileUri;

    public PointModel(int idGroup, String name, int ID_PROGRESS_RAITNG_FILE) {
        this.idGroup = idGroup;
        this.name = name;
        this.ID_PROGRESS_RAITNG_FILE = ID_PROGRESS_RAITNG_FILE;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID_PROGRESS_RAITNG_FILE() {
        return ID_PROGRESS_RAITNG_FILE;
    }

    public void setID_PROGRESS_RAITNG_FILE(int ID_PROGRESS_RAITNG_FILE) {
        this.ID_PROGRESS_RAITNG_FILE = ID_PROGRESS_RAITNG_FILE;
    }


}
