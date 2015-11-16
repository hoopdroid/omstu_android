package savindev.myuniversity.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;

import savindev.myuniversity.schedule.GroupsModel;

// TODO При get-запросах к локальной БД проверять искомые данные на наличие перед выполнением запроса.
// При отсутствии - выводить сообщение об ошибке с предложением скачать данные с сервера.
// Проверять по имени таблице в базе
// Если невозможно - прописать в контракте методов об ошибке, буду отлавливать у себя


// [CR] сделай статичными все классы-методы, где возможно
// [CR] переведи геттеры на использование VIEW
// [CR] не склеивай строки при формировании запросов. Используй bind-переменные
// [CR] и засунь сюда все, что работает с базой данных. негоже им по всей программе расползаться

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
    private UsedSchedulesHelper usedSchedulesHelper;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        universityInfoHelper = new UniversityInfoHelper();
        teachersHelper = new TeachersHelper();
        semestersHelper = new SemestersHelper();
        pairsHelper = new PairsHelper();
        groupsHelper = new GroupsHelper();
        facultiesHelper = new FacultiesHelper();
        departmentsHelper = new DepartmentsHelper();
        usedSchedulesHelper = new UsedSchedulesHelper();

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
        usedSchedulesHelper.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public TeachersHelper getTeachersHelper() {
        return teachersHelper;
    }

    public FacultiesHelper getFacultiesHelper() {
        return facultiesHelper;
    }

    public UniversityInfoHelper getUniversityInfoHelper() {
        return universityInfoHelper;
    }

    public SemestersHelper getSemestersHelper() {
        return semestersHelper;
    }

    public GroupsHelper getGroupsHelper() {
        return groupsHelper;
    }

    public PairsHelper getPairsHelper() {
        return pairsHelper;
    }

    public DepartmentsHelper getDepartmentsHelper() {
        return departmentsHelper;
    }

    public UsedSchedulesHelper getUsedSchedulesHelper() {
        return usedSchedulesHelper;
    }


    public class UniversityInfoHelper { // [CR] либо класс сделать приватным, либо убрать геттеры для классов. правильнее - первое

        // [CR] почему внутренние переменные публичные?

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
            Log.d(TAG, "SUCCESFULL CREATE TABLE UNIVERSITYINFO");
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

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
    }

    public static class TeachersHelper { // [CR] и во всех это поправить

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

        public ArrayList<GroupsModel> getTeachers(Context context, String department) {
            String selection = DepartmentsHelper.COL_DEPARTMENT_ID;
            int teacher_id = getIdFromString(context, DepartmentsHelper.TABLE_NAME, selection, DepartmentsHelper.COL_DEPARTMENT_FULLNAME, department);
            ArrayList<String> teachersNameList = new ArrayList<>();

            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
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

            ArrayList teacherIdList = getList(context, TABLE_NAME, COL_ID_TEACHER, COL_ID_DEPARTMENT, teacher_id);
            ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();

            for (int i = 0; i < teachersNameList.size(); i++) {
                GroupsModel groupsModel = new GroupsModel(
                        teachersNameList.get(i),
                        Integer.parseInt(teacherIdList.get(i).toString()),
                        false
                );
                groupsModelArrayList.add(groupsModel);
            }
            return groupsModelArrayList;
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

        public ArrayList getSemesters(Context context) {

            String table = TABLE_NAME;
            String selection = COL_BEGIN_DATE;

            return getList(context, table, selection, null, 0);

        }
    }


    public class PairsHelper {

        public static final String TABLE_NAME = "Pairs";
        public static final String COL_ID_PAIR = "pair_id";
        public static final String COL_PAIR_NUMBER = "pair_number";
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

    public static class GroupsHelper {

        public static final String TABLE_NAME = "Groups";
        public static final String COL_ID_GROUP = "group_id";
        public static final String COL_ID_FACULTY = "faculty_id";
        public static final String COL_GROUP_NAME = "name_group";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_GROUP + " INTEGER PRIMARY KEY," +
                    COL_ID_FACULTY + " INTEGER," +
                    COL_GROUP_NAME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


        public ArrayList<GroupsModel> getGroups(Context context, String faculty) {


            String selection = FacultiesHelper.COL_FACULTY_ID;
            int group_id = getIdFromString(context, FacultiesHelper.TABLE_NAME, selection, FacultiesHelper.COL_FACULTY_SHORTNAME, faculty);
            ArrayList<String> groupNameList = getList(context, TABLE_NAME, COL_GROUP_NAME, COL_ID_FACULTY, group_id);
            ArrayList groupIdList = getList(context, TABLE_NAME, COL_ID_GROUP, COL_ID_FACULTY, group_id);
            ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();

            for (int i = 0; i < groupNameList.size(); i++) {
                GroupsModel groupsModel = new GroupsModel(
                        groupNameList.get(i),
                        Integer.parseInt(groupIdList.get(i).toString()),
                        true
                );
                groupsModelArrayList.add(groupsModel);
            }

            return groupsModelArrayList;

        }

    }


    public static class FacultiesHelper {

        public static final String TABLE_NAME = "Faculties";
        public static final String COL_FACULTY_ID = "faculty_id";
        public static final String COL_FACULTY_FULLNAME = "faculty_fullname";
        public static final String COL_FACULTY_SHORTNAME = "faculty_shortname";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_FACULTY_ID + " INTEGER PRIMARY KEY," +
                    COL_FACULTY_FULLNAME + " TEXT," +
                    COL_FACULTY_SHORTNAME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


        public ArrayList<String> getFaculties(Context context) {

            String table = FacultiesHelper.TABLE_NAME;
            String selection = FacultiesHelper.COL_FACULTY_SHORTNAME;

            return getList(context, table, selection);

        }


    }

    public class DepartmentsHelper {

        public static final String TABLE_NAME = "Departments";
        public static final String COL_DEPARTMENT_ID = "department_id";
        public static final String COL_FACULTY_ID = "faculty_id";
        public static final String COL_CLASSROOM_ID = "classroom_id";
        public static final String COL_DEPARTMENT_FULLNAME = "department_fullname";
        public static final String COL_DEPARTMENT_SHORTNAME = "department_shortname";


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

        //Запрос к БД на получение данных
        public ArrayList<String> getDepartments(Context context) {

            String table = DepartmentsHelper.TABLE_NAME;
            String selection = DepartmentsHelper.COL_DEPARTMENT_FULLNAME;

            return getList(context, table, selection);

        }


    }


    public static class UsedSchedulesHelper {

        public static final String TABLE_NAME = "UsedSchedules";
        public static final String COL_ID_SCHEDULE = "id_schedule";
        public static final String COL_NAME_SCHEDULE = "name_schedule";
        public static final String COL_IS_GROUP = "is_group";
        public static final String COL_IS_MAIN = "is_main";
        public static final String COL_LAST_REFRESH_DATE = "last_refresh_date";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_SCHEDULE + " INTEGER PRIMARY KEY," +
                    COL_NAME_SCHEDULE + " TEXT," +
                    COL_IS_GROUP + " INTEGER," +
                    COL_IS_MAIN + " INTEGER," +
                    COL_LAST_REFRESH_DATE + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


        public static void setSchedule(Context context, int groupid, boolean isGroup, boolean isMain, String lastRefresh) {

            int isGroupDB, isMainDB;

            if (isGroup)
                isGroupDB = 1;
            else
                isGroupDB = 0;

            if (isMain)
                isMainDB = 1;
            else
                isMainDB = 0;

            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            ContentValues scheduleRow = new ContentValues();
            scheduleRow.put(COL_ID_SCHEDULE, groupid);
            scheduleRow.put(COL_NAME_SCHEDULE, getUserGroup(groupid, context));
            scheduleRow.put(COL_IS_GROUP, isGroupDB);
            scheduleRow.put(COL_IS_MAIN, isMainDB);
            scheduleRow.put(COL_LAST_REFRESH_DATE, lastRefresh);
            db.insert(TABLE_NAME, null, scheduleRow);

        }

        public static void deleteSchedule(Context context, int id) {
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            delete_byID(db, TABLE_NAME, COL_ID_SCHEDULE, id);
        }

        public static GroupsModel getMainGroupModel(Context context) {

            GroupsModel groupsModelMain = null;
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();

            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE is_main=1", null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    boolean isGroup;

                    if (cursor.getInt(cursor.getColumnIndex(COL_IS_GROUP)) == 1)
                        isGroup = true;
                    else
                        isGroup = false;

                    groupsModelMain = new GroupsModel(
                            cursor.getString(cursor.getColumnIndex(COL_NAME_SCHEDULE)),
                            cursor.getInt(cursor.getColumnIndex(COL_ID_SCHEDULE)),
                            isGroup,
                            cursor.getString(cursor.getColumnIndex(COL_LAST_REFRESH_DATE))
                    );
                    cursor.moveToNext();
                }

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);

            }
            return groupsModelMain;
        }


        public static ArrayList<GroupsModel> getGroupsModelList(Context context) {

            ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();

            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();

            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE is_main=0", null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    boolean isGroup;
                    if (cursor.getInt(cursor.getColumnIndex(COL_IS_GROUP)) == 1)
                        isGroup = true;
                    else
                        isGroup = false;

                    GroupsModel groupsModel = new GroupsModel(
                            cursor.getString(cursor.getColumnIndex(COL_NAME_SCHEDULE)),
                            cursor.getInt(cursor.getColumnIndex(COL_ID_SCHEDULE)),
                            isGroup,
                            cursor.getString(cursor.getColumnIndex(COL_LAST_REFRESH_DATE))
                    );
                    groupsModelArrayList.add(groupsModel);
                    cursor.moveToNext();
                }

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);
            }

            return groupsModelArrayList;
        }


    }


    private static ArrayList getList(Context context, String table, String selection, String findColumn, int valueColumn) {

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        ArrayList list = new ArrayList();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT " + selection + " FROM " + table + " WHERE " + findColumn + " = " + valueColumn + " ORDER BY " + selection, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                //  list.add(cursor.getString(cursor.getColumnIndex(selection)));
                list.add(cursor.getString(cursor.getColumnIndex(selection)))
                ;
                cursor.moveToNext();

            }

        } catch (SQLiteException e) {
            Log.e("SQLITE EXCEPTION", e.toString(), e);
        }


        return list;
    }

    private static ArrayList getList(Context context, String table, String selection) {

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        ArrayList list = new ArrayList();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT " + selection + " FROM " + table + " ORDER BY " + selection, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                list.add(cursor.getString(cursor.getColumnIndex(selection)));
                cursor.moveToNext();
            }

        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);
        }

        return list;
    }


    public static boolean isInitializationInfoThere(Context context) {

        SQLiteDatabase db;
        ArrayList tables = getAllDBTables(context);
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();


        boolean hasTables = false;


        for (int i = 0; i < tables.size(); i++) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + tables.get(i), null);

            if (cursor != null && cursor.getCount() > 0) {
                hasTables = true;
                cursor.close();
            }
        }

        return hasTables;


    }

    public static boolean isTableExists(String table, Context context) {
        boolean isExists = false;
        ArrayList tablesList = getAllDBTables(context);
        for (int i = 0; i < tablesList.size(); i++) {
            if (table.equals(tablesList.get(i))) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }


    public static ArrayList getAllDBTables(Context context) {
        SQLiteDatabase db;
        ArrayList tables = new ArrayList();
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        c.moveToFirst();
        if (c.moveToNext()) {
            while (!c.isAfterLast()) {
                String table = c.getString(c.getColumnIndex("name"));
                if (!table.equals("android_metadata"))
                    tables.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        return tables;
    }

    public static int getIdFromString(Context context, String tableName, String selection, String columnName, String valueColumn) {

        int id = 0;
        DBHelper dbHelper = getInstance(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

        try {
            String selectQuery = "SELECT " + selection + " FROM " + tableName + " WHERE " + columnName + "=?";
            Cursor c = sqLiteDatabase.rawQuery(selectQuery, new String[]{valueColumn});
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(selection));
            }
            c.close();
        } catch (SQLiteException e) {
            Log.e("DB EXCEPTION", e.toString(), e);
        }

        return id;
    }

    public static void removeAllFromDatabase(Context context) {
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(TeachersHelper.TABLE_NAME, null, null);
        db.delete(GroupsHelper.TABLE_NAME, null, null);
        db.delete(DepartmentsHelper.TABLE_NAME, null, null);
        db.delete(FacultiesHelper.TABLE_NAME, null, null);
        db.delete(UniversityInfoHelper.TABLE_NAME, null, null);
        db.delete(PairsHelper.TABLE_NAME, null, null);
        db.delete(SemestersHelper.TABLE_NAME, null, null);
    }


    public static void delete_byID(SQLiteDatabase db, String table, String select, int id) {
        db.delete(table, select + "=" + id, null);
    }

    public static ArrayList sortList(ArrayList arrayList) {
        Collections.sort(arrayList);
        return arrayList;
    }

    public static String getUserGroup(int id, Context context) {
        String groupName = "Group";

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String find = "SELECT * FROM  " + DBHelper.GroupsHelper.TABLE_NAME + " WHERE " + DBHelper.GroupsHelper.COL_ID_GROUP + " = " + id;
        Cursor cursor = sqLiteDatabase.rawQuery(find, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            groupName = cursor.getString(cursor.getColumnIndex(DBHelper.GroupsHelper.COL_GROUP_NAME));
            cursor.moveToNext();
        }
        cursor.close();
        return groupName;
    }
}