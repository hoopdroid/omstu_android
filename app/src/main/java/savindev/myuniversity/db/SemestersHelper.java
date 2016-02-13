package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import savindev.myuniversity.schedule.DateUtil;
import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 * Информация о учебных семестрах
 */
public class SemestersHelper {

    protected static final String TABLE_NAME = "Semesters";
    protected static final String COL_ID_SEMESTER = "id_semester";
    protected static final String COL_BEGIN_DATE = "begindate";
    protected static final String COL_END_DATE = "enddate";


    private DBHelper dbHelper;

    public SemestersHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_SEMESTER + " INTEGER," +
                COL_BEGIN_DATE + " TEXT," +
                COL_END_DATE + " TEXT" +
                ");");

    }

    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setSemesters(Context context, UniversityInfo init) {

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_SEMESTER + ", " + COL_BEGIN_DATE + ", " +
                COL_END_DATE + ")" + " VALUES (?, ?, ?)";
        //PARSE SEMESTRES TO SQLITE

        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();
        for (int index = 0; index < init.SEMESTERS.size(); index++) {
            if (!init.SEMESTERS.get(index).IS_DELETED) {
                stmt.bindLong(1, init.SEMESTERS.get(index).ID_SEMESTER);
                stmt.bindString(2, init.SEMESTERS.get(index).BEGIN_DT);
                stmt.bindString(3, init.SEMESTERS.get(index).END_DT);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, SemestersHelper.TABLE_NAME, SemestersHelper.COL_ID_SEMESTER, init.SEMESTERS.get(index).ID_SEMESTER);
            }


        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }

    public ArrayList getSemesters(Context context) {

        String table = TABLE_NAME;
        String selection = COL_BEGIN_DATE;

        return DBRequest.getList(context, table, selection, null, 0, selection);

    }

    public String getSemesterEndDate(Context context, String date) {
        String endDate = "";
        int idSemester;

        idSemester = 2;
        //idSemester = getNumSemesterFromDate(date);

        /*
            Проверяем вхождение даты в интервал семестра
         */
        if (idSemester == 0)
            return null;

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String find = "SELECT " + COL_END_DATE + " FROM  " + TABLE_NAME + " WHERE " + COL_ID_SEMESTER + " = " + idSemester;
        Cursor cursor = sqLiteDatabase.rawQuery(find, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            endDate = cursor.getString(cursor.getColumnIndex(COL_END_DATE));
            cursor.moveToNext();
        }
        cursor.close();
        return endDate;
    }


    public String getSemesterBeginDate(Context context, String date) {
        String beginDate = "";
        int idSemester;

        idSemester = 2;
        // idSemester = getNumSemesterFromDate(date);

        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String find = "SELECT " + COL_BEGIN_DATE + " FROM  " + TABLE_NAME + " WHERE " + COL_ID_SEMESTER + " = " + idSemester;
        Cursor cursor = sqLiteDatabase.rawQuery(find, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            beginDate = cursor.getString(cursor.getColumnIndex(COL_BEGIN_DATE));
            cursor.moveToNext();
        }
        cursor.close();
        return beginDate;
    }

    public int getNumSemesterFromDate(String date) {
        int num = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        /*
         Получаем пару beginDate + endDate -> если date входит в этот диапозон получаем idSemester
         Если не входит переходим курсором на след семестр
         Выводим 0 в случае если ничего не нашли
         */

        String find = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = database.rawQuery(find, null);

        cursor.moveToFirst();
        try {
            while (!cursor.isAfterLast()) {

                if (format.parse(date).compareTo(format.parse(
                        DateUtil.formatStandart(cursor.getString(cursor.getColumnIndex(COL_BEGIN_DATE))))) >= 0
                        && format.parse(date).
                        compareTo(format.parse
                                (DateUtil.formatStandart(cursor.getString(cursor.getColumnIndex(COL_END_DATE))))) <= 0) {
                    num = cursor.getInt(cursor.getColumnIndex(COL_ID_SEMESTER));
                }
                cursor.moveToNext();

            }
            cursor.close();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return num;
    }
}
