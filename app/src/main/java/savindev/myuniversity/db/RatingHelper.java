package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.notes.NoteModel;
import savindev.myuniversity.notes.Priority;
import savindev.myuniversity.performance.PointModel;
import savindev.myuniversity.performance.RatingModel;
import savindev.myuniversity.serverTasks.UniversityInfo;

/**
 * Класс для работы с рейтингом из базы данных
 */
public class RatingHelper {

    protected static final String TABLE_NAME = "Rating";
    protected static final String COL_ID_GROUP = "group_id";
    protected static final String COL_NAME = "name";
    protected static final String COL_ID_PROGRESS_RATING_FILE = "id_progress_rating_file";
    protected static final String COL_ESTIMATION_POINT_NAME = "estimation_point_name";
    protected static final String COL_ESTIMATION_POINT_NUMBER = "estimation_point_number";


    private DBHelper dbHelper;

    public RatingHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_GROUP + " INTEGER PRIMARY KEY," +
                COL_NAME + " TEXT," +
                COL_ID_PROGRESS_RATING_FILE+ " INTEGER," +
                COL_ESTIMATION_POINT_NAME+ " TEXT," +
                COL_ESTIMATION_POINT_NUMBER+ " INTEGER" +
                ");");

    }

    public void setRatingModels(Context context, ArrayList<RatingModel> ratingModelList) {

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();
        String sql = "INSERT INTO " + TABLE_NAME + " (" + COL_ID_GROUP+ ", " + COL_NAME + ", " +
                COL_ID_PROGRESS_RATING_FILE + ", " + COL_ESTIMATION_POINT_NAME+ ", " + COL_ESTIMATION_POINT_NUMBER +  ")" + " VALUES (?, ?, ?, ?, ?)";
        SQLiteStatement stmt = sqliteDatabase.compileStatement(sql);
        sqliteDatabase.beginTransaction();

        for (int index = 0; index < ratingModelList.size(); index++) {
            if (!ratingModelList.get(index).getPoints().get(index).isDeleted()) {
                stmt.bindLong(1, ratingModelList.get(index).getPoints().get(index).getIdGroup());
                stmt.bindString(2,ratingModelList.get(index).getPoints().get(index).getName());
                stmt.bindLong(3, ratingModelList.get(index).getPoints().get(index).getID_PROGRESS_RAITNG_FILE());
                stmt.bindString(4, ratingModelList.get(index).getESTIMATION_POINT_NAME());
                stmt.bindLong(5, ratingModelList.get(index).getESTIMATION_POINT_NUMBER());
                stmt.execute();
                stmt.clearBindings();
            } else {
                DBRequest.delete_byID(sqliteDatabase, PairsHelper.TABLE_NAME, PairsHelper.COL_ID_PAIR, ratingModelList.get(index).getPoints().get(index).getIdGroup());
            }
        }
        sqliteDatabase.setTransactionSuccessful();
        sqliteDatabase.endTransaction();
        sqliteDatabase.close();
    }

    public ArrayList<RatingModel> getRatingModels() {

        ArrayList<RatingModel> ratingModelArrayList = new ArrayList<>();
        ArrayList<PointModel> pointModelList = new ArrayList<>();

        SQLiteDatabase db;

        db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                PointModel pointModel = new PointModel(
                        cursor.getInt(cursor.getColumnIndex(COL_ID_GROUP)),
                        cursor.getString(cursor.getColumnIndex(COL_NAME)),
                        cursor.getInt(cursor.getColumnIndex(COL_ID_PROGRESS_RATING_FILE))
                );
                pointModelList.add(pointModel);

                RatingModel ratingModel = new RatingModel(
                        pointModelList,
                        cursor.getString(cursor.getColumnIndex(COL_ESTIMATION_POINT_NAME)),
                        cursor.getInt(cursor.getColumnIndex(COL_ESTIMATION_POINT_NUMBER))
                );

                ratingModelArrayList.add(ratingModel);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);
        }

        db.close();

        return ratingModelArrayList;
    }




}
