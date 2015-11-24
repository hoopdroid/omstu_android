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
    private SchedulesHelper schedulesHelper;

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
        schedulesHelper.create(db);
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
            int teacher_id = DBRequest.getIdFromString(context, DepartmentsHelper.TABLE_NAME, selection, DepartmentsHelper.COL_DEPARTMENT_FULLNAME, department);
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

            ArrayList teacherIdList = DBRequest.getList(context, TABLE_NAME, COL_ID_TEACHER, COL_ID_DEPARTMENT, teacher_id,COL_TEACHER_LASTNAME);
            ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();

            for (int i = 0; i < teachersNameList.size(); i++) {
                GroupsModel groupsModel = new GroupsModel(
                        teachersNameList.get(i),
                        Integer.parseInt(teacherIdList.get(i).toString()),
                        false
                );
                groupsModelArrayList.add(groupsModel);
            }
            int a =5 ;
            return groupsModelArrayList;
        }

        public String getTeacherById(Context context,int teacher_id){
            String teacherName = "";

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

            try {
                String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID_TEACHER + "="+teacher_id;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);
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

            return  teacherName;
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

            return DBRequest.getList(context, table, selection, null, 0,selection);

        }

        public String getSemesterEndDate(Context context,int idSemester){
            String endDate = "";

            DBHelper dbHelper = DBHelper.getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
            String find = "SELECT "+COL_END_DATE+" FROM  " + TABLE_NAME + " WHERE " + COL_ID_SEMESTER + " = " + idSemester;
            Cursor cursor = sqLiteDatabase.rawQuery(find, null);

            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {
                endDate = cursor.getString(cursor.getColumnIndex(COL_END_DATE));
                cursor.moveToNext();
            }
            cursor.close();
            return  endDate;
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

        public String getPairNumber(Context context,int pairId){

            int number =0;

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

            try {
                String selectQuery = "SELECT " + COL_PAIR_NUMBER + " FROM " + TABLE_NAME+ " WHERE " + COL_ID_PAIR + "="+pairId;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);
                if (c.moveToFirst()) {
                    number = c.getInt(c.getColumnIndex(COL_PAIR_NUMBER));
                }
                c.close();
            } catch (SQLiteException e) {
                Log.e("DB EXCEPTION", e.toString(), e);
            }

            return Integer.toString(number);
        }

        public String getPairTime(Context context,String selectionTime,int pairId){
            String time = "";

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();

            try {
                String selectQuery = "SELECT " + selectionTime + " FROM " + TABLE_NAME+ " WHERE " + COL_ID_PAIR + "="+pairId;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);
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
            int group_id = DBRequest.getIdFromString(context, FacultiesHelper.TABLE_NAME, selection, FacultiesHelper.COL_FACULTY_SHORTNAME, faculty);
            ArrayList<String> groupNameList = DBRequest.getList(context, TABLE_NAME, COL_GROUP_NAME, COL_ID_FACULTY, group_id, COL_GROUP_NAME);
            ArrayList groupIdList = DBRequest.getList(context, TABLE_NAME, COL_ID_GROUP, COL_ID_FACULTY, group_id,COL_GROUP_NAME);
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

        public String getGroupById(Context context,int groupId){
            String groupName = "";

            DBHelper dbHelper = getInstance(context);
            SQLiteDatabase sqLiteDatabase = dbHelper.getWritableDatabase();
            try {
                String selectQuery = "SELECT "+COL_GROUP_NAME+ " FROM " + TABLE_NAME + " WHERE " + COL_ID_GROUP + "="+groupId;
                Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);
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

            return DBRequest.getList(context, table, selection);

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
                    COL_SCHEDULE_DATE+ " TEXT," +
                    COL_CLASSROOM_ID+ " INTEGER," +
                    COL_SUBGROUP_NUMBER + " INTEGER," +
                    COL_IS_CANCELLED + " INTEGER" +
                    ");");
        }

        public String dateFormat(ArrayList<Schedule> schedule,int index){
            String dt = schedule.get(0).SCHEDULE_FIRST_DATE;
            Log.d("BEFORE",dt);// Start date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Calendar c = Calendar.getInstance();
            try {
                c.setTime(sdf.parse(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DATE, schedule.get(index).SCHEDULE_INTERVAL);  // number of days to add
            dt = sdf.format(c.getTime());  // dt is now the new date
            Log.d("AFTER",dt);
            return  dt;
        }

        public void setSchedule(Context context,ArrayList<Schedule> schedule) {

            SQLiteDatabase sqliteDatabase;
            DBHelper helper = new DBHelper(context);
            sqliteDatabase = helper.getWritableDatabase();

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String previousValue="";
            Date beginDate = null;
            Date endDate=null;

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

                            if(!DBRequest.checkIsDataAlreadyInDBorNot(context,TABLE_NAME,COL_SCHEDULE_ID,schedule.get(index).ID_SCHEDULE,COL_SCHEDULE_DATE,previousValue))
                                sqliteDatabase.insert(TABLE_NAME, null, scheduleRow);
                            else break;
                            beginDate = format.parse(previousValue);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }


                else
                    DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_SCHEDULE_ID, schedule.get(index).ID_SCHEDULE);
            }


        }

        public static ArrayList<ScheduleModel> getSchedules(Context context,String date,int groupId ,boolean isGroup){
            boolean isCancelled;
            ArrayList<ScheduleModel> scheduleModelArrayList = new ArrayList<>();
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            Cursor cursor;
            int selectionGroup,selectionTeacher;

            try {

                cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME  + " WHERE " + COL_SCHEDULE_DATE + " = " + date + " AND " + COL_GROUP_ID + " = "+ groupId +" ORDER BY " +COL_PAIR_ID, null);
                cursor.moveToFirst();

                while (!cursor.isAfterLast()) {

                    if (isGroup){
                        selectionGroup =  cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID));
                        selectionTeacher =cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID));}
                    else {
                        selectionGroup = cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID));
                        selectionTeacher = cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID));}

                            if(cursor.getInt(cursor.getColumnIndex(COL_IS_CANCELLED))==1)
                                isCancelled = true;
                            else
                                isCancelled = false;


                            ScheduleModel scheduleModel = new ScheduleModel(

                            cursor.getInt(cursor.getColumnIndex(COL_SCHEDULE_ID)),
                            cursor.getInt(cursor.getColumnIndex(COL_PAIR_ID)),
                            selectionGroup,
                            selectionTeacher,
                            cursor.getInt(cursor.getColumnIndex(COL_CLASSROOM_ID)),
                            cursor.getInt(cursor.getColumnIndex(COL_SUBGROUP_NUMBER)),
                            dbHelper.getPairsHelper().getPairNumber(context,cursor.getColumnIndex(COL_PAIR_ID)),
                            dbHelper.getPairsHelper().getPairTime(context, PairsHelper.COL_BEGIN_TIME,cursor.getInt(cursor.getColumnIndex(COL_PAIR_ID))),
                            dbHelper.getPairsHelper().getPairTime(context, PairsHelper.COL_END_TIME, cursor.getInt(cursor.getColumnIndex(COL_PAIR_ID))),
                            cursor.getString(cursor.getColumnIndex(COL_SCHEDULE_DATE)), cursor.getString(cursor.getColumnIndex(COL_DISCIPLINE_NAME)),
                            dbHelper.getTeachersHelper().getTeacherById(context,cursor.getInt(cursor.getColumnIndex(COL_TEACHER_ID))),
                            dbHelper.getGroupsHelper().getGroupById(context,cursor.getInt(cursor.getColumnIndex(COL_GROUP_ID))),
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
            int a =5 ;
            return scheduleModelArrayList;

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


        public static void setUsedSchedule(Context context, int groupid, boolean isGroup, boolean isMain, String lastRefresh) {


           int isGroupDB = 0, isMainDB = 0;
           if (isGroup)
               isGroupDB = 1;
           if (isMain)
               isMainDB = 1;


            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            ContentValues scheduleRow = new ContentValues();
            scheduleRow.put(COL_ID_SCHEDULE, groupid);
            scheduleRow.put(COL_NAME_SCHEDULE, DBRequest.getUserGroup(groupid, context));
            scheduleRow.put(COL_IS_GROUP, isGroupDB);
            scheduleRow.put(COL_IS_MAIN, isMainDB);
            scheduleRow.put(COL_LAST_REFRESH_DATE, lastRefresh);
            db.insert(TABLE_NAME, null, scheduleRow);

        }

        public static void deleteUsedSchedule(Context context, int id) {
            SQLiteDatabase db;
            DBHelper dbHelper = new DBHelper(context);
            db = dbHelper.getWritableDatabase();
            DBRequest.delete_byID(db, TABLE_NAME, COL_ID_SCHEDULE, id);
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


}