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

    public  ArrayList getGroups(Context context) {

        String table = GroupsHelper.TABLE_NAME;
        String selection = GroupsHelper.COL_GROUP_NAME;

        return getList(context,table,selection);
    }


    //Запрос к БД на получение данных
    public  ArrayList getDepartments(Context context){

        String table = DepartmentsHelper.TABLE_NAME;
        String selection = DepartmentsHelper.COL_DEPARTMENT_FULLNAME;

        return getList(context,table,selection);

    }

    //Запрос к БД на получение данных
    public  ArrayList getFaculties(Context context){

        String table = FacultiesHelper.TABLE_NAME;
        String selection = FacultiesHelper.COL_FACULTY_SHORTNAME;

        return getList(context,table,selection);

    }


    //Запрос к БД на получение данных
    public  ArrayList getTeachers(Context context){

        String table = TeachersHelper.TABLE_NAME;
        String selection =  TeachersHelper.COL_TEACHER_LASTNAME;

        return getList(context,table,selection);

    }

    private ArrayList getList(Context context,String table,String selection) {

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        ArrayList list = new ArrayList();
        Cursor cursor = db.rawQuery("SELECT " + selection + " FROM "+table, null);

        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            String name = cursor.getString(0);
            list.add(name);
            cursor.moveToNext();

        }

        return list;
    }

    //TODO возвращает, имеются ли данные в БД  по наличию всех нужных таблиц. содержимое - хоть 1 строчка
    public static boolean isInitializationInfoThere(Context context){


        SQLiteDatabase db;
        ArrayList tables = new ArrayList();
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        c.moveToFirst();
        if (c.moveToNext()) {
            while ( !c.isAfterLast() ) {
                tables.add(c.getString(0));
                c.moveToNext();
            }
        }

        boolean hasTables = false;


        for (int i = 0 ;i<tables.size();i++) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + tables.get(i), null);

            if (cursor != null && cursor.getCount() > 0) {
                hasTables = true;
                cursor.close();


            }


        }

        return hasTables;


    }


    /**
     * Remove all user data
     */
    public static void removeAllFromDatabase(Context context)
    {

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TeachersHelper.TABLE_NAME, null, null);
        db.delete(GroupsHelper.TABLE_NAME,null,null);
        db.delete(DepartmentsHelper.TABLE_NAME,null,null);
        db.delete(FacultiesHelper.TABLE_NAME,null,null);
        db.delete(UniversityInfoHelper.TABLE_NAME,null,null);
        db.delete(PairsHelper.TABLE_NAME,null,null);
        db.delete(SemestersHelper.TABLE_NAME,null,null);

    }

}
