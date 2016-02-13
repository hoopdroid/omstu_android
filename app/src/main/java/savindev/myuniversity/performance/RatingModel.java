package savindev.myuniversity.performance;

import java.util.ArrayList;

public class RatingModel {


    private ArrayList<PointModel> points;
    private String ESTIMATION_POINT_NAME;
    private int ESTIMATION_POINT_NUMBER;

    public RatingModel(ArrayList<PointModel> points, String ESTIMATION_POINT_NAME, int ESTIMATION_POINT_NUMBER) {
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

