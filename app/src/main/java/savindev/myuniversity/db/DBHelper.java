package savindev.myuniversity.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import savindev.myuniversity.schedule.DateUtil;
import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.schedule.ScheduleModel;
import savindev.myuniversity.serverTasks.Schedule;
import savindev.myuniversity.serverTasks.ScheduleDates;
import savindev.myuniversity.serverTasks.UniversityInfo;

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
    private SchedulesHelper schedulesHelper;
    private ScheduleDatesHelper scheduleDatesHelper;

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
        schedulesHelper = new SchedulesHelper();
        scheduleDatesHelper = new ScheduleDatesHelper();

    }

    public static DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context.getApplicationContext());
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
        schedulesHelper.create(db);
        scheduleDatesHelper.create(db);
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

    public SchedulesHelper getSchedulesHelper() {
        return schedulesHelper;
    }

    public ScheduleDatesHelper getScheduleDatesHelper() {
        return scheduleDatesHelper;
    }


    public class UniversityInfoHelper { // [CR] либо класс сделать приватным, либо убрать геттеры для классов. правильнее - первое


        protected static final String TABLE_NAME = "UniversityInfo";
        protected static final String COL_FULLNAME = "fullname";
        protected static final String COL_SHORTNAME = "shortname";
        protected static final String COL_DAYS_IN_WEEK = "daysinweek";


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

        public void setUniversityInfo(Context context, UniversityInfo init) {
            //PARSE UNIVERSITY INFO TO SQLITE

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            ContentValues uninfoRow = new ContentValues();
            uninfoRow.put(DBHelper.UniversityInfoHelper.COL_FULLNAME, init.UNIVERSITY_FULLNAME);
            uninfoRow.put(DBHelper.UniversityInfoHelper.COL_SHORTNAME, init.UNIVERSITY_SHORTNAME);
            uninfoRow.put(DBHelper.UniversityInfoHelper.COL_DAYS_IN_WEEK, init.DAYS_IN_WEEK);
            sqliteDatabase.insert(DBHelper.UniversityInfoHelper.TABLE_NAME, null, uninfoRow);
        }
    }

    public static class TeachersHelper { // [CR] и во всех это поправить

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
            Log.d(TAG, "SUCCESFULL CREATE TABLE TEACHERS");

        }

        public void setTeachers(Context context, UniversityInfo init) {

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            ContentValues teacherRow = new ContentValues();
            for (int index = 0; index < init.TEACHERS.size(); index++) {
                if (!init.TEACHERS.get(index).IS_DELETED) {
                    teacherRow.put(DBHelper.TeachersHelper.COL_ID_TEACHER, init.TEACHERS.get(index).ID_TEACHER);
                    teacherRow.put(DBHelper.TeachersHelper.COL_ID_DEPARTMENT, init.TEACHERS.get(index).ID_DEPARTMENT);
                    teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_LASTNAME, init.TEACHERS.get(index).TEACHER_LASTNAME);
                    teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_FIRSTNAME, init.TEACHERS.get(index).TEACHER_FIRSTNAME);
                    teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_MIDDLENAME, init.TEACHERS.get(index).TEACHER_MIDDLENAME);
                    sqliteDatabase.insert(DBHelper.TeachersHelper.TABLE_NAME, null, teacherRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, DBHelper.TeachersHelper.TABLE_NAME, DBHelper.TeachersHelper.COL_ID_TEACHER, init.TEACHERS.get(index).ID_TEACHER);
                }
            }
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
                        false
                );
                groupsModelArrayList.add(groupsModel);
            }
            int a = 5;
            return groupsModelArrayList;
        }

        public String getTeacherById(Context context, int teacher_id) {
            String teacherName = "";

            DBHelper dbHelper = getInstance(context);
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

    public class SemestersHelper {

        protected static final String TABLE_NAME = "Semesters";
        protected static final String COL_ID_SEMESTER = "id_semester";
        protected static final String COL_BEGIN_DATE = "begindate";
        protected static final String COL_END_DATE = "enddate";


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


            //PARSE SEMESTRES TO SQLITE
            ContentValues semestresRow = new ContentValues();
            for (int index = 0; index < init.SEMESTERS.size(); index++) {
                if (!init.SEMESTERS.get(index).IS_DELETED) {
                    semestresRow.put(DBHelper.SemestersHelper.COL_ID_SEMESTER, init.SEMESTERS.get(index).ID_SEMESTER);
                    semestresRow.put(DBHelper.SemestersHelper.COL_BEGIN_DATE, init.SEMESTERS.get(index).BEGIN_DT);
                    semestresRow.put(DBHelper.SemestersHelper.COL_END_DATE, init.SEMESTERS.get(index).END_DT);
                    sqliteDatabase.insert(DBHelper.SemestersHelper.TABLE_NAME, null, semestresRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, DBHelper.SemestersHelper.TABLE_NAME, DBHelper.SemestersHelper.COL_ID_SEMESTER, init.SEMESTERS.get(index).ID_SEMESTER);
                }
            }
        }

        public ArrayList getSemesters(Context context) {

            String table = TABLE_NAME;
            String selection = COL_BEGIN_DATE;

            return DBRequest.getList(context, table, selection, null, 0, selection);

        }

        public String getSemesterEndDate(Context context, int idSemester) {
            String endDate = "";

            DBHelper dbHelper = DBHelper.getInstance(context);
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
    }

    public class PairsHelper {

        protected static final String TABLE_NAME = "Pairs";
        protected static final String COL_ID_PAIR = "pair_id";
        protected static final String COL_PAIR_NUMBER = "pair_number";
        protected static final String COL_BEGIN_TIME = "pair_begin_time";
        protected static final String COL_END_TIME = "pair_end_time";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_PAIR + " INTEGER PRIMARY KEY," +
                    COL_PAIR_NUMBER + " INTEGER," +
                    COL_BEGIN_TIME + " TEXT," +
                    COL_END_TIME + " TEXT" +
                    ");");

        }

        public void setPairs(Context context, UniversityInfo init) {

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();


            //PARSE PAIRS TO SQLITE
            ContentValues pairsRow = new ContentValues();
            for (int index = 0; index < init.PAIRS.size(); index++) {
                if (!init.PAIRS.get(index).IS_DELETED) {
                    pairsRow.put(DBHelper.PairsHelper.COL_ID_PAIR, init.PAIRS.get(index).ID_PAIR);
                    pairsRow.put(DBHelper.PairsHelper.COL_PAIR_NUMBER, init.PAIRS.get(index).PAIR_NUMBER);
                    pairsRow.put(DBHelper.PairsHelper.COL_BEGIN_TIME, init.PAIRS.get(index).PAIR_BEGIN_TIME);
                    pairsRow.put(DBHelper.PairsHelper.COL_END_TIME, init.PAIRS.get(index).PAIR_END_TIME);
                    sqliteDatabase.insert(DBHelper.PairsHelper.TABLE_NAME, null, pairsRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, DBHelper.PairsHelper.TABLE_NAME, DBHelper.PairsHelper.COL_ID_PAIR, init.PAIRS.get(index).ID_PAIR);
                }
            }
        }

        public String getPairNumber(Context context, int pairId) {

            int number = 0;

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

            try {
                String selectQuery = "SELECT " + COL_PAIR_NUMBER + " FROM " + TABLE_NAME + " WHERE " + COL_ID_PAIR + "=" + pairId;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);
                if (c.moveToFirst()) {
                    number = c.getInt(c.getColumnIndex(COL_PAIR_NUMBER));
                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }

            return Integer.toString(number);
        }

        public String getPairTime(Context context, String selectionTime, int pairId) {
            String time = "";

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

            try {
                String selectQuery = "SELECT " + selectionTime + " FROM " + TABLE_NAME + " WHERE " + COL_ID_PAIR + "=" + pairId;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);
                if (c.moveToFirst()) {
                    time = c.getString(c.getColumnIndex(selectionTime)).substring(0, 5);

                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }

            return time;
        }


    }

    public static class GroupsHelper {

        protected static final String TABLE_NAME = "Groups";
        protected static final String COL_ID_GROUP = "group_id";
        protected static final String COL_ID_FACULTY = "faculty_id";
        protected static final String COL_GROUP_NAME = "name_group";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_GROUP + " INTEGER PRIMARY KEY," +
                    COL_ID_FACULTY + " INTEGER," +
                    COL_GROUP_NAME + " TEXT" +
                    ");");

        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

        public void setGroups(Context context, UniversityInfo init) {

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();


            //PARSE GROUPS TO SQLITE
            ContentValues groupsRow = new ContentValues();
            for (int index = 0; index < init.GROUPS.size(); index++) {
                if (!init.GROUPS.get(index).IS_DELETED) {
                    groupsRow.put(DBHelper.GroupsHelper.COL_ID_GROUP, init.GROUPS.get(index).ID_GROUP);
                    groupsRow.put(DBHelper.GroupsHelper.COL_GROUP_NAME, init.GROUPS.get(index).GROUP_NAME);
                    groupsRow.put(DBHelper.GroupsHelper.COL_ID_FACULTY, init.GROUPS.get(index).ID_FACULTY);
                    sqliteDatabase.insert(DBHelper.GroupsHelper.TABLE_NAME, null, groupsRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, DBHelper.GroupsHelper.TABLE_NAME, DBHelper.GroupsHelper.COL_ID_GROUP, init.GROUPS.get(index).ID_GROUP);
                }
            }
        }


        public ArrayList<GroupsModel> getGroups(Context context, String faculty) {


            String selection = FacultiesHelper.COL_FACULTY_ID;
            int group_id = DBRequest.getIdFromString(context, FacultiesHelper.TABLE_NAME, selection, FacultiesHelper.COL_FACULTY_SHORTNAME, faculty);
            ArrayList<String> groupNameList = DBRequest.getList(context, TABLE_NAME, COL_GROUP_NAME, COL_ID_FACULTY, group_id, COL_GROUP_NAME);
            ArrayList groupIdList = DBRequest.getList(context, TABLE_NAME, COL_ID_GROUP, COL_ID_FACULTY, group_id, COL_GROUP_NAME);
            ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();
            int g = 5;
            for (int i = 0; i < groupNameList.size(); i++) {
                GroupsModel groupsModel = new GroupsModel(
                        groupNameList.get(i),
                        Integer.parseInt(groupIdList.get(i).toString()),
                        true
                );
                groupsModelArrayList.add(groupsModel);
            }
            int a = 5;
            return groupsModelArrayList;

        }

        public String getGroupById(Context context, int groupId) {
            String groupName = "";

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            try {
                String selectQuery = "SELECT " + COL_GROUP_NAME + " FROM " + TABLE_NAME + " WHERE " + COL_ID_GROUP + "=" + groupId;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);
                if (c.moveToFirst()) {
                    groupName = c.getString(c.getColumnIndex(COL_GROUP_NAME));
                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }


            return groupName;
        }

    }


    public static class FacultiesHelper {

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


            //PARSE FACULTIES TO SQLITE
            ContentValues facultiesRow = new ContentValues();
            for (int index = 0; index < init.FACULTIES.size(); index++) {
                if (!init.FACULTIES.get(index).IS_DELETED) {
                    facultiesRow.put(DBHelper.FacultiesHelper.COL_FACULTY_ID, init.FACULTIES.get(index).ID_FACULTY);
                    facultiesRow.put(DBHelper.FacultiesHelper.COL_FACULTY_FULLNAME, init.FACULTIES.get(index).FACULTY_FULLNAME);
                    facultiesRow.put(DBHelper.FacultiesHelper.COL_FACULTY_SHORTNAME, init.FACULTIES.get(index).FACULTY_SHORTNAME);
                    sqliteDatabase.insert(DBHelper.FacultiesHelper.TABLE_NAME, null, facultiesRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, DBHelper.FacultiesHelper.TABLE_NAME, DBHelper.FacultiesHelper.COL_FACULTY_ID, init.FACULTIES.get(index).ID_FACULTY);
                }
            }
        }

        public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }


        public ArrayList<String> getFaculties(Context context) {

            String table = FacultiesHelper.TABLE_NAME;
            String selection = FacultiesHelper.COL_FACULTY_SHORTNAME;

            return DBRequest.getList(context, table, selection);

        }


    }

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


            //PARSE DEPARTMENTS TO SQLITE
            ContentValues departmentsRow = new ContentValues();
            for (int index = 0; index < init.DEPARTMENTS.size(); index++) {
                if (!init.DEPARTMENTS.get(index).IS_DELETED) {
                    departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_ID, init.DEPARTMENTS.get(index).ID_DEPARTMENT);
                    departmentsRow.put(DBHelper.DepartmentsHelper.COL_FACULTY_ID, init.DEPARTMENTS.get(index).ID_FACULTY);
                    departmentsRow.put(DBHelper.DepartmentsHelper.COL_CLASSROOM_ID, init.DEPARTMENTS.get(index).ID_CLASSROOM);
                    departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_FULLNAME, init.DEPARTMENTS.get(index).DEPARTMENT_FULLNAME);
                    departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_SHORTNAME, init.DEPARTMENTS.get(index).DEPARTMENT_SHORTNAME);
                    sqliteDatabase.insert(DBHelper.DepartmentsHelper.TABLE_NAME, null, departmentsRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, DBHelper.DepartmentsHelper.TABLE_NAME, DBHelper.DepartmentsHelper.COL_DEPARTMENT_ID, init.DEPARTMENTS.get(index).ID_DEPARTMENT);
                }
            }
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

    public static class SchedulesHelper {

        public static final String TABLE_NAME = "Schedules";
        public static final String COL_SCHEDULE_ID = "schedule_id";
        public static final String COL_PAIR_ID = "pair_id";
        public static final String COL_GROUP_ID = "group_id";
        public static final String COL_TEACHER_ID = "teacher_id";
        public static final String COL_DISCIPLINE_NAME = "discipline_name";
        public static final String COL_DISCIPLINE_TYPE = "discipline_type";
        public static final String COL_SCHEDULE_DATE = "schedule__date";
        public static final String COL_CLASSROOM_ID = "classroom_id";
        public static final String COL_SUBGROUP_NUMBER = "subgroup_number";
        public static final String COL_IS_CANCELLED = "is_cancelled";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_SCHEDULE_ID + " INTEGER," +
                    COL_PAIR_ID + " INTEGER," +
                    COL_GROUP_ID + " INTEGER," +
                    COL_TEACHER_ID + " INTEGER," +
                    COL_DISCIPLINE_NAME + " TEXT," +
                    COL_DISCIPLINE_TYPE + " TEXT," +
                    COL_SCHEDULE_DATE + " TEXT," +
                    COL_CLASSROOM_ID + " INTEGER," +
                    COL_SUBGROUP_NUMBER + " INTEGER," +
                    COL_IS_CANCELLED + " INTEGER" +
                    ");");
        }

        public String dateFormat(ArrayList<Schedule> schedule, int index) {
            String dt = schedule.get(0).SCHEDULE_FIRST_DATE;
            Log.d("BEFORE", dt);// Start date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DATE, schedule.get(index).SCHEDULE_INTERVAL);  // number of days to add
            dt = sdf.format(c.getTime());  // dt is now the new date
            Log.d("AFTER", dt);
            return dt;
        }

        public void setSchedule(Context context, ArrayList<Schedule> schedule) {

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String previousValue = "";
            Date beginDate = null;
            Date endDate = null;

            for (int index = 1; index < schedule.size(); index++) {
                if (!schedule.get(index).IS_DELETED) {
                    //TODO test all variants of work dataalreadyornot function)) {
                    try {
                        beginDate = format.parse(schedule.get(index).SCHEDULE_FIRST_DATE);//дата начала пары
                        endDate = format.parse(DateUtil.formatStandart(helper.getSemestersHelper().getSemesterEndDate(context, 1))); //конец семестра
                        previousValue = schedule.get(index).SCHEDULE_FIRST_DATE;
                        while (beginDate.compareTo(endDate) <= 0) { // если дата начала пары раньше конца семестра

                            ContentValues scheduleRow = new ContentValues();
                            scheduleRow.put(SchedulesHelper.COL_SCHEDULE_ID, schedule.get(index).ID_SCHEDULE);
                            scheduleRow.put(SchedulesHelper.COL_PAIR_ID, schedule.get(index).ID_PAIR);
                            scheduleRow.put(SchedulesHelper.COL_GROUP_ID, schedule.get(index).ID_GROUP);
                            scheduleRow.put(SchedulesHelper.COL_TEACHER_ID, schedule.get(index).ID_TEACHER);
                            scheduleRow.put(SchedulesHelper.COL_DISCIPLINE_NAME, schedule.get(index).DISCIPLINE_NAME);
                            scheduleRow.put(SchedulesHelper.COL_DISCIPLINE_TYPE, schedule.get(index).DISCIPLINE_TYPE);
                            previousValue = DateUtil.dateFormatIncrease(schedule, index, previousValue);
                            scheduleRow.put(SchedulesHelper.COL_SCHEDULE_DATE, previousValue);
                            scheduleRow.put(SchedulesHelper.COL_CLASSROOM_ID, schedule.get(index).ID_CLASSROOM);
                            scheduleRow.put(SchedulesHelper.COL_SUBGROUP_NUMBER, schedule.get(index).SUBGROUP_NUMBER);
                            scheduleRow.put(SchedulesHelper.COL_IS_CANCELLED, false);//TODO Сделать обработку в ScheduleDates

                            if (!DBRequest.checkIsDataAlreadyInDBorNot(context, TABLE_NAME, COL_SCHEDULE_ID, schedule.get(index).ID_SCHEDULE, COL_SCHEDULE_DATE, previousValue))
                                sqliteDatabase.insert(TABLE_NAME, null, scheduleRow);
                            else break;
                            beginDate = format.parse(previousValue);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else
                    DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_SCHEDULE_ID, schedule.get(index).ID_SCHEDULE);
            }


        }

        public static ArrayList<ScheduleModel> getSchedules(Context context, String date, int groupId, boolean isGroup) {
            boolean isCancelled;
            ArrayList<ScheduleModel> scheduleModelArrayList = new ArrayList<>();
            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();
            Cursor cursor;
            int selectionGroup, selectionTeacher;
            String nameTeacher, nameGroup;

            try {
                if (isGroup)
                    cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_SCHEDULE_DATE + " = " + date + " AND " + COL_GROUP_ID + " = " + groupId + " ORDER BY " + COL_PAIR_ID, null);
                else
                    cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_SCHEDULE_DATE + " = " + date + " AND " + COL_TEACHER_ID + " = " + groupId + " ORDER BY " + COL_PAIR_ID, null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    if (isGroup) {
                        selectionGroup = cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID));
                        selectionTeacher = cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID));
                        nameTeacher = dbHelper.getTeachersHelper().getTeacherById(context, cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID)));
                        nameGroup = dbHelper.getGroupsHelper().getGroupById(context, cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID)));
                    } else {
                        selectionGroup = cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID));
                        selectionTeacher = cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID));
                        nameTeacher = dbHelper.getGroupsHelper().getGroupById(context, cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID)));
                        nameGroup = dbHelper.getTeachersHelper().getTeacherById(context, cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID)));
                    }

                    if (cursor.getInt(cursor.getColumnIndex(COL_IS_CANCELLED)) == 1)
                        isCancelled = true;
                    else
                        isCancelled = false;

                    int idPair = cursor.getInt(cursor.getColumnIndex(COL_PAIR_ID));
                    ScheduleModel scheduleModel = new ScheduleModel(

                            cursor.getInt(cursor.getColumnIndex(COL_SCHEDULE_ID)),
                            idPair,
                            selectionGroup,
                            selectionTeacher,
                            cursor.getInt(cursor.getColumnIndex(COL_CLASSROOM_ID)),
                            cursor.getInt(cursor.getColumnIndex(COL_SUBGROUP_NUMBER)),
                            dbHelper.getPairsHelper().getPairNumber(context, idPair),
                            dbHelper.getPairsHelper().getPairTime(context, PairsHelper.COL_BEGIN_TIME, idPair),
                            dbHelper.getPairsHelper().getPairTime(context, PairsHelper.COL_END_TIME, idPair),
                            cursor.getString(cursor.getColumnIndex(COL_SCHEDULE_DATE)),
                            cursor.getString(cursor.getColumnIndex(COL_DISCIPLINE_NAME)),
                            nameTeacher,
                            nameGroup,
                            "1-250",
                            cursor.getString(cursor.getColumnIndex(COL_DISCIPLINE_TYPE)),
                            isCancelled

                    );
                    scheduleModelArrayList.add(scheduleModel);
                    cursor.moveToNext();
                }

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);
            }
            return scheduleModelArrayList;

        }

        public static void deleteSchedule(Context context, int idSchedule, boolean isGroup) {

            int isGroupDB = 0;
            if (isGroup)
                isGroupDB = 1;

            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();

            //Delete Schedule

            db.delete(dbHelper.getUsedSchedulesHelper().TABLE_NAME, dbHelper.getUsedSchedulesHelper().COL_ID_SCHEDULE + "=" + idSchedule +
                    " AND " + dbHelper.getUsedSchedulesHelper().COL_IS_GROUP + "=" + isGroupDB, null);
            if (isGroup)
                db.delete(TABLE_NAME, COL_GROUP_ID + "=" + idSchedule, null);
            else
                db.delete(TABLE_NAME, COL_TEACHER_ID + "=" + idSchedule, null);
        }


    }


    public static class UsedSchedulesHelper {

        protected static final String TABLE_NAME = "UsedSchedules";
        protected static final String COL_ID_SCHEDULE = "id_schedule";
        protected static final String COL_NAME_SCHEDULE = "name_schedule";
        protected static final String COL_IS_GROUP = "is_group";
        protected static final String COL_IS_MAIN = "is_main";
        protected static final String COL_LAST_REFRESH_DATE = "last_refresh_date";


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


        public static void setUsedSchedule(Context context, int groupid, boolean isGroup, boolean isMain, String lastRefresh) {

            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            ContentValues scheduleRow = new ContentValues();
            scheduleRow.put(COL_ID_SCHEDULE, groupid);
            if (isGroup)
                scheduleRow.put(COL_NAME_SCHEDULE, DBRequest.getUserGroup(groupid, context));
            else
                scheduleRow.put(COL_NAME_SCHEDULE, DBHelper.getInstance(context).getTeachersHelper().getTeacherById(context, groupid));
            scheduleRow.put(COL_IS_GROUP, isGroup);
            scheduleRow.put(COL_IS_MAIN, isMain);
            scheduleRow.put(COL_LAST_REFRESH_DATE, lastRefresh);
            db.insert(TABLE_NAME, null, scheduleRow);

        }

        public static void deleteMainSchedule(Context context) {
            int isMainDB = 1;

            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();
            db.delete(TABLE_NAME, COL_IS_MAIN + "=" + isMainDB, null);
        }

        public static void deleteUsedSchedule(Context context, int groupId, boolean isGroup) {
            int isGroupDB = 0;
            if (isGroup)
                isGroupDB = 1;
            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();
            db.delete(TABLE_NAME, COL_ID_SCHEDULE + "=" + groupId + " AND " + COL_IS_GROUP + " = " + isGroupDB, null);
            //DBRequest.checkIsDataAlreadyInDBorNot(context,SchedulesHelper.TABLE_NAME,SchedulesHelper.COL_GROUP_ID,groupId,)
        }

        public static GroupsModel getMainGroupModel(Context context) {

            GroupsModel groupsModelMain = null;
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();

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
            db = dbHelper.getReadableDatabase();

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

        public static void updateRefreshDate(Context context, int idSchedule, boolean isGroup, String newLastRefresh) {

            int isGroupDB = 0;
            if (isGroup)
                isGroupDB = 1;
            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(COL_LAST_REFRESH_DATE, newLastRefresh);
            db.update(TABLE_NAME, cv, COL_ID_SCHEDULE + "=" + idSchedule + " AND " + COL_IS_GROUP + "=" + isGroupDB, null);
        }
    }

    public class ScheduleDatesHelper {
        protected static final String TABLE_NAME = "ScheduleDates";
        protected static final String COL_SCHEDULE_ID = "schedule_id";
        protected static final String COL_DATE = "date";
        protected static final String COL_IS_CANCELLED = "is_cancelled";

        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_SCHEDULE_ID + " INTEGER," +
                    COL_DATE + " TEXT," +
                    COL_IS_CANCELLED + " INTEGER" +
                    ");");

        }

        public void getScheduleDates(Context context, ArrayList<ScheduleDates> scheduleDates) {
        }

        public void setScheduleDates(Context context, ArrayList<ScheduleDates> scheduleDates) {
            SQLiteDatabase sqliteDatabase;
            DBHelper helper = DBHelper.getInstance(context);
            sqliteDatabase = helper.getWritableDatabase();

            for (int i = 0; i < scheduleDates.size(); i++) {
                for (int j = 0; j < scheduleDates.get(j).DATES.size(); j++) {
                    ContentValues scheduleDatesRow = new ContentValues();
                    scheduleDatesRow.put(COL_SCHEDULE_ID, scheduleDates.get(i).ID_SCHEDULE);
                    scheduleDatesRow.put(COL_DATE, scheduleDates.get(j).DATES.get(j).DATE);
                    scheduleDatesRow.put(COL_IS_CANCELLED, scheduleDates.get(j).DATES.get(j).IS_CANCELED);
                    sqliteDatabase.insert(TABLE_NAME, null, scheduleDatesRow);
                }
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}