package savindev.myuniversity.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import savindev.myuniversity.schedule.GroupsModel;

/**
 * Список используемых расписаний
 */
public class UsedSchedulesHelper {

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
        if (isGroup)
            isGroupDB = 1;
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

    public static ArrayList<Integer> getIdSchedules(Context context, boolean isGroup) {
        int isGroupDB = 0;
        if (isGroup) isGroupDB = 1;
        //TODO Сделать добавление правильных айди,сейчас проверяется по всем в UsedSchedules,но это не критично
        ArrayList<Integer> idList = new ArrayList<>();

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT " + COL_ID_SCHEDULE + " FROM " + TABLE_NAME, null);
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
