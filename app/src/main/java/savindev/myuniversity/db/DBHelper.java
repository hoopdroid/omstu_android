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
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.notes.NoteModel;
import savindev.myuniversity.notes.Priority;
import savindev.myuniversity.schedule.DateUtil;
import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.schedule.ScheduleModel;
import savindev.myuniversity.serverTasks.Schedule;
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
    private CampusesHelper campusesHelper;
    private ClassroomsHelper classroomsHelper;
    private BuildingsHelper buildingsHelper;
    private NotesHelper notesHelper;

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
        campusesHelper = new CampusesHelper();
        classroomsHelper = new ClassroomsHelper();
        buildingsHelper = new BuildingsHelper();
        notesHelper = new NotesHelper();

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
        campusesHelper.create(db);
        classroomsHelper.create(db);
        buildingsHelper.create(db);
        notesHelper.create(db);
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

    public CampusesHelper getCampusesHelper() {
        return campusesHelper;
    }

    public ClassroomsHelper getClassroomsHelper() {
        return classroomsHelper;
    }

    public BuildingsHelper getBuildingsHelper() {
        return buildingsHelper;
    }

    public NotesHelper getNotesHelper() {
        return notesHelper;
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

        public int getDaysInWeek() {

            int daysinweek = 0;

            SQLiteDatabase sqliteDatabase = getReadableDatabase();
            Cursor cursor;

            cursor = sqliteDatabase.rawQuery("SELECT " + COL_DAYS_IN_WEEK + " FROM " + TABLE_NAME, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                daysinweek = cursor.getInt(cursor.getColumnIndex(COL_DAYS_IN_WEEK));

                cursor.moveToNext();

            }

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

        public int getPairsInDay(Context context){
            int maxPair=0;

            SQLiteDatabase sqLiteDatabase = getReadableDatabase();

            try {
                String selectQuery = "SELECT " + COL_PAIR_NUMBER + " FROM " + TABLE_NAME;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);
                if (c.moveToLast()) {
                    maxPair = c.getInt(c.getColumnIndex(COL_PAIR_NUMBER));
                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }

            return maxPair;
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
        public static final String COL_ID = "_id";
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
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
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


        public Set<String> getGroupLessons(Context context,int idGroup,boolean isGroup){

            String selectionDB=COL_GROUP_ID;
            if(!isGroup){
                selectionDB=COL_TEACHER_ID;
            }
            SortedSet<String> lessons = new TreeSet<>();
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            try {
                String selectQuery = "SELECT " + COL_DISCIPLINE_NAME + " FROM " + TABLE_NAME+" WHERE "+selectionDB+" = "+idGroup;
                Cursor c = db.rawQuery(selectQuery, null);
                c.moveToFirst();
                while (!c.isAfterLast()){

                    lessons.add(c.getString(c.getColumnIndex(COL_DISCIPLINE_NAME)));
                    c.moveToNext();

                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }

            return lessons;
        }

        public Set<String> getGroupLessonsTypes(Context context,int idGroup,boolean isGroup){

            String selectionDB=COL_GROUP_ID;
            if(!isGroup){
                selectionDB=COL_TEACHER_ID;
            }
            SortedSet<String> lessons = new TreeSet<>();
            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            try {
                String selectQuery = "SELECT " + COL_DISCIPLINE_TYPE + " FROM " + TABLE_NAME+" WHERE "+selectionDB+" = "+idGroup;
                Cursor c = db.rawQuery(selectQuery, null);
                c.moveToFirst();
                while (!c.isAfterLast()){

                    lessons.add(c.getString(c.getColumnIndex(COL_DISCIPLINE_TYPE)));
                    c.moveToNext();

                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }

            return lessons;
        }

        public void setSchedule(Context context, ArrayList<Schedule> schedule) {

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String previousValue = "";
            Date beginDate = null;
            Date endDate = null;
            boolean firstPair;

            for (int index = 0; index < schedule.size(); index++) {
                if (!schedule.get(index).IS_DELETED) {
                    //TODO test all variants of work dataalreadyornot function)) {
                    try {
                        firstPair = true;
                        beginDate = format.parse(schedule.get(index).SCHEDULE_FIRST_DATE);//дата начала пары
                        endDate = format.parse(DateUtil.formatStandart(helper.getSemestersHelper().getSemesterEndDate(context, 1))); //конец семестра
                        previousValue = schedule.get(index).SCHEDULE_FIRST_DATE;
                        while (beginDate.compareTo(endDate) <= 0 && (schedule.get(index).SCHEDULE_INTERVAL > 0 || firstPair)) { // если дата начала пары раньше конца семестра
                            firstPair = false;
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
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            Date beginDate = null;
            Date endDate = null;






            try {
                beginDate = format.parse(date);
                endDate = format.parse(DateUtil.formatStandart(dbHelper.getSemestersHelper().getSemesterEndDate(context, 1))); //конец семестра
                if(beginDate.compareTo(endDate) >= 0)
                    return null;

            }
            catch (ParseException e) {
                e.printStackTrace();
            }

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

                    List<ScheduleModel.Pair> list = new ArrayList<>();

                    ScheduleModel.Pair pair = new ScheduleModel.Pair(
                            cursor.getInt(cursor.getColumnIndex(COL_SCHEDULE_ID)),
                            idPair,
                            selectionGroup,
                            selectionTeacher,
                            cursor.getColumnIndex(COL_CLASSROOM_ID),
                            cursor.getInt(cursor.getColumnIndex(COL_SUBGROUP_NUMBER)),
                            cursor.getString(cursor.getColumnIndex(COL_DISCIPLINE_NAME)),
                            nameTeacher,
                            nameGroup,
                            dbHelper.getClassroomsHelper().getClassroom(context,cursor.getInt(cursor.getColumnIndex(COL_CLASSROOM_ID))),
                            cursor.getString(cursor.getColumnIndex(COL_DISCIPLINE_TYPE)),
                            isCancelled
                    );
                    list.add(pair);


                    ScheduleModel scheduleModel = new ScheduleModel(

                            dbHelper.getPairsHelper().getPairNumber(context, idPair),
                            dbHelper.getPairsHelper().getPairTime(context, PairsHelper.COL_BEGIN_TIME, idPair),
                            dbHelper.getPairsHelper().getPairTime(context, PairsHelper.COL_END_TIME, idPair),
                            cursor.getString(cursor.getColumnIndex(COL_SCHEDULE_DATE)),
                            isCancelled,
                            list,
                            dbHelper.getNotesHelper().getPairNotes(
                                    cursor.getInt(cursor.getColumnIndex(COL_SCHEDULE_ID)),date)
                    );

                    scheduleModelArrayList.add(scheduleModel);
                    cursor.moveToNext();
                }
                int a =5;
            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);
            }
            return scheduleModelArrayList;

        }

        public static void deleteGroupSchedule(Context context, int idSchedule) {


            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();

            ArrayList<Integer> idList = UsedSchedulesHelper.getIdSchedules(context,true);


            String tempselect=idList.get(0).toString() +",";
            String select="";

            for(int i =0 ; i<idList.size();i++) {
                select += tempselect + idList.get(i).toString()+",";
                tempselect =  idList.get(i).toString()+",";
            }

            select = select.substring(0, select.length()-1);

            Log.d("SELECT",select);

            db.delete(dbHelper.getUsedSchedulesHelper().TABLE_NAME, dbHelper.getUsedSchedulesHelper().COL_ID_SCHEDULE + "=" + idSchedule +
                    " AND " + dbHelper.getUsedSchedulesHelper().COL_IS_GROUP + "=" + 1, null);

            db.delete(TABLE_NAME, COL_GROUP_ID + " IN ("+idSchedule+") " +" AND " + COL_TEACHER_ID +" NOT IN ("+select+")", null);
        }

        public static void deleteTeacherchedule(Context context, int idSchedule) {


            SQLiteDatabase db;
            DBHelper dbHelper = DBHelper.getInstance(context);
            db = dbHelper.getWritableDatabase();

            ArrayList<Integer> idList = UsedSchedulesHelper.getIdSchedules(context,false);


            String tempselect=idList.get(0).toString() +",";
            String select="";

            for(int i =0 ; i<idList.size();i++) {
                select += tempselect + idList.get(i).toString()+",";
                tempselect =  idList.get(i).toString()+",";
            }

            select = select.substring(0, select.length()-1);

            Log.d("SELECT",select);




            for(int i=0;i<idList.size();i++){


                db.delete(dbHelper.getUsedSchedulesHelper().TABLE_NAME, dbHelper.getUsedSchedulesHelper().COL_ID_SCHEDULE + "=" + idSchedule +
                        " AND " + dbHelper.getUsedSchedulesHelper().COL_IS_GROUP + "=" + 0, null);

                db.delete(TABLE_NAME, COL_TEACHER_ID + " IN ("+idSchedule+") " +" AND " + COL_GROUP_ID +" NOT IN ("+select+")", null);
            }


            //clearDB(context,COL_TEACHER_ID,true);


        }



        public static boolean equalsTeachAndGroups(Context context,DBHelper dbHelper,SQLiteDatabase database,String TableName,
                                                   String dbfieldGroup, int fieldIdGroup,String dbfieldTeacher,int fieldIdTeacher) {

            String Query = "SELECT * FROM " + TableName + " WHERE " + dbfieldGroup + " = " + fieldIdGroup+" AND "+ dbfieldTeacher + "="+fieldIdTeacher;
            Cursor cursor = database.rawQuery(Query, null);
            if(cursor.getCount() <= 0){
                cursor.close();
                return false;
            }
            cursor.close();

            return true;
        }

        public static  void clearDB(Context context,String selection,boolean isGroup) {
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            ArrayList<Integer> idList = UsedSchedulesHelper.getIdSchedules(context,isGroup);
            for (int i = 0 ;i<idList.size();i++)
                db.delete(TABLE_NAME, selection +"!="+idList.get(i), null);

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
            int isGroupDB = 0;
            if(isGroup)
                isGroupDB=1;
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
            if (!DBRequest.checkIsDataAlreadyInDBorNot(context, TABLE_NAME, COL_ID_SCHEDULE, groupid))
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

        public static ArrayList<Integer> getIdSchedules(Context context,boolean isGroup) {
            int isGroupDB =0;
            if(isGroup)isGroupDB=1;
            //TODO Сделать добавление правильных айди,сейчас проверяется по всем в UsedSchedules,но это не критично
            ArrayList<Integer> idList = new ArrayList<>();

            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getReadableDatabase();

            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT "+COL_ID_SCHEDULE+" FROM " + TABLE_NAME, null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {


                    idList.add(cursor.getInt(cursor.getColumnIndex(COL_ID_SCHEDULE)));
                    cursor.moveToNext();
                }

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);
            }

            return idList;
        }

    }

    public static class CampusesHelper {

        protected static final String TABLE_NAME = "Campuses";
        protected static final String COL_ID_CAMPUS = "campus_id";
        protected static final String COL_CAMPUS_NAME = "campus_name";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_CAMPUS + " INTEGER PRIMARY KEY," +
                    COL_CAMPUS_NAME + " TEXT" +
                    ");");
        }

        public  void setCampuses(UniversityInfo init, Context context) {
            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            for (int index = 0; index < init.CAMPUSES.size(); index++) {
                if (!init.CAMPUSES.get(index).IS_DELETED) {
                    ContentValues campusesRow = new ContentValues();
                    campusesRow.put(COL_ID_CAMPUS, init.CAMPUSES.get(index).ID_CAMPUS);
                    campusesRow.put(COL_CAMPUS_NAME, init.CAMPUSES.get(index).CAMPUS_NAME);
                    sqliteDatabase.insert(TABLE_NAME, null, campusesRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_ID_CAMPUS, init.CAMPUSES.get(index).ID_CAMPUS);
                }
            }
        }
    }


    public static class ClassroomsHelper {

        protected static final String TABLE_NAME = "Classrooms";
        protected static final String COL_ID_CLASSROOM = "classroom_id";
        protected static final String COL_ID_BUILDING = "building_id";
        protected static final String COL_CLASSROOM_FLOOR = "classroom_floor";
        protected static final String COL_CLASSROOM_NAME = "classroom_name";
        protected static final String COL_CLASSROOM_FULLNAME = "classroom_fullname";
        protected static final String COL_CLASSROOM_TYPE = "classroom_type";
        protected static final String COL_CLASSROOM_DESCRIPTION = "classroom_description";

        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_CLASSROOM + " INTEGER PRIMARY KEY," +
                    COL_ID_BUILDING + " INTEGER," +
                    COL_CLASSROOM_NAME + " TEXT," +
                    COL_CLASSROOM_FULLNAME + " TEXT," +
                    COL_CLASSROOM_FLOOR + " INTEGER," +
                    COL_CLASSROOM_DESCRIPTION + " TEXT," +
                    COL_CLASSROOM_TYPE + " INTEGER" +
                    ");");
        }

        public  void setClassrooms(UniversityInfo init, Context context) {
            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            for (int index = 0; index < init.CLASSROOMS.size(); index++) {
                if (!init.CLASSROOMS.get(index).IS_DELETED) {
                    ContentValues classroomsRow = new ContentValues();
                    classroomsRow.put(COL_ID_CLASSROOM, init.CLASSROOMS.get(index).ID_CLASSROOM);
                    classroomsRow.put(COL_ID_BUILDING, init.CLASSROOMS.get(index).ID_BUILDING);
                    classroomsRow.put(COL_CLASSROOM_NAME, init.CLASSROOMS.get(index).CLASSROOM_NAME);
                    classroomsRow.put(COL_CLASSROOM_FULLNAME, init.CLASSROOMS.get(index).CLASSROOM_FULLNAME);
                    classroomsRow.put(COL_CLASSROOM_FLOOR, init.CLASSROOMS.get(index).CLASSROOM_FLOOR);
                    classroomsRow.put(COL_CLASSROOM_DESCRIPTION, init.CLASSROOMS.get(index).CLASSROOM_DESCRIPTION);
                    classroomsRow.put(COL_CLASSROOM_TYPE, init.CLASSROOMS.get(index).CLASSROOM_TYPE_NAME);
                    sqliteDatabase.insert(TABLE_NAME, null, classroomsRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_ID_CLASSROOM, init.CLASSROOMS.get(index).ID_CLASSROOM);
                }
            }
        }

        public String getClassroom(Context context,int classroomId){
            String classroomName="330";
            int buildingName=0;
            int buildingID=0;

            DBHelper dbHelper = new DBHelper(context);
            SQLiteDatabase  db = dbHelper.getReadableDatabase();

            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+COL_ID_CLASSROOM+" = "+classroomId, null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    classroomName = cursor.getString(cursor.getColumnIndex(COL_CLASSROOM_NAME));
                    buildingID = cursor.getInt(cursor.getColumnIndex(COL_ID_BUILDING));
                    cursor.moveToNext();
                }

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);

            }

            try {
                cursor = db.rawQuery("SELECT "+BuildingsHelper.COL_BUILDING_NUMBER+" FROM " + BuildingsHelper.TABLE_NAME + " WHERE "+BuildingsHelper.COL_ID_BUILDING+" = "+buildingID, null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    buildingName = cursor.getInt(cursor.getColumnIndex(BuildingsHelper.COL_BUILDING_NUMBER));

                    cursor.moveToNext();
                }

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);

            }

            if(classroomName == null || classroomName.equals("null")){
                classroomName =  context.getResources().getString(R.string.building);
            }

            return Integer.toString(buildingName) + "-"+classroomName;
        }

    }
    public static class BuildingsHelper {

        protected static final String TABLE_NAME = "Buildings";
        protected static final String COL_ID_BUILDING = "building_id";
        protected static final String COL_ID_CAMPUS = "campus_id";
        protected static final String COL_BUILDING_NUMBER = "building_number";
        protected static final String COL_BUILDING_NAME = "building_name";
        protected static final String COL_BUILDING_TYPE_NAME = "building_type_name";
        protected static final String COL_BUILDING_FLOOR_COUNT = "floor_count";
        protected static final String COL_HAS_GROUND_FLOOR = "has_ground_floor";


        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_BUILDING + " INTEGER PRIMARY KEY," +
                    COL_ID_CAMPUS + " INTEGER," +
                    COL_BUILDING_NUMBER + " INTEGER," +
                    COL_BUILDING_NAME + " TEXT," +
                    COL_BUILDING_TYPE_NAME + " TEXT," +
                    COL_BUILDING_FLOOR_COUNT + " INTEGER," +
                    COL_HAS_GROUND_FLOOR + " INTEGER" +
                    ");");
        }

        public  void setBuildings(UniversityInfo init, Context context) {
            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            for (int index = 0; index < init.BUILDINGS.size(); index++) {
                if (!init.BUILDINGS.get(index).IS_DELETED) {
                    ContentValues buildingsRow = new ContentValues();
                    buildingsRow.put(COL_ID_BUILDING, init.BUILDINGS.get(index).ID_BUILDING);
                    buildingsRow.put(COL_ID_CAMPUS, init.BUILDINGS.get(index).ID_CAMPUS);
                    buildingsRow.put(COL_BUILDING_NUMBER, init.BUILDINGS.get(index).BUILDING_NUMBER);
                    buildingsRow.put(COL_BUILDING_NAME, init.BUILDINGS.get(index).BUILDING_NAME);
                    buildingsRow.put(COL_BUILDING_TYPE_NAME, init.BUILDINGS.get(index).BUILDING_TYPE_NAME);
                    buildingsRow.put(COL_BUILDING_FLOOR_COUNT, init.BUILDINGS.get(index).BUILDING_FLOOR_COUNT);
                    buildingsRow.put(COL_HAS_GROUND_FLOOR, init.BUILDINGS.get(index).HAS_GROUND_FLOOR);
                    sqliteDatabase.insert(TABLE_NAME, null, buildingsRow);
                } else {
                    DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_ID_BUILDING, init.BUILDINGS.get(index).ID_BUILDING);
                }
            }
        }
    }

    public class NotesHelper {
        //TODO Поясни мне работу с enum приоритетом и типом
        protected static final String TABLE_NAME = "Notes";
        protected static final String COL_ID_NOTE = "note_id";
        protected static final String COL_NOTE_NAME = "note_name";
        protected static final String COL_SENDER_NAME = "sender_name";
        protected static final String COL_IS_DONE= "is_done";
        protected static final String COL_PRIORITY = "priority";
        protected static final String COL_PHOTO_LINK = "photo_link";
        protected static final String COL_TYPE_PAIR = "pair_type";
        protected static final String COL_TYPE_TIME = "pair_time";
        protected static final String COL_TYPE_REPEAT = "pair_repeat";
        protected static final String COL_TYPE_DATE = "pair_date";
        protected static final String COL_NOTE_TEXT = "note_text";
        protected static final String COL_NOTE_DATE = "note_date";
        protected static final String COL_PAIR_ID = "pair_id";
        protected static final String COL_NOTE_TIME = "time";
        protected static final String COL_REPEAT_TIME = "repeat_time";
        protected static final String COL_ACCESS = "pair_access";

        public void create(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                    COL_ID_NOTE + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    COL_NOTE_NAME + " TEXT," +
                    COL_SENDER_NAME + " TEXT," +
                    COL_IS_DONE + " INTEGER," +
                    COL_PRIORITY + " TEXT," +
                    COL_PHOTO_LINK + " TEXT," +
                    COL_TYPE_PAIR + " INTEGER," +
                    COL_TYPE_TIME + " INTEGER," +
                    COL_TYPE_REPEAT + " INTEGER," +
                    COL_TYPE_DATE + " INTEGER," +
                    COL_NOTE_TEXT + " TEXT," +
                    COL_NOTE_DATE + " TEXT," +
                    COL_PAIR_ID + " TEXT," +//Уникальный идентификатор pairID + Date из Schedules
                    COL_NOTE_TIME + " TEXT," +
                    COL_REPEAT_TIME + " TEXT," +
                    COL_ACCESS + " TEXT" +
                    ");");
        }

        public void setPairNote( NoteModel noteModel ) {


            SQLiteDatabase sqliteDatabase = getWritableDatabase();

            ContentValues noteRow = new ContentValues();
            noteRow.put(COL_NOTE_NAME, noteModel.getName());
            noteRow.put(COL_SENDER_NAME, "UserName");
            noteRow.put(COL_IS_DONE, 0);
            noteRow.put(COL_PRIORITY,noteModel.getPriority().toString());
            //TODO   noteRow.put(COL_PRIORITY, noteModel.getPriority().toString());//как тут поступать?
            noteRow.put(COL_TYPE_PAIR, 1);
            noteRow.put(COL_NOTE_TEXT, noteModel.getText());
            noteRow.put(COL_PAIR_ID, noteModel.getPairId());
            noteRow.put(COL_NOTE_DATE, noteModel.getDate());

            sqliteDatabase.insert(TABLE_NAME, null, noteRow);

        }





        public ArrayList<NoteModel> getAllNotes() {
            ArrayList<NoteModel> noteModelArrayList = new ArrayList<>();

            SQLiteDatabase db;

            db = getReadableDatabase();

            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME+" ORDER BY "+ COL_NOTE_DATE + " IS NOT NULL", null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    NoteModel noteModel = new NoteModel(
                            cursor.getInt(cursor.getColumnIndex(COL_ID_NOTE)),
                            cursor.getString(cursor.getColumnIndex(COL_NOTE_NAME)),
                            null,
                            cursor.getInt(cursor.getColumnIndex(COL_IS_DONE)),
                            Priority.fromString(cursor.getString(cursor.getColumnIndex(COL_PRIORITY))),
                            null,
                            null,
                            cursor.getString(cursor.getColumnIndex(COL_NOTE_TEXT)),
                            cursor.getString(cursor.getColumnIndex(COL_NOTE_DATE)),
                            null,
                            null
                    );
                    noteModelArrayList.add(noteModel);
                    cursor.moveToNext();

                }
                cursor.close();
            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);
            }

            return  noteModelArrayList;

        }


        public ArrayList<NoteModel> getPairNotes(int scheduleId,String scheduleDate) {

            ArrayList<NoteModel> noteModelArrayList = new ArrayList<>();

            SQLiteDatabase db;

            String pairId = Integer.toString(scheduleId)+scheduleDate;
            db = getReadableDatabase();

            Cursor cursor;
            try {
                cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +COL_PAIR_ID + " = "+ pairId, null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    NoteModel noteModel = new NoteModel(
                            cursor.getInt(cursor.getColumnIndex(COL_ID_NOTE)),
                            cursor.getString(cursor.getColumnIndex(COL_NOTE_NAME)),
                            cursor.getString(cursor.getColumnIndex(COL_SENDER_NAME)),
                            cursor.getInt(cursor.getColumnIndex(COL_IS_DONE)),
                            Priority.fromString(cursor.getString(cursor.getColumnIndex(COL_PRIORITY))),
                            null,
                            null,
                            cursor.getString(cursor.getColumnIndex(COL_NOTE_TEXT)),
                            cursor.getString(cursor.getColumnIndex(COL_NOTE_DATE)),
                            cursor.getString(cursor.getColumnIndex(COL_PAIR_ID)),
                            null
                    );
                    noteModelArrayList.add(noteModel);
                    cursor.moveToNext();
                }
                cursor.close();

            } catch (SQLiteException e) {
                Log.e("SQLITE DB EXCEPTION", e.toString(), e);
            }

            return  noteModelArrayList;

        }

        public void deleteNote(int idNote){
            SQLiteDatabase db;
            db = getWritableDatabase();
            db.delete(TABLE_NAME, COL_ID_NOTE + "=" + idNote, null);
        }

        public void setNoteIsDone(int idNote){
            SQLiteDatabase db;
            db = getWritableDatabase();
            ContentValues isDoneValue = new ContentValues();
            isDoneValue.put(COL_IS_DONE, 1);

            db.update(TABLE_NAME, isDoneValue,COL_ID_NOTE+ " = "+idNote, null);

        }




    }


    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

}