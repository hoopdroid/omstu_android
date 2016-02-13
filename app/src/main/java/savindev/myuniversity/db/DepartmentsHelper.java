package savindev.myuniversity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;

import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 *
 * Кафедры
 *
 */
public class DepartmentsHelper {

    protected static final String TABLE_NAME = "Departments";
    protected static final String COL_DEPARTMENT_ID = "department_id";
    protected static final String COL_FACULTY_ID = "faculty_id";
    protected static final String COL_CLASSROOM_ID = "classroom_id";
    protected static final String COL_DEPARTMENT_FULLNAME = "department_fullname";
    protected static final String COL_DEPARTMENT_SHORTNAME = "department_shortname";


    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_DEPARTMENT_ID + " INTEGER PRIMARY KEY," +
                COL_FACULTY_ID + " INTEGER," +
                COL_CLASSROOM_ID + " INTEGER," +
                COL_DEPARTMENT_FULLNAME + " TEXT," +
                COL_DEPARTMENT_SHORTNAME + " TEXT" +
                ");");

    }

    public void setDepartments(Context context, UniversityInfo init) {

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_DEPARTMENT_ID + ", " + COL_FACULTY_ID + ", " +
                COL_CLASSROOM_ID + ", " + COL_DEPARTMENT_FULLNAME + ", " +
                COL_DEPARTMENT_SHORTNAME + ")" + " VALUES (?, ?, ?, ?, ?)";

        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        for (int index = 0; index < init.DEPARTMENTS.size(); index++) {
            if (!init.DEPARTMENTS.get(index).IS_DELETED) {
                stmt.bindLong(1, init.DEPARTMENTS.get(index).ID_DEPARTMENT);
                stmt.bindLong(2, init.DEPARTMENTS.get(index).ID_FACULTY);
                stmt.bindLong(3, init.DEPARTMENTS.get(index).ID_CLASSROOM);
                stmt.bindString(4, init.DEPARTMENTS.get(index).DEPARTMENT_FULLNAME);
                stmt.bindString(5, init.DEPARTMENTS.get(index).DEPARTMENT_SHORTNAME);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, DepartmentsHelper.TABLE_NAME, DepartmentsHelper.COL_DEPARTMENT_ID, init.DEPARTMENTS.get(index).ID_DEPARTMENT);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }

    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Запрос к БД на получение данных
    public ArrayList<String> getDepartments(Context context) {

        String table = DepartmentsHelper.TABLE_NAME;
        String selection = DepartmentsHelper.COL_DEPARTMENT_FULLNAME;

        return DBRequest.getList(context, table, selection);

    }


}
