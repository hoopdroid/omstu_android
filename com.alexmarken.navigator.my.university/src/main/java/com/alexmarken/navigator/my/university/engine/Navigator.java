package com.alexmarken.navigator.my.university.engine;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexmarken.navigator.my.university.engine.navigator.naviAuditor;
import com.alexmarken.navigator.my.university.engine.navigator.naviCampus;
import com.alexmarken.navigator.my.university.engine.navigator.naviCorp;
import com.alexmarken.navigator.my.university.engine.navigator.naviGraps;
import com.alexmarken.navigator.my.university.engine.navigator.naviMap;
import com.alexmarken.navigator.my.university.engine.navigator.naviTeachers;
import com.alexmarken.navigator.my.university.util.ExternalDbOpenHelper;

public class Navigator {
    private naviCampus campus;
    private naviGraps graps;
    private naviTeachers teachers;
    private Context context;

    public ExternalDbOpenHelper dbOpenHelper;
    public SQLiteDatabase db;

    public Navigator(Context arg0) {
        context = arg0;

        dbOpenHelper = new ExternalDbOpenHelper(context, "university.db");
        db = dbOpenHelper.openDataBase();

        campus = new naviCampus();
        graps = new naviGraps(context);
        teachers = new naviTeachers();

        loadDataMaps();
        loadDataCampus();
        loadDataTeachers();

        dbOpenHelper.close();
    }

    public naviCampus getCampus() {
        return campus;
    }

    // <-- ******* PRIVATE ******* --> //


    private void loadDataMaps() {
        Cursor c = db.query(dbOpenHelper.mapsHelper.TABLE_NAME, null, "", null, null, null, null);

        naviMap map = new naviMap("campus", -1, -1);
        campus.maps.add(map);

        if (c.moveToFirst())
            do {
                int indId = c.getColumnIndex("ID");
                int indName = c.getColumnIndex("Name");
                int indCorpus = c.getColumnIndex("Corpus");
                int indStage = c.getColumnIndex("Stage");

                map = new naviMap(c.getString(indName), c.getInt(indCorpus), c.getInt(indStage));

                campus.maps.add(map);
            } while(c.moveToNext());
    }

    private void loadDataCampus() {
        Cursor c = db.query(dbOpenHelper.campusHelper.TABLE_NAME, null, "", null, null, null, null);

        if (c.moveToFirst())
            do {
                int indId = c.getColumnIndex("id");
                int indName = c.getColumnIndex("CorpusName");
                int indStages = c.getColumnIndex("StageCount");
                int indGround = c.getColumnIndex("isGround");
                int indX = c.getColumnIndex("X");
                int indY = c.getColumnIndex("Y");

                naviCorp corp = new naviCorp(c.getInt(indId), c.getString(indName), c.getInt(indStages),
                        c.getInt(indGround), c.getInt(indX), c.getInt(indY));

                loadDataAuditor(corp);

                campus.corplist.add(corp);
            } while(c.moveToNext());
    }

    private void loadDataAuditor(naviCorp arg0) {
        String QUERY = "Corpus = '" + arg0.getId() + "'";
        Cursor c = db.query(dbOpenHelper.auditorHelper.TABLE_NAME, null, QUERY, null, null, null, null);

        if (c.moveToFirst())
            do {
                int indAuditor = c.getColumnIndex("Auditor");
                int indCorpus = c.getColumnIndex("Corpus");
                int indName = c.getColumnIndex("NameAuditor");
                int indStage = c.getColumnIndex("Stage");
                int indType = c.getColumnIndex("TypeAuditor");
                int indUrl = c.getColumnIndex("url");
                int indX = c.getColumnIndex("X");
                int indY = c.getColumnIndex("Y");

                naviAuditor a = new naviAuditor(c.getString(indAuditor), c.getInt(indCorpus),
                        c.getString(indName), c.getInt(indStage), c.getString(indType),
                        c.getString(indUrl), c.getInt(indX), c.getInt(indY));

                arg0.getAuditors().add(a);
            } while(c.moveToNext());

    }

    private void loadDataTeachers() {
        Cursor c = db.query(dbOpenHelper.teachersHelper.TABLE_NAME, null, "", null, null, null, null);

        if (c.moveToFirst())
            do {
                int indName = c.getColumnIndex("Name");
                int indPos = c.getColumnIndex("Position");
                int indDeg = c.getColumnIndex("Degree"); //! null database*
                int indRank = c.getColumnIndex("AcademicRank"); //! null database*
                int indDesc = c.getColumnIndex("Desciplines");
                int indDir = c.getColumnIndex("Directing");

                String degree = "";
                String rank = "";

                if (!c.isNull(indDeg))
                    degree = c.getString(indDeg);

                if (!c.isNull(indRank))
                    rank = c.getString(indRank);

                teachers.add(degree, checkIsNull(c.getString(indDesc)),
                        checkIsNull(c.getString(indDir)), checkIsNull(c.getString(indName)),
                        checkIsNull(c.getString(indPos)), rank);

            } while(c.moveToNext());
    }

    private String checkIsNull(String arg0) {
        return (arg0 != null) ? arg0 : "";
    }

    public naviTeachers getTeachers() {
        return teachers;
    }

    public naviGraps getGraps() {
        return graps;
    }
}
