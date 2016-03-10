package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
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

import savindev.myuniversity.schedule.DateUtil;
import savindev.myuniversity.serverTasks.Schedule;

/**
 * Скачанные учебные расписания пользователя
 */
public class SchedulesHelper {

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
        String dt = schedule.get(0).getSCHEDULE_FIRST_DATE();
        Log.d("BEFORE", dt);// Start date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(dt));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.add(Calendar.DATE, schedule.get(index).getSCHEDULE_INTERVAL());  // number of days to add
        dt = sdf.format(c.getTime());  // dt is now the new date
        Log.d("AFTER", dt);
        return dt;
    }


    public Set<String> getGroupLessons(Context context, int idGroup, boolean isGroup) {

        String selectionDB = COL_GROUP_ID;
        if (!isGroup) {
            selectionDB = COL_TEACHER_ID;
        }
        SortedSet<String> lessons = new TreeSet<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String selectQuery = "SELECT " + COL_DISCIPLINE_NAME + " FROM " + TABLE_NAME + " WHERE " + selectionDB + " = " + idGroup;
            Cursor c = db.rawQuery(selectQuery, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {

                lessons.add(c.getString(c.getColumnIndex(COL_DISCIPLINE_NAME)));
                c.moveToNext();

            }
            c.close();
        } catch (SQLiteException e) {
            Log.e("DB EXCEPTION", e.toString(), e);
        }

        return lessons;
    }

    public Set<String> getGroupLessonsTypes(Context context, int idGroup, boolean isGroup) {

        String selectionDB = COL_GROUP_ID;
        if (!isGroup) {
            selectionDB = COL_TEACHER_ID;
        }
        SortedSet<String> lessons = new TreeSet<>();
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {
            String selectQuery = "SELECT " + COL_DISCIPLINE_TYPE + " FROM " + TABLE_NAME + " WHERE " + selectionDB + " = " + idGroup;
            Cursor c = db.rawQuery(selectQuery, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {

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

        /*

        Переход на SQLite Statement ускоряет insert в базу в 2 раза
        по сравнению с ContentValues

         */

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_SCHEDULE_ID + ", " + COL_PAIR_ID + ", " +
                COL_GROUP_ID + ", " + COL_TEACHER_ID + ", " + COL_DISCIPLINE_NAME + ", " +
                COL_DISCIPLINE_TYPE + ", " + COL_SCHEDULE_DATE + ", " +
                COL_CLASSROOM_ID + ", " + COL_SUBGROUP_NUMBER + ", " +
                COL_IS_CANCELLED + ")" + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        String previousValue = "";
        Date beginDate = null;
        Date endDate = null;
        boolean firstPair;

        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);

        for (int index = 0; index < schedule.size(); index++) {
            if (!schedule.get(index).getIS_DELETED()) {

                try {
                    firstPair = true;
                    previousValue = schedule.get(index).getSCHEDULE_FIRST_DATE();
                    beginDate = format.parse(schedule.get(index).getSCHEDULE_FIRST_DATE());//дата начала пары
                    endDate = format.parse(DateUtil.formatStandart(helper.getSemestersHelper().getSemesterEndDate(context, previousValue))); //конец семестра
                    int j = 0;
                    while (beginDate.compareTo(endDate) <= 0 && (schedule.get(index).getSCHEDULE_INTERVAL() > 0 || firstPair)) { // если дата начала пары раньше конца семестра

                        firstPair = false;

                        stmt.bindLong(1, schedule.get(index).getID_SCHEDULE());
                        stmt.bindLong(2, schedule.get(index).getID_PAIR());
                        stmt.bindLong(3, schedule.get(index).getID_GROUP());
                        stmt.bindLong(4, schedule.get(index).getID_TEACHER());
                        stmt.bindString(5, schedule.get(index).getDISCIPLINE_NAME());
                        stmt.bindString(6, schedule.get(index).getDISCIPLINE_TYPE());
                        previousValue = DateUtil.dateFormatIncrease(schedule.get(index), j, previousValue);
                        stmt.bindString(7, previousValue);
                        stmt.bindLong(8, schedule.get(index).getID_CLASSROOM());
                        stmt.bindLong(9, schedule.get(index).getSUBGROUP_NUMBER());
                        stmt.bindLong(10, 0);

                        if (!DBRequest.checkIsDataAlreadyInDBorNot(context, TABLE_NAME, COL_SCHEDULE_ID, schedule.get(index).getID_SCHEDULE(), COL_SCHEDULE_DATE, previousValue)) {
                            stmt.execute();
                            stmt.clearBindings();
                        } else break;

                        beginDate = format.parse(previousValue);
                        j++;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else
                DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_SCHEDULE_ID, schedule.get(index).getID_SCHEDULE());
        }

        sqliteDatabase.close();
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
            String findendDate = dbHelper.getSemestersHelper().getSemesterEndDate(context, date);
            if (findendDate != null)
                endDate = format.parse(DateUtil.formatStandart(findendDate));
            else
                return null;
            //конец семестра
            if (beginDate.compareTo(endDate) >= 0)
                return null;

        } catch (ParseException e) {
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

                isCancelled = cursor.getInt(cursor.getColumnIndex(COL_IS_CANCELLED)) == 1;

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
                        dbHelper.getClassroomsHelper().getClassroom(context, cursor.getInt(cursor.getColumnIndex(COL_CLASSROOM_ID))),
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
                                cursor.getInt(cursor.getColumnIndex(COL_SCHEDULE_ID)), date)
                );

                scheduleModelArrayList.add(scheduleModel);
                cursor.moveToNext();
            }
            int a = 5;
        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);
        }
        return scheduleModelArrayList;

    }

    public static void deleteGroupSchedule(Context context, int idSchedule) {


        SQLiteDatabase db;
        DBHelper dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();

        ArrayList<Integer> idList = UsedSchedulesHelper.getIdSchedules(context, true);


        String tempselect = idList.get(0).toString() + ",";
        String select = "";

        for (int i = 0; i < idList.size(); i++) {
            select += tempselect + idList.get(i).toString() + ",";
            tempselect = idList.get(i).toString() + ",";
        }

        select = select.substring(0, select.length() - 1);

        Log.d("SELECT", select);

        db.delete(dbHelper.getUsedSchedulesHelper().TABLE_NAME, dbHelper.getUsedSchedulesHelper().COL_ID_SCHEDULE + "=" + idSchedule +
                " AND " + dbHelper.getUsedSchedulesHelper().COL_IS_GROUP + "=" + 1, null);

        db.delete(TABLE_NAME, COL_GROUP_ID + " IN (" + idSchedule + ") " + " AND " + COL_TEACHER_ID + " NOT IN (" + select + ")", null);
    }

    public static void deleteTeacherchedule(Context context, int idSchedule) {


        SQLiteDatabase db;
        DBHelper dbHelper = DBHelper.getInstance(context);
        db = dbHelper.getWritableDatabase();

        ArrayList<Integer> idList = UsedSchedulesHelper.getIdSchedules(context, false);


        String tempselect = idList.get(0).toString() + ",";
        String select = "";

        for (int i = 0; i < idList.size(); i++) {
            select += tempselect + idList.get(i).toString() + ",";
            tempselect = idList.get(i).toString() + ",";
        }

        select = select.substring(0, select.length() - 1);

        Log.d("SELECT", select);


        for (int i = 0; i < idList.size(); i++) {


            db.delete(dbHelper.getUsedSchedulesHelper().TABLE_NAME, dbHelper.getUsedSchedulesHelper().COL_ID_SCHEDULE + "=" + idSchedule +
                    " AND " + dbHelper.getUsedSchedulesHelper().COL_IS_GROUP + "=" + 0, null);

            db.delete(TABLE_NAME, COL_TEACHER_ID + " IN (" + idSchedule + ") " + " AND " + COL_GROUP_ID + " NOT IN (" + select + ")", null);
        }


        //clearDB(context,COL_TEACHER_ID,true);


    }


    public static boolean equalsTeachAndGroups(Context context, DBHelper dbHelper, SQLiteDatabase database, String TableName,
                                               String dbfieldGroup, int fieldIdGroup, String dbfieldTeacher, int fieldIdTeacher) {

        String Query = "SELECT * FROM " + TableName + " WHERE " + dbfieldGroup + " = " + fieldIdGroup + " AND " + dbfieldTeacher + "=" + fieldIdTeacher;
        Cursor cursor = database.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();

        return true;
    }

    public static void clearDB(Context context, String selection, boolean isGroup) {
        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
        ArrayList<Integer> idList = UsedSchedulesHelper.getIdSchedules(context, isGroup);
        for (int i = 0; i < idList.size(); i++)
            db.delete(TABLE_NAME, selection + "!=" + idList.get(i), null);

    }


}
