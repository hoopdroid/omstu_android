package savindev.myuniversity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 * Факультеты
 */
public class FacultiesHelper {

    protected static final String TABLE_NAME = "Faculties";
    protected static final String COL_FACULTY_ID = "faculty_id";
    protected static final String COL_FACULTY_FULLNAME = "faculty_fullname";
    protected static final String COL_FACULTY_SHORTNAME = "faculty_shortname";


    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_FACULTY_ID + " INTEGER PRIMARY KEY," +
                COL_FACULTY_FULLNAME + " TEXT," +
                COL_FACULTY_SHORTNAME + " TEXT" +
                ");");

    }

    public void setFaculties(Context context, UniversityInfo init) {

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_FACULTY_ID + ", " + COL_FACULTY_FULLNAME + ", " +
                COL_FACULTY_SHORTNAME + ")" + " VALUES (?, ?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        for (int index = 0; index < init.FACULTIES.size(); index++) {
            if (!init.FACULTIES.get(index).IS_DELETED) {
                stmt.bindLong(1, init.FACULTIES.get(index).ID_FACULTY);
                stmt.bindString(2, init.FACULTIES.get(index).FACULTY_FULLNAME);
                stmt.bindString(3, init.FACULTIES.get(index).FACULTY_SHORTNAME);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, FacultiesHelper.TABLE_NAME, FacultiesHelper.COL_FACULTY_ID, init.FACULTIES.get(index).ID_FACULTY);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }

    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public ArrayList<String> getFaculties(Context context) {

        String table = FacultiesHelper.TABLE_NAME;
        String selection = FacultiesHelper.COL_FACULTY_SHORTNAME;

        return DBRequest.getList(context, table, selection);

    }


}
