package savindev.myuniversity.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 *
 * Кампусы университета
 * Created by ilyas on 13.02.2016.
 */
public class CampusesHelper {

    protected static final String TABLE_NAME = "Campuses";
    protected static final String COL_ID_CAMPUS = "campus_id";
    protected static final String COL_CAMPUS_NAME = "campus_name";


    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_CAMPUS + " INTEGER PRIMARY KEY," +
                COL_CAMPUS_NAME + " TEXT" +
                ");");
    }

    public void setCampuses(UniversityInfo init, Context context) {
        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_CAMPUS + ", " +
                COL_CAMPUS_NAME + ")" + " VALUES (?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        for (int index = 0; index < init.CAMPUSES.size(); index++) {
            if (!init.CAMPUSES.get(index).IS_DELETED) {

                stmt.bindLong(1, init.CAMPUSES.get(index).ID_CAMPUS);
                stmt.bindString(2, init.CAMPUSES.get(index).CAMPUS_NAME);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, TABLE_NAME, COL_ID_CAMPUS, init.CAMPUSES.get(index).ID_CAMPUS);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }

}
