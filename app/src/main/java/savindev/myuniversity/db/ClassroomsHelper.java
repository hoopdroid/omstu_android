package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import savindev.myuniversity.R;
import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 *
 * Аудитории университета
 *
 */
public class ClassroomsHelper {

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
                COL_CLASSROOM_TYPE + " TEXT" +
                ");");
    }

    public void setClassrooms(UniversityInfo init, Context context) {
        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_CLASSROOM + ", " + COL_ID_BUILDING + ", " +
                COL_CLASSROOM_NAME + ", " + COL_CLASSROOM_FULLNAME + ", " + COL_CLASSROOM_FLOOR + ", " +
                COL_CLASSROOM_DESCRIPTION + ", " + COL_CLASSROOM_TYPE + ")" + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();
        for (int index = 0; index < init.CLASSROOMS.size(); index++) {
            if (!init.CLASSROOMS.get(index).IS_DELETED) {

                stmt.bindLong(1, init.CLASSROOMS.get(index).ID_CLASSROOM);
                stmt.bindLong(2, init.CLASSROOMS.get(index).ID_BUILDING);
                stmt.bindString(3, init.CLASSROOMS.get(index).CLASSROOM_NAME);
                stmt.bindString(4, init.CLASSROOMS.get(index).CLASSROOM_FULLNAME);
                stmt.bindLong(5, init.CLASSROOMS.get(index).CLASSROOM_FLOOR);
                stmt.bindString(6, init.CLASSROOMS.get(index).CLASSROOM_DESCRIPTION);
                stmt.bindString(7, init.CLASSROOMS.get(index).CLASSROOM_TYPE_NAME);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_ID_CLASSROOM, init.CLASSROOMS.get(index).ID_CLASSROOM);
            }


        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();

    }

    public String getClassroom(Context context, int classroomId) {
        String classroomName = "330";
        int buildingName = 0;
        int buildingID = 0;

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_ID_CLASSROOM + " = " + classroomId, null);
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
            cursor = db.rawQuery("SELECT " + BuildingsHelper.COL_BUILDING_NUMBER + " FROM " + BuildingsHelper.TABLE_NAME + " WHERE " + BuildingsHelper.COL_ID_BUILDING + " = " + buildingID, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                buildingName = cursor.getInt(cursor.getColumnIndex(BuildingsHelper.COL_BUILDING_NUMBER));

                cursor.moveToNext();
            }

        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);

        }

        if (classroomName == null || classroomName.equals("null")) {
            classroomName = context.getResources().getString(R.string.building);
        }

        return Integer.toString(buildingName) + "-" + classroomName;
    }

}
