package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;

import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 * Преподаватели универстета
 */
public class TeachersHelper { // [CR] и во всех это поправить

    protected static final String TABLE_NAME = "Teachers";
    protected static final String COL_ID_TEACHER = "id_teacher";
    protected static final String COL_ID_DEPARTMENT = "id_department";
    protected static final String COL_TEACHER_LASTNAME = "teacher_lastname";
    protected static final String COL_TEACHER_FIRSTNAME = "teacher_firstname";
    protected static final String COL_TEACHER_MIDDLENAME = "teacher_middlename";
    protected static final String COL_TEACHER_GENDER = "gender";


    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_TEACHER + " INTEGER," +
                COL_ID_DEPARTMENT + " INTEGER," +
                COL_TEACHER_LASTNAME + " TEXT," +
                COL_TEACHER_FIRSTNAME + " TEXT," +
                COL_TEACHER_MIDDLENAME + " TEXT," +
                COL_TEACHER_GENDER + " TEXT" +
                ");");


    }

    public void setTeachers(Context context, UniversityInfo init) {


        /*

        Переход на SQLite Statement ускоряет insert в базу в 2 раза
        по сравнению с ContentValues

         */

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();

        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_TEACHER + ", " + COL_ID_DEPARTMENT + ", " +
                COL_TEACHER_LASTNAME + ", " + COL_TEACHER_FIRSTNAME + ", " + COL_TEACHER_MIDDLENAME + ", " +
                COL_TEACHER_GENDER + ")" + " VALUES (?, ?, ?, ?, ?, ?)";

        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();
        for (int index = 0; index < init.TEACHERS.size(); index++) {
            if (!init.TEACHERS.get(index).IS_DELETED) {
                stmt.bindLong(1, init.TEACHERS.get(index).ID_TEACHER);
                stmt.bindLong(2, init.TEACHERS.get(index).ID_DEPARTMENT);
                stmt.bindString(3, init.TEACHERS.get(index).TEACHER_LASTNAME);
                stmt.bindString(4, init.TEACHERS.get(index).TEACHER_FIRSTNAME);
                stmt.bindString(5, init.TEACHERS.get(index).TEACHER_MIDDLENAME);
                stmt.bindString(6, init.TEACHERS.get(index).GENDER);
                stmt.execute();
                stmt.clearBindings();

            } else {
                DBRequest.delete_byID(sqliteDatabase, TeachersHelper.TABLE_NAME, TeachersHelper.COL_ID_TEACHER, init.TEACHERS.get(index).ID_TEACHER);
            }

        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();

    }


    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<GroupsModel> getTeachers(Context context, String department) {
        String selection = DepartmentsHelper.COL_DEPARTMENT_ID;
        int teacher_id = DBRequest.getIdFromString(context, DepartmentsHelper.TABLE_NAME, selection, DepartmentsHelper.COL_DEPARTMENT_FULLNAME, department);
        ArrayList<String> teachersNameList = new ArrayList<>();

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID_DEPARTMENT + " = " + teacher_id + " ORDER BY " + COL_TEACHER_LASTNAME, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                String lastname = cursor.getString(cursor.getColumnIndex(COL_TEACHER_LASTNAME));
                String firstname = cursor.getString(cursor.getColumnIndex(COL_TEACHER_FIRSTNAME)).substring(0, 1) + ".";
                String middlename = cursor.getString(cursor.getColumnIndex(COL_TEACHER_MIDDLENAME)).substring(0, 1) + ".";

                //  list.add(cursor.getString(cursor.getColumnIndex(selection)));
                teachersNameList.add(lastname + " " + firstname + " " + middlename);

                cursor.moveToNext();

            }

        } catch (SQLiteException e) {
            Log.e("SQLITE EXCEPTION", e.toString(), e);
        }

        ArrayList teacherIdList = DBRequest.getList(context, TABLE_NAME, COL_ID_TEACHER, COL_ID_DEPARTMENT, teacher_id, COL_TEACHER_LASTNAME);
        ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();

        for (int i = 0; i < teachersNameList.size(); i++) {
            GroupsModel groupsModel = new GroupsModel(
                    teachersNameList.get(i),
                    Integer.parseInt(teacherIdList.get(i).toString()),
                    false,
                    false
            );
            groupsModelArrayList.add(groupsModel);
        }
        return groupsModelArrayList;
    }

    public String getTeacherById(Context context, int teacher_id) {
        String teacherName = "";

        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        try {
            String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID_TEACHER + "=" + teacher_id;
            Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);
            if (c.moveToFirst()) {

                String lastname = c.getString(c.getColumnIndex(COL_TEACHER_LASTNAME));
                String firstname = c.getString(c.getColumnIndex(COL_TEACHER_FIRSTNAME)).substring(0, 1) + ".";
                String middlename = c.getString(c.getColumnIndex(COL_TEACHER_MIDDLENAME)).substring(0, 1) + ".";
                teacherName = lastname + " " + firstname + " " + middlename;

            }
            c.close();
        } catch (SQLiteException e) {
            Log.e("DB EXCEPTION", e.toString(), e);
        }

        return teacherName;
    }

}
