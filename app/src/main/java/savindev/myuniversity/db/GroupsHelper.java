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
 * Учебные группы
 */
public class GroupsHelper {

    protected static final String TABLE_NAME = "Groups";
    protected static final String COL_ID_GROUP = "group_id";
    protected static final String COL_IS_FILE_SCHEDULE = "is_file_schedule";
    protected static final String COL_ID_FACULTY = "faculty_id";
    protected static final String COL_GROUP_NAME = "name_group";


    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_GROUP + " INTEGER PRIMARY KEY," +
                COL_ID_FACULTY + " INTEGER," +
                COL_IS_FILE_SCHEDULE + " INTEGER," +
                COL_GROUP_NAME + " TEXT" +
                ");");

    }

    public void upgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void setGroups(Context context, UniversityInfo init) {

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_GROUP + ", " + COL_GROUP_NAME + ", " +
                COL_ID_FACULTY + ", " + COL_IS_FILE_SCHEDULE + ")" + " VALUES (?, ?, ?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        for (int index = 0; index < init.GROUPS.size(); index++) {
            if (!init.GROUPS.get(index).IS_DELETED) {
                stmt.bindLong(1, init.GROUPS.get(index).ID_GROUP);
                stmt.bindString(2, init.GROUPS.get(index).GROUP_NAME);
                stmt.bindLong(3, init.GROUPS.get(index).ID_FACULTY);
                stmt.bindLong(4, init.GROUPS.get(index).IS_FILE_SCHEDULE ? 1 : 0);
                stmt.execute();
                stmt.clearBindings();

            } else {
                DBRequest.delete_byID(sqliteDatabase, GroupsHelper.TABLE_NAME, GroupsHelper.COL_ID_GROUP, init.GROUPS.get(index).ID_GROUP);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }


    public ArrayList<GroupsModel> getGroups(Context context, String faculty) {

        boolean isFile;
        String selection = FacultiesHelper.COL_FACULTY_ID;
        int group_id = DBRequest.getIdFromString(context, FacultiesHelper.TABLE_NAME, selection, FacultiesHelper.COL_FACULTY_SHORTNAME, faculty);
        ArrayList<String> groupNameList = DBRequest.getList(context, TABLE_NAME, COL_GROUP_NAME, COL_ID_FACULTY, group_id, COL_GROUP_NAME);
        ArrayList groupIdList = DBRequest.getList(context, TABLE_NAME, COL_ID_GROUP, COL_ID_FACULTY, group_id, COL_GROUP_NAME);
        ArrayList<String> groupIsFileList = DBRequest.getList(context, TABLE_NAME, COL_IS_FILE_SCHEDULE, COL_ID_FACULTY, group_id, COL_GROUP_NAME);
        ArrayList<GroupsModel> groupsModelArrayList = new ArrayList<>();
        for (int i = 0; i < groupNameList.size(); i++) {

            isFile = true;
            if (Integer.parseInt(groupIsFileList.get(i)) == 0)
                isFile = false;


            GroupsModel groupsModel = new GroupsModel(
                    groupNameList.get(i),
                    Integer.parseInt(groupIdList.get(i).toString()),
                    isFile,
                    true
            );
            groupsModelArrayList.add(groupsModel);
        }
        return groupsModelArrayList;

    }

    public String getGroupById(Context context, int groupId) {
        String groupName = "";

        DBHelper dbHelper = DBHelper.getInstance(context);
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
