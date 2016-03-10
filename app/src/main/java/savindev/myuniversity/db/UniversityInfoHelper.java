package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 * Общая информация о университете
 */
public class UniversityInfoHelper { // [CR] либо класс сделать приватным, либо убрать геттеры для классов. правильнее - первое


    protected static final String TABLE_NAME = "UniversityInfo";
    protected static final String COL_UNIVERSITY_ID = "university_id";
    protected static final String COL_FULLNAME = "fullname";
    protected static final String COL_SHORTNAME = "shortname";
    protected static final String COL_DAYS_IN_WEEK = "daysinweek";


    private DBHelper dbHelper;

    public UniversityInfoHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_UNIVERSITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_FULLNAME + " TEXT," +
                COL_SHORTNAME + " TEXT," +
                COL_DAYS_IN_WEEK + " INTEGER" +
                ");");

    }

    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getDaysInWeek() {

        int daysinweek = 0;

        SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();
        Cursor cursor;

        cursor = sqliteDatabase.rawQuery("SELECT " + COL_DAYS_IN_WEEK + " FROM " + TABLE_NAME, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            daysinweek = cursor.getInt(cursor.getColumnIndex(COL_DAYS_IN_WEEK));

            cursor.moveToNext();

        }
        cursor.close();
        return daysinweek;

    }

    public String getUniversityFullName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(COL_FULLNAME));
    }

    public String getUniversityShortName(Cursor cursor) {
        return cursor.getString(cursor.getColumnIndex(COL_SHORTNAME));
    }

    public int getUniversityDaysInWeek(Cursor cursor) {
        return cursor.getInt(cursor.getColumnIndex(COL_DAYS_IN_WEEK));
    }

    public void setUniversityInfo(Context context, UniversityInfo init) {
        //PARSE UNIVERSITY INFO TO SQLITE

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_FULLNAME + ", " + COL_SHORTNAME + ", " +
                COL_DAYS_IN_WEEK + ")" + " VALUES (?, ?, ?)";

        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        stmt.bindString(1, init.getUNIVERSITY_FULLNAME());
        stmt.bindString(2, init.getUNIVERSITY_SHORTNAME());
        stmt.bindLong(3, init.getDAYS_IN_WEEK());

        stmt.execute();
        stmt.clearBindings();
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }
}
