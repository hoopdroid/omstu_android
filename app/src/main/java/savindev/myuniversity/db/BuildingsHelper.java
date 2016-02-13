package savindev.myuniversity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 *
 * Здания университета
 *
 */
public class BuildingsHelper {

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

    public void setBuildings(UniversityInfo init, Context context) {
        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_BUILDING + ", " + COL_ID_CAMPUS + ", " +
                COL_BUILDING_NUMBER + ", " + COL_BUILDING_NAME + ", " + COL_BUILDING_TYPE_NAME + ", " +
                COL_BUILDING_FLOOR_COUNT + ", " + COL_HAS_GROUND_FLOOR + ")" + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();
        for (int index = 0; index < init.BUILDINGS.size(); index++) {
            if (!init.BUILDINGS.get(index).IS_DELETED) {

                stmt.bindLong(1, init.BUILDINGS.get(index).ID_BUILDING);
                stmt.bindLong(2, init.BUILDINGS.get(index).ID_CAMPUS);
                stmt.bindLong(3, init.BUILDINGS.get(index).BUILDING_NUMBER);
                stmt.bindString(4, init.BUILDINGS.get(index).BUILDING_NAME);
                stmt.bindString(5, init.BUILDINGS.get(index).BUILDING_TYPE_NAME);
                stmt.bindLong(6, init.BUILDINGS.get(index).BUILDING_FLOOR_COUNT);

                int hasGroundFloor = 1;
                if (!init.BUILDINGS.get(index).HAS_GROUND_FLOOR)
                    hasGroundFloor = 0;

                stmt.bindLong(7, hasGroundFloor);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_ID_BUILDING, init.BUILDINGS.get(index).ID_BUILDING);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
    }
}
