package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 * Информация о времени начала учебного занятия университета
 */
public class PairsHelper {

    protected static final String TABLE_NAME = "Pairs";
    protected static final String COL_ID_PAIR = "pair_id";
    protected static final String COL_PAIR_NUMBER = "pair_number";
    protected static final String COL_BEGIN_TIME = "pair_begin_time";
    protected static final String COL_END_TIME = "pair_end_time";


    private DBHelper dbHelper;

    public PairsHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

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
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_PAIR + ", " + COL_PAIR_NUMBER + ", " +
                COL_BEGIN_TIME + ", " + COL_END_TIME + ")" + " VALUES (?, ?, ?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        for (int index = 0; index < init.PAIRS.size(); index++) {
            if (!init.PAIRS.get(index).IS_DELETED) {
                stmt.bindLong(1, init.PAIRS.get(index).ID_PAIR);
                stmt.bindLong(2, init.PAIRS.get(index).PAIR_NUMBER);
                stmt.bindString(3, init.PAIRS.get(index).PAIR_BEGIN_TIME);
                stmt.bindString(4, init.PAIRS.get(index).PAIR_END_TIME);
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, PairsHelper.TABLE_NAME, PairsHelper.COL_ID_PAIR, init.PAIRS.get(index).ID_PAIR);
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }

    public int getPairsInDay(Context context) {
        int maxPair = 0;

        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

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

        DBHelper dbHelper = DBHelper.getInstance(context);
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

        DBHelper dbHelper = DBHelper.getInstance(context);
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
