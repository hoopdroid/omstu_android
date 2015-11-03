package savindev.myuniversity.db;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

//TODO ADD GROUPS,FACULTIES,DEPARTMENTS
//TODO CHANGE PAIRS TIME VOVA
public class DBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "university.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "DBHelper";
    private static DBHelper instance = null;

    private UniversityInfoHelper universityInfoHelper;
    private TeachersHelper teachersHelper;
    private SemestersHelper semestersHelper;
    private PairsHelper pairsHelper;
    private GroupsHelper groupsHelper;
    private FacultiesHelper facultiesHelper;
    private DepartmentsHelper departmentsHelper;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        universityInfoHelper = new UniversityInfoHelper();
        teachersHelper = new TeachersHelper();
        semestersHelper = new SemestersHelper();
        pairsHelper = new PairsHelper();
        groupsHelper = new GroupsHelper();
        facultiesHelper = new FacultiesHelper();
        departmentsHelper = new DepartmentsHelper();

    }

    public static DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context);
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        universityInfoHelper.create(db);
        teachersHelper.create(db);
        semestersHelper.create(db);
        pairsHelper.create(db);
        groupsHelper.create(db);
        facultiesHelper.create(db);
        departmentsHelper.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public class UniversityInfoHelper {

        public static final String TABLE_NAME = "UniversityInfo";
        public static final String COL_FULLNAME = "fullname";
        public static final String COL_SHORTNAME = "shortname";
        public static final String COL_DAYS_IN_WEEK = "daysinweek";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_FULLNAME + " TEXT," +
                    COL_SHORTNAME + " TEXT," +
                    COL_DAYS_IN_WEEK + " INTEGER" +
                    ");");
            Log.d(TAG,"SUCCESFULL CREATE TABLE UNIVERSITYINFO");
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public String getUniversityFullName(Cursor cursor){
            return cursor.getString(cursor.getColumnIndex(COL_FULLNAME));
        }

        public String getUniversityShortName(Cursor cursor){
            return cursor.getString(cursor.getColumnIndex(COL_SHORTNAME));
        }

        public int getUniversityDaysInWeek(Cursor cursor){
            return cursor.getInt(cursor.getColumnIndex(COL_DAYS_IN_WEEK));
        }
    }

    public class TeachersHelper {

        public static final String TABLE_NAME = "Teachers";
        public static final String COL_ID_TEACHER = "id_teacher";
        public static final String COL_ID_DEPARTMENT = "id_department";
        public static final String COL_TEACHER_LASTNAME = "teacher_lastname";
        public static final String COL_TEACHER_FIRSTNAME = "teacher_firstname";
        public static final String COL_TEACHER_MIDDLENAME = "teacher_middlename";
        public static final String COL_TEACHER_GENDER = "gender";



        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_TEACHER + " INTEGER," +
                    COL_ID_DEPARTMENT + " INTEGER," +
                    COL_TEACHER_LASTNAME + " TEXT," +
                    COL_TEACHER_FIRSTNAME + " TEXT," +
                    COL_TEACHER_MIDDLENAME + " TEXT," +
                    COL_TEACHER_GENDER + " TEXT" +
                    ");");
            Log.d(TAG, "SUCCESFULL CREATE TABLE TEACHERS");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void getTeacher(Cursor cursor){

        }

    }

    public class SemestersHelper {

        public static final String TABLE_NAME = "Semesters";
        public static final String COL_ID_SEMESTER = "id_semester";
        public static final String COL_BEGIN_DATE = "begindate";
        public static final String COL_END_DATE = "enddate";



        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_SEMESTER + " INTEGER," +
                    COL_BEGIN_DATE + " TEXT," +
                    COL_END_DATE + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void getSemester(Cursor cursor){

        }

    }


    public class PairsHelper {

        public static final String TABLE_NAME = "Pairs";
        public static final String COL_ID_PAIR= "pair_id";
        public static final String COL_PAIR_NUMBER= "pair_number";
        public static final String COL_BEGIN_TIME = "pair_begin_time";
        public static final String COL_END_TIME = "pair_end_time";



        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_PAIR + " INTEGER PRIMARY KEY," +
                    COL_PAIR_NUMBER + " INTEGER," +
                    COL_BEGIN_TIME + " TEXT," +
                    COL_END_TIME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


    }

    public class GroupsHelper {

        public static final String TABLE_NAME = "Groups";
        public static final String COL_ID_GROUP= "group_id";
        public static final String COL_ID_FACULTY= "faculty_id";
        public static final String COL_GROUP_NAME= "name_group";



        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_GROUP + " INTEGER PRIMARY KEY," +
                    COL_ID_FACULTY + " INTEGER," +
                    COL_GROUP_NAME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }





    }



    public class FacultiesHelper {

        public static final String TABLE_NAME = "Faculties";
        public static final String COL_FACULTY_ID = "faculty_id";
        public static final String COL_FACULTY_FULLNAME= "faculty_fullname";
        public static final String COL_FACULTY_SHORTNAME= "faculty_shortname";




        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_FACULTY_ID + " INTEGER PRIMARY KEY," +
                    COL_FACULTY_FULLNAME + " TEXT," +
                    COL_FACULTY_SHORTNAME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


    }

    public class DepartmentsHelper {

        public static final String TABLE_NAME = "Departments";
        public static final String COL_DEPARTMENT_ID = "department_id";
        public static final String COL_FACULTY_ID = "faculty_id";
        public static final String COL_CLASSROOM_ID = "classroom_id";
        public static final String COL_DEPARTMENT_FULLNAME= "department_fullname";
        public static final String COL_DEPARTMENT_SHORTNAME= "department_shortname";




        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_DEPARTMENT_ID + " INTEGER PRIMARY KEY," +
                    COL_FACULTY_ID + " INTEGER," +
                    COL_CLASSROOM_ID + " INTEGER," +
                    COL_DEPARTMENT_FULLNAME + " TEXT," +
                    COL_DEPARTMENT_SHORTNAME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


    }

    public static ArrayList getAllGroups(Context context) {
        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db=dbHelper.getWritableDatabase();
        ArrayList groups = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT * FROM Groups",null);
        cursor.moveToFirst();

            while (cursor.isAfterLast() == false) {
                String name = cursor.getString(2);
                groups.add(name);
                cursor.moveToNext();
            }

        return  groups;
    }




}
