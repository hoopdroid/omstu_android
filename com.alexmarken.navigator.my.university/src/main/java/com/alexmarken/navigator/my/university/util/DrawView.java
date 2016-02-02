package com.alexmarken.navigator.my.university.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.R;
import com.alexmarken.navigator.my.university.engine.Navigator;
import com.alexmarken.navigator.my.university.engine.navigator.naviAuditor;
import com.alexmarken.navigator.my.university.engine.navigator.naviCampus;
import com.alexmarken.navigator.my.university.engine.navigator.naviCorp;
import com.alexmarken.navigator.my.university.engine.navigator.naviGraps;
import com.alexmarken.navigator.my.university.engine.navigator.naviMap;
import com.alexmarken.navigator.my.university.engine.navigator.naviMarkers;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class DrawView extends View {
    public static float ScreenWidth, ScreenHeight;

    public boolean isSearch = false;
    public boolean isCampus = true;
    public boolean isAuditor = false;
    public boolean isWC = false;
    public boolean isSteps = false;

    public int xCoor, yCoor;
    public int xMouseCoor, yMouseCoor;

    private float MarkerWidth = 0;
    private float MarkerHeight = 0;

    private boolean zoomAnimation = false;

    private naviMarkers markers;
    private Bitmap image = null;
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector = null;
    private Scroller scroller = null;
    private Paint p = null;

    public static float scaleFactor = 1;
    public static float adaptiveScale = scaleFactor;
    public static onMapsEvents onMapsEvent = null;

    private int corp = -1;
    private int stage = -1;
    public String activeAuditory;
    public naviGraps.Graps points = null;
    public ArrayList<naviGraps.Point> steps = null;
    public String navi_descr = "";


    private Navigator navigator;

    public interface onMapsEvents {
        void onChangeFiter(int typeFilter, boolean flag);
        void onChangeMap(boolean isCampus, int corp, int stage);
        void onClickAuditor(int corp, int stage, naviAuditor data);
        void onClickLine(int corp, int stage, String text);
    }

    public DrawView(Context context, View parent, String appName) {
        super(context);

        navigator = NavigatorLibrary.engine.getUniversityById("omgups").getNavigator();

        setHorizontalScrollBarEnabled(true);
        setVerticalScrollBarEnabled(true);

        markers = new naviMarkers();

        gestureDetector = new GestureDetector(context, new MyGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(context, new MyScaleGestureListener());
        scroller = new Scroller(context);

        final TypedArray a = (TypedArray) context.obtainStyledAttributes(R.styleable.View);
        a.recycle();

        CheckDisplay();

        p = new Paint();

        loadImage(corp, stage);

        AdaptToScreen(false, ScreenWidth, ScreenHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(0, 0, 0, 0);

        markers.markersRefresh(30, 30);
        Rect rect = new Rect(0, 0, getScaledWidth(), getScaledHeight());
        canvas.drawBitmap(image, null, rect, p);

        p.setStyle(Paint.Style.FILL);
        p.setAntiAlias(true);
        //p.setColor(Color.RED);

        float minWidth = 0;

        if (isCampus)
            minWidth = (float) (getWidth() * 0.065);
        else
            minWidth = (float) (getWidth() * 0.059);
        float kWidth = (int) (minWidth * (scaleFactor));

        if (kWidth < minWidth)
            kWidth = (int) minWidth;

        MarkerWidth = kWidth;
        MarkerHeight = kWidth;

        markers.markersRefresh(MarkerWidth, MarkerHeight);

        naviCampus nCampus = navigator.getCampus();

        if (isCampus) {
            for(int i = 1; i <= nCampus.getCount(); i++) {
                naviCorp corp = nCampus.getCorpus(i);

                if (corp != null) {

                    Bitmap marker = markers.getMarker(naviMarkers.ID_MARKER_CAMPUS);

                    if (marker != null)
                        canvas.drawBitmap(marker, corp.getX() * scaleFactor - marker.getWidth() / 2,
                                corp.getY() * scaleFactor - marker.getHeight() / 2, p);
                }
            }
        }
        else {
            ArrayList<naviAuditor> auditorlist = nCampus.getCorpus(corp).getAuditors(stage);

            // Рисуем точки аудиторий
            if ((isAuditor) && (scaleFactor >= 0.7)) {
                for (int i = 0; i < auditorlist.size(); i++) {
                    naviAuditor auditor = auditorlist.get(i);

                    Bitmap marker = markers.getMarker(naviMarkers.ID_MARKER_AUDITOR);

                    if (marker != null)
                        canvas.drawBitmap(marker, auditor.getX() * scaleFactor - marker.getWidth() / 2,
                                auditor.getY() * scaleFactor - marker.getHeight() / 2, p);

                    //canvas.drawCircle(auditor.getX() * scaleFactor, auditor.getY() * scaleFactor, 20, p);
                }
            }

            // Рисуем лестницы
            if ((isSteps) && (steps != null)) {

                for (int i = 0; i < steps.size(); i++) {
                    Bitmap marker = markers.getMarker(naviMarkers.ID_MARKER_STEPS);

                    if (marker != null)
                        canvas.drawBitmap(marker, steps.get(i).getX() * scaleFactor - marker.getWidth() / 2,
                                steps.get(i).getY() * scaleFactor - marker.getHeight() / 2, p);
                }
            }

            // Отображаем маршрут
            if (points != null) {
                p.setStrokeWidth(9 * getResources().getDisplayMetrics().density * scaleFactor);
                p.setColor(Color.RED);
                p.setStrokeMiter(0);

                naviGraps gr = navigator.getGraps();

                Point pBeginNavi = null, pEndNavi = null;
                ArrayList<Point> pGraphicsSteps = new ArrayList<Point>(); // begin-..STEPS..-end

                Path path = new Path();
                boolean first_point = false;

                for(int i = 0; i < points.FItems.size() - 1; i++) {
                    int bPoint = points.FItems.get(i);
                    int ePoint = points.FItems.get(i + 1);


                    if ((gr.Points.get(bPoint).getStage() == stage) &&
                            (gr.Points.get(bPoint).getStage() == gr.Points.get(ePoint).getStage())) {
                        float X1 = gr.Points.get(bPoint).getX() * scaleFactor;
                        float Y1 = gr.Points.get(bPoint).getY() * scaleFactor;

                        float X2 = gr.Points.get(ePoint).getX() * scaleFactor;
                        float Y2 = gr.Points.get(ePoint).getY() * scaleFactor;

                        if (!first_point) {
                            path.moveTo(X1, Y1);
                            first_point = true;
                        }

                        path.lineTo(X2, Y2);

                        if (i == 0)
                            pBeginNavi = new Point((int)X1, (int)Y1);
                        else if (i ==  points.FItems.size() - 2)
                            pEndNavi = new Point((int)X2, (int)Y2);
                    }
                }

                p.setStyle(Paint.Style.STROKE);
                canvas.drawPath(path, p);

                if (pBeginNavi != null) {
                    Bitmap marker = markers.getMarker(naviMarkers.ID_MARKER_POINT_A);

                    if (marker != null)
                        canvas.drawBitmap(marker, pBeginNavi.x - marker.getWidth() / 2, pBeginNavi.y - marker.getHeight() / 2, p);
                }

                if (pEndNavi != null) {
                    Bitmap marker = markers.getMarker(naviMarkers.ID_MARKER_POINT_B);

                    if (marker != null)
                        canvas.drawBitmap(marker, pEndNavi.x - marker.getWidth() / 2, pEndNavi.y - marker.getHeight() / 2, p);
                }
            }

            if ((isSearch) && (activeAuditory != null)) {
                int findInd = -1;

                for (int i = 0; (i < auditorlist.size()) && (findInd == -1); i++)
                    if (activeAuditory == auditorlist.get(i).getAuditor())
                        findInd = i;

                if (findInd != -1) {
                    naviAuditor auditor = auditorlist.get(findInd);

                    Bitmap marker = markers.getMarker(naviMarkers.ID_MARKER_AUDITOR);

                    if (marker != null)
                        canvas.drawBitmap(marker, auditor.getX() * scaleFactor - marker.getWidth() / 2,
                                auditor.getY() * scaleFactor - marker.getHeight() / 2, p);
                }
            }
        }
    }

    public int getScaledWidth()
    {
        return (int)(image.getWidth() * scaleFactor);
    }

    public int getScaledHeight()
    {
        return (int)(image.getHeight() * scaleFactor);
    }

    public int getNonScaledWidth()
    {
        return (int)image.getWidth();
    }

    public int getNonScaledHeight()
    {
        return (int)image.getHeight();
    }

    private void CheckDisplay() {
        Display display = NavigatorLibrary.mainActivity.getWindowManager().getDefaultDisplay();
        DisplayMetrics metricsB = new DisplayMetrics();
        display.getMetrics(metricsB);

        ScreenWidth = metricsB.widthPixels;
        ScreenWidth *= 0.85;

        ScreenHeight = metricsB.heightPixels;
        ScreenHeight *= 0.7;
    }

    public static Bitmap resize(Bitmap bit, float newWidth, float newHeight) {

        int width = bit.getWidth();
        int height = bit.getHeight();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bit, 0, 0, width, height, matrix, true);
    }

    public void loadImage(int corp, int stage) {
        naviMap map = navigator.getCampus().getMap(corp, stage);

        if (map != null) {
            this.corp = map.corpus;
            this.stage = map.stage;

            image = map.bitmap;

            if (image == null) {
                throw new NullPointerException("The image can't be decoded.");
            }

            if (corp != -1)
                scaleFactor = (float) 0.4;
            else
                scaleFactor = adaptiveScale;

            int width = getWidth();
            int height = getHeight();

            if ((width != 0) || (height != 0))
            {
                int scrollX = (getScaledWidth() < width ? - (width - getScaledWidth()) / 2 : getScaledWidth() / 2);
                int scrollY = (getScaledHeight() < height ? - (height - getScaledHeight()) / 2 : getScaledHeight() / 2);
                scrollTo(scrollX, scrollY);

                computeScroll();
            }

            Update();

            if (onMapsEvent != null)
                onMapsEvent.onChangeMap(this.corp == -1, this.corp, this.stage);

            // search mode
            if ((isSearch) && (onMapsEvent != null)) {
                int findInd = -1;

                naviCampus nCampus = navigator.getCampus();
                ArrayList<naviAuditor> auditorlist = nCampus.getCorpus(corp).getAuditors(stage);

                for (int i = 0; (i < auditorlist.size()) && (findInd == -1); i++)
                    if (activeAuditory == auditorlist.get(i).getAuditor()) {
                        findInd = i;

                        onMapsEvent.onClickAuditor(corp, stage, auditorlist.get(findInd));
                    }
            }

        }
    }

    public void loadImage(int resID)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;

        image = BitmapFactory.decodeResource(getResources(), resID, options);

        if (image == null) throw new NullPointerException("The image can't be decoded.");

        scaleFactor = 1;

        int width = getWidth();
        int height = getHeight();

        if ((width != 0) || (height != 0))
        {
            int scrollX = (getScaledWidth() < width ? - (width - getScaledWidth()) / 2 : getScaledWidth() / 2);
            int scrollY = (getScaledHeight() < height ? -(height - getScaledHeight()) / 2 : getScaledHeight() / 2);
            scrollTo(scrollX, scrollY);

            computeScroll();
        }

        Update();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            if (!scroller.isFinished()) scroller.abortAnimation();
        }

        scaleGestureDetector.onTouchEvent(event);

        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }

        // check for pointer release
        if ((event.getPointerCount() == 1) && ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP))
        {
            int newScrollX = getScrollX();
            if (getScaledWidth() < getWidth()) newScrollX = -(getWidth() - getScaledWidth()) / 2;
            else if (getScrollX() < 0) newScrollX = 0;
            else if (getScrollX() > getScaledWidth() - getWidth()) newScrollX = getScaledWidth() - getWidth();

            int newScrollY = getScrollY();
            if (getScaledHeight() < getHeight()) newScrollY = -(getHeight() - getScaledHeight()) / 2;
            else if (getScrollY() < 0) newScrollY = 0;
            else if (getScrollY() > getScaledHeight() - getHeight()) newScrollY = getScaledHeight() - getHeight();

            if ((newScrollX != getScrollX()) || (newScrollY != getScrollY()))
            {
                scroller.startScroll(getScrollX(), getScrollY(), newScrollX - getScrollX(), newScrollY - getScrollY());
                awakenScrollBars(scroller.getDuration());
                computeScroll();
                return true;
            }
        }

        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            return true;
        }

        // нажатие
        if ((event.getPointerCount() == 1) && (event.getAction() == MotionEvent.ACTION_UP) && (event.getAction() != MotionEvent.ACTION_MOVE)) {
            clickOnMarker(getScrollX() + (int) event.getX(), getScrollY() + (int) event.getY(), event);

            return true;
        }

        return true;
    }

    private boolean joinInPoint(float c, float ps, float pe) {
        return (c >= ps) && (c <= pe);
    }

    private int findAuditor(float x, float y) {
        int ind = -1;
        naviCampus nCampus = navigator.getCampus();
        ArrayList<naviAuditor> auds = nCampus.getCorpus(corp).getAuditors(stage);

        for(int i = 0; (i < auds.size()) && (ind == -1); i++) {
            float pointX = auds.get(i).getX() * scaleFactor;
            float pointY = auds.get(i).getY() * scaleFactor;
            float delH = MarkerHeight / 2;
            float delW = MarkerWidth / 2;

            if ((joinInPoint(x, pointX - delW, pointX + delW)) && (joinInPoint(y, pointY - delH, pointY + delH)))
                ind = i;
        }

        return ind;
    }

    private int findPointGraps(float x, float y) {
        int ind = -1;
        naviGraps gr = navigator.getGraps();

        if (points != null)
            for(int i = 0; (i < points.FItems.size()) && (ind == -1); i++) {
                float pointX = gr.Points.get(points.FItems.get(i)).getX() * scaleFactor;
                float pointY = gr.Points.get(points.FItems.get(i)).getY() * scaleFactor;
                float delH = MarkerHeight / 2;
                float delW = MarkerWidth / 2;

                if ((joinInPoint(x, pointX - delW, pointX + delW)) && (joinInPoint(y, pointY - delH, pointY + delH)))
                    ind = i;
            }

        return ind;
    }

    private int findCoprus(float x, float y) {
        int ind = -1;
        naviCampus nCampus = navigator.getCampus();

        for(int i = 1; (i <= nCampus.getCount()) && (ind == -1); i++) {
            float pointX = nCampus.getCorpus(i).getX() * scaleFactor;
            float pointY = nCampus.getCorpus(i).getY() * scaleFactor;
            float delH = MarkerHeight / 2;
            float delW = MarkerWidth / 2;

            if ((joinInPoint(x, pointX - delW, pointX + delW)) && (joinInPoint(y, pointY - delH, pointY + delH)))
                ind = i;
        }

        return ind;
    }

    private boolean clickOnMarker(float x, float y, MotionEvent e) {
        if ((isCampus) && (e.getPointerCount() == 1) && (e.getAction() == MotionEvent.ACTION_UP) && (e.getAction() != MotionEvent.ACTION_MOVE)) {
            int a_id = findCoprus(x, y);

            if (a_id != -1) {
                naviCampus nCampus = navigator.getCampus();
                final int newIndex = nCampus.getCorpus(a_id).getId();
                int newStage = 1;

                if (nCampus.getCorpus(a_id).isGround())
                    newStage--;

                final int finalNewStage = newStage;
                Snackbar.make(this, nCampus.getCorpus(a_id).getName(), Snackbar.LENGTH_LONG).setAction("Открыть", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadImage(newIndex, finalNewStage);
                    }
                }).show();

            }

            return true;
        }
        else if ((e.getPointerCount() == 1) && (e.getAction() == MotionEvent.ACTION_UP) && (e.getAction() != MotionEvent.ACTION_MOVE)) {
            int a_id = findAuditor(x, y);
            if (a_id != -1) {
                naviCampus nCampus = navigator.getCampus();
                naviAuditor aud = nCampus.getCorpus(corp).getAuditors(stage).get(a_id);

                if ((isAuditor) && (scaleFactor >= 0.7)) {
                    if (onMapsEvent != null)
                        onMapsEvent.onClickAuditor(corp, stage, aud);
                } else if (isSearch) {
                    if (aud.getAuditor() == activeAuditory)
                        if (onMapsEvent != null)
                            onMapsEvent.onClickAuditor(corp, stage, aud);
                }
            }
            else if (points != null) {
                a_id = findPointGraps(x, y);

                if (a_id != -1) {
                    if (onMapsEvent != null)
                        onMapsEvent.onClickLine(corp, stage, navi_descr);

                }
            }

            return true;
        }

        return true;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY)
    {
        //
    }

    @Override
    public void computeScroll()
    {
        if (scroller.computeScrollOffset())
        {
            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            scrollTo(x, y);
            if (oldX != getScrollX() || oldY != getScrollY())
            {
                onScrollChanged(getScrollX(), getScrollY(), oldX, oldY);
            }

            postInvalidate();
        }
    }

    @Override
    protected int computeHorizontalScrollRange()
    {
        return getScaledWidth();
    }

    @Override
    protected int computeVerticalScrollRange()
    {
        return getScaledHeight();
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight)
    {
        int scrollX = (getScaledWidth() < width ? -(width - getScaledWidth()) / 2 : getScaledWidth() / 2);
        int scrollY = (getScaledHeight() < height ? -(height - getScaledHeight()) / 2 : getScaledHeight() / 2);
        scrollTo(scrollX, scrollY);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            boolean scrollBeyondImage = ((getScrollX() < 0) || (getScrollX() > image.getWidth()) || (getScrollY() < 0) || (getScrollY() > image.getHeight()));
            if (scrollBeyondImage) return false;

            scroller.fling(getScrollX(), getScrollY(), -(int)velocityX, -(int)velocityY, 0, image.getWidth() - getWidth(), 0, image.getHeight() - getHeight());
            awakenScrollBars(scroller.getDuration());

            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            scrollBy((int) distanceX, (int) distanceY);
            return true;
        }
    }

    private class MyScaleGestureListener implements ScaleGestureDetector.OnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector detector)
        {
            scaleFactor *= detector.getScaleFactor();

            if ((scaleFactor > 2) || (scaleFactor < 0.4)) {
                scaleFactor /= detector.getScaleFactor();
                return true;
            }

            int focusX = (int)detector.getFocusX();
            int focusY = (int)detector.getFocusY();

            if (focusX > getScaledWidth())
                focusX = getScaledWidth();

            if (focusY > getScaledHeight())
                focusY = getScaledHeight();


            int newScrollX = (int)((getScrollX() + focusX) * detector.getScaleFactor() - focusX);
            int newScrollY = (int)((getScrollY() + focusY) * detector.getScaleFactor() - focusY);
            scrollTo(newScrollX, newScrollY);
            invalidate();

            return true;
        }

        public boolean onScaleBegin(ScaleGestureDetector detector)
        {
            return true;
        }

        public void onScaleEnd(ScaleGestureDetector detector)
        {
        }
    }

    public void changeScaleFactor(float arg0) {
        applyScaleFactor(arg0);
    }

    public void Update() {
        invalidate();
    }

    public void applyScaleFactor(final float arg0) {
        if (!zoomAnimation) {
            zoomAnimation = true;

            final DrawView dView = this;
            dView.scaleFactor = (float)((int)(dView.scaleFactor * 1000)) / 1000;

            final float d = (float) (arg0 < 0 ? -0.01 : 0.01);
            float sum = dView.scaleFactor + arg0;

            if (sum > 2)
                sum = (float) 2;
            else if (sum < 0.4)
                sum = (float) 0.4;

            final float finalValue = ((float)((int)(sum * 1000))) / 1000;

            new Timer().schedule(new TimerTask() {

                @Override
                public void run() {
                    if ((int)(finalValue * 100) == (int)(dView.scaleFactor * 100)) {
                        this.cancel();
                        zoomAnimation = false;
                    }
                    else {
                        dView.scaleFactor = dView.scaleFactor + d;

                        post(new Runnable() {
                            @Override
                            public void run() {
                                Update();

                                computeScroll();
                            }
                        });
                    }
                }
            }, 5, 10);
        }
    }

    public void AdaptToScreen(boolean onlySetScale, float displayWidth, float displayHeight){
        final float adaptingScaleWidth = displayWidth / (float)this.getNonScaledWidth();
        final float adaptingScaleHeight = displayHeight / (float)this.getNonScaledHeight();

        if(adaptingScaleHeight < adaptingScaleWidth) {
            adaptiveScale = adaptingScaleHeight;
            scaleFactor = adaptiveScale;
        }
        else {
            adaptiveScale = adaptingScaleWidth;
            scaleFactor = adaptiveScale;
        }

    }

    public void setFilterAuditory(boolean arg0) {
        isAuditor = arg0;

        if (onMapsEvent != null)
            onMapsEvent.onChangeFiter(0, isAuditor);

        Update();
    }

    public void setFilterSteps(boolean arg0) {
        isSteps = arg0;

        if (onMapsEvent != null)
            onMapsEvent.onChangeFiter(1, isSteps);

        Update();
    }

    public void setFilterWC(boolean arg0) {
        isWC = arg0;

        if (onMapsEvent != null)
            onMapsEvent.onChangeFiter(2, isWC);

        Update();
    }
}