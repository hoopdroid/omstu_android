package com.alexmarken.navigator.my.university.engine.navigator;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.alexmarken.navigator.my.university.util.ExternalDbOpenHelper;

import java.util.ArrayList;

/**
 * Created by Alex Marken on 28.12.2015.
 */

public class naviGraps {
    private Context context;

    // Implements
    public class NaviParam {
        public int CorpA;
        public int CorpB;
        public int PointA;
        public int PointB;
        public int StageA;
        public int StageB;
    }

    public class Graps {
        public ArrayList<Integer> FItems = new ArrayList<Integer>();
        float gLength = 0;

        public int getCount() {
            return FItems.size();
        }

        void add(int PointID) {
            FItems.add(PointID);
        }

        Integer getGrItem(int ind) {
            return FItems.get(ind);
        }
    }

    public class Grapslist {
        ArrayList<Graps> Items = new ArrayList<Graps>();
    }

    public class Point {
        private String auditory;
        private int id;
        private ArrayList<Integer> items = new ArrayList<Integer>();
        private String map;
        private boolean node = false;
        private int stage;
        private int x, y;

        public Point(int id, String map, int stage, String auditory, String list, boolean node, int x, int y) {
            this.id = id;
            this.map = map;
            this.stage = stage;
            this.auditory = auditory;
            this.items = ParseStringToInd(list);
            this.node = node;
            this.x = x;
            this.y = y;
        }

        public String getMap() {
            return map;
        }

        public int getStage() {
            return stage;
        }

        public ArrayList<Integer> getItems() {
            return items;
        }

        public Boolean getNode() {
            return node;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getId() {
            return id;
        }

        public String getAuditory() {
            return auditory;
        }
    }
    // Implements

    static int Count = 10;

    public Graps grapsMin = null;
    public float FLengths[][];
    public ArrayList<Point> Points = new ArrayList<Point>();


    public naviGraps(Context context) {
        this.context = context;

        loadData();
    }

    public void loadData() {
        ExternalDbOpenHelper dbOpenHelper = new ExternalDbOpenHelper(context, "university.db");
        SQLiteDatabase db = dbOpenHelper.openDataBase();

        Cursor c = db.query(dbOpenHelper.grapsHelper.TABLE_NAME, null, "ID", null, null, null, null);

        if (c.moveToFirst())
            do {
                int indId = c.getColumnIndex("ID");
                int indMap = c.getColumnIndex("Map");
                int indAuditor = c.getColumnIndex("Auditor");
                int indGraps = c.getColumnIndex("Graps");
                int indNode = c.getColumnIndex("Node");
                int indX = c.getColumnIndex("X");
                int indY = c.getColumnIndex("Y");

                naviGraps.Point p = new naviGraps.Point(c.getInt(indId), c.getString(indMap), ParseStageString(c.getString(indMap)),
                        c.getString(indAuditor), c.getString(indGraps), Boolean.valueOf(c.getString(indNode)), c.getInt(indX), c.getInt(indY));

                Points.add(p);

            } while(c.moveToNext());

        init();
    }

    public int getIdOnAuditory(String map, String auditory) {
        int ind = -1;

        for(int i = 0; (i < Points.size()) && (ind == -1); i++)
            if ((Points.get(i).getMap().equals(map)) && (Points.get(i).getAuditory().equals(auditory)))
                ind = i;

        return ind;
    }

    private int ParseStageString(String text) {
        String b = String.valueOf(text.charAt(text.length() - 1));
        return Integer.valueOf(b);
    }

    private String Copy(String Text, int s, int e) {
        String buf = "";

        for(int i = s; i < s + e; i++)
            buf += Text.charAt(i);

        return buf;
    }

    public Graps ParseString(String Text) {
        Graps gr = new Graps();

        int sPos = -1, ePos;
        int i, l;
        String buf = "";

        for(i = 0; i < Text.length(); i++) {
            if (Text.charAt(i) == ':') {
                if (sPos == -1)
                    sPos = i;
                else {
                    ePos = i;
                    buf = Copy(Text, sPos + 1, (ePos - sPos) - 1);
                    gr.add(Integer.parseInt(buf));
                    sPos = ePos;
                }
            }
        }

        return gr;
    }

    private ArrayList<Integer> ParseStringToInd(String Text) {
        ArrayList<Integer> gr = new ArrayList<Integer>();

        int sPos = -1, ePos;
        int i, l;
        String buf = "";

        for(i = 0; i < Text.length(); i++) {
            if (Text.charAt(i) == ':') {
                if (sPos == -1)
                    sPos = i;
                else {
                    ePos = i;
                    buf = Copy(Text, sPos + 1, (ePos - sPos) - 1);
                    gr.add(Integer.parseInt(buf));
                    sPos = ePos;
                }
            }
        }


        return gr;
    }

    private int Min(int A, int B) {
        if (A > B)
            return B;
        else
            return A;
    }

    private int Max(int A, int B) {
        if (A > B)
            return A;
        else
            return B;
    }

    private float GetLengthGraf(Graps gp, float[][] lengths) {
        float res = 0;

        for(int i = 0; i <= gp.getCount() - 2; i++) {
            res += lengths[gp.getGrItem(i)][gp.getGrItem(i + 1)];
        }

        return res;
    }


    private float GetLengthGraf(int PointA, int PointB) {
        int X1, X2;
        int Y1, Y2;

        if ((Points.get(PointA).getNode()) && (Points.get(PointB).getNode()))
            return 10;

        X1 = Points.get(PointA).x;
        Y1 = Points.get(PointA).y;
        X2 = Points.get(PointB).x;
        Y2 = Points.get(PointB).y;

        return (float)Math.pow(Math.pow(Max(X1, X2) - Min(X1, X2), 2) + Math.pow(Max(Y1, Y2) - Min(Y1, Y2), 2), 0.5);
    }

    public String GrapsToString(Graps gr) {
        String buf = ":";

        for(int i = 0; i < gr.FItems.size(); i++)
            buf += gr.FItems.get(i) + ":";

        return buf;
    }

    // !
    private void init() {
        int i, j, sid;

        Count = Points.size();
        FLengths = new float[Count][Count];

        for(i = 0; i < Count; i++)
            for(j = 0; j < Count; j++)
                FLengths[i][j] = 0;

        for(i = 0; i < Count; i++) {
            for(j = 0; j < Points.get(i).getItems().size(); j++) {
                sid = Points.get(i).getItems().get(j);

                if (FLengths[i][sid] == 0) {
                    FLengths[i][sid] = GetLengthGraf(i, sid);
                    FLengths[sid][i] = FLengths[i][sid];
                }
            }
        }
    }

    public Graps StartScan(int pFrom, int pTo, int oldp, int p, String Text) {
        grapsMin = null;

        return StartScan(pFrom, pTo, oldp, p, ParseString(Text));
    }

    public Graps StartScan(int pFrom, int pTo, int oldp, int p, Graps gC) {
        int curID;
        Graps grapsCur;

        if (pFrom == pTo)
            return grapsMin;

        if (p == pTo) {
            if (grapsMin == null) {
                grapsMin = Copy(gC);
                grapsMin.gLength = GetLengthGraf(grapsMin, FLengths);
            }
            else if (grapsMin.gLength > GetLengthGraf(gC, FLengths)) {
                grapsMin.FItems.clear();
                grapsMin = Copy(gC);
                grapsMin.gLength = GetLengthGraf(grapsMin, FLengths);
            }

            return grapsMin;
        }

        if (grapsMin != null) {
            grapsCur = Copy(gC);

            if (grapsMin.gLength < GetLengthGraf(grapsCur, FLengths)) {
                grapsCur.FItems.clear();
                grapsCur = null;
                return grapsMin;
            }
        }

        if (p > Count - 1)
            return grapsMin;

        for(int i = 0; i < Points.get(p).getItems().size(); i++) {
            curID = Points.get(p).getItems().get(i);

            if ((p != curID) && (oldp != curID) && (SearchGrapsIndex(curID, gC) == -1) && (curID != pFrom)) {
                Graps l = new Graps();
                l = Copy(gC);
                l.add(curID);

                int st = Points.get(pFrom).getStage() - Points.get(pTo).getStage();

                if (st < 0) {
                    if (Points.get(curID).getStage() >= Points.get(pFrom).getStage())
                        StartScan(pFrom, pTo, p, curID, l);
                }
                else if (st > 0) {
                    if (Points.get(curID).getStage() <= Points.get(pFrom).getStage())
                        StartScan(pFrom, pTo, p, curID, l);
                }
                else if (st == 0)
                    StartScan(pFrom, pTo, p, curID, l);
            }
        }


        return grapsMin;
    }

    private int SearchGrapsIndex(int onIndex, Graps arg) {
        int ind = -1;

        for(int i = 0; (i < arg.getCount()) && (ind == -1); i++)
            if (onIndex == arg.getGrItem(i))
                ind = i;

        return ind;
    }

    private Graps Copy(Graps from) {
        Graps res = new Graps();

        res.gLength = from.gLength;

        for(int i = 0; i < from.getCount(); i++)
            res.add(from.FItems.get(i));

        return res;
    }

    /// Текстовый анализ графа
    public String analise(Graps arg0) {
        ArrayList<Point> p = new ArrayList<Point>();

        for(int i = 0; i < arg0.getCount(); i++)
            p.add(Points.get(arg0.FItems.get(i)));

        return analise(p);
    }

    public String analise(ArrayList<Point> arg0) {
        ArrayList<String> buf = new ArrayList<String>();

        Point active = null;
        int vector = -2;

        for(int i = 1; i < arg0.size(); i++) {
            active = arg0.get(i - 1);

            // действите на одном этаже
            if (!changeStage(active, arg0.get(i))) {
                if (i < arg0.size()) {

                    if (vector == -2) {
                        vector = getVector(active, arg0.get(i));

                        if ((arg0.get(i).getAuditory() != null) && (!arg0.get(i).getNode()))
                            buf.add("Двигайтесь в прямом направлении к аудитории " + arg0.get(i).getAuditory());
                        else
                            buf.add("Двигайтесь в прямом направлении");
                    }
                    else {
                        int new_vector = getVector(active, arg0.get(i));

                        if (new_vector != vector) {
                            if ((arg0.get(i - 1).getAuditory() != null) && (arg0.get(i - 1).getAuditory().length() > 0) && (!arg0.get(i - 1).getNode()))
                                buf.add("У аудитории " + arg0.get(i - 1).getAuditory() + " поверните " + rotation(active, arg0.get(i), new_vector));
                            else
                                buf.add("Поверните " + rotation(active, arg0.get(i), new_vector));
                        }

                        vector = new_vector;
                    }
                }
            }
            // переходим между этажами
            else {
                buf.add(getVectorStage(active, arg0.get(i)));
            }

            if (i == arg0.size() - 1) {
                buf.add("Мы на месте");
            }
        }

        String str = "";

        for(int i = 0; i < buf.size(); i++)
            str += buf.get(i) + "\n";

        return str;
    }

    private boolean rotationRight(Point a, Point b, int vector) {
        int res = 0;

        if (vector == 1)
            res = b.x - a.x;
        else if (vector == 0)
            res = b.y - a.y;

        return (res > 0);
    }

    private String rotation(Point a, Point b, int vector) {
        if (rotationRight(a, b, vector))
            return "направо";
        else
            return "налево";
    }

    private int getVector(Point a, Point b) {
        if (a.x == b.x)
            return 0;
        else if (a.y == b.y)
            return 1;
        else
            return -1;
    }

    private String getVectorStage(Point a, Point b) {
        int res = b.stage - a.stage;

        if (res > 0)
            return "Поднимитесь на " + res + " этаж(ей) выше";
        else
            return "Спуститесь на " + ((-1) * res) + " этаж(ей) ниже";

    }

    private boolean changeStage(Point a, Point b) {
        return a.stage != b.stage;
    }

    private void message(String arg0) {
        Log.d("ALX.NAVI.TEXT", arg0);
    }

    public ArrayList<Point> getSteps(String map) {
        ArrayList<Point> result = new ArrayList<Point>();

        for(int i = 0; i < Points.size(); i++)
            if ((Points.get(i).getNode()) && (Points.get(i).getMap().equals(map))) {
                result.add(Points.get(i));
                Log.d("ALX.STEPS", String.valueOf(i));
            }

        return result;
    }
}
