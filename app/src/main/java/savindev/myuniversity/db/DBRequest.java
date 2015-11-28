package savindev.myuniversity.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by ilyas on 20.11.2015.
 */
public class DBRequest {

    public DBRequest() {
    }


    public static ArrayList getList(Context context, String table, String selection, String findColumn, int valueColumn,String orderBy) {

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        ArrayList list = new ArrayList();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT " + selection + " FROM " + table + " WHERE " + findColumn + " = " + valueColumn + " ORDER BY " + orderBy, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                //  list.add(cursor.getString(cursor.getColumnIndex(selection)));
                list.add(cursor.getString(cursor.getColumnIndex(selection)))
                ;
                cursor.moveToNext();

            }

        } catch (SQLiteException e) {
            Log.e("SQLITE EXCEPTION", e.toString(), e);
        }

        db.close();
        return list;
    }

    public static ArrayList getList(Context context, String table, String selection) {

        SQLiteDatabase db;
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        ArrayList list = new ArrayList();
        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT " + selection + " FROM " + table + " ORDER BY " + selection, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                list.add(cursor.getString(cursor.getColumnIndex(selection)));
                cursor.moveToNext();
            }

        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);
        }
        db.close();
        return list;
    }

    public static boolean isUniversityInfoThere(Context context) {

        SQLiteDatabase db;
        ArrayList tables = getAllDBTables(context);
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();


        boolean hasTables = false;


        for (int i = 0; i < tables.size(); i++) {

            Cursor cursor = db.rawQuery("SELECT * FROM " + tables.get(i), null);

            if (cursor != null && cursor.getCount() > 0) {
                hasTables = true;
                cursor.close();
            }
        }
        db.close();
        return hasTables;


    }

    public static boolean isTableExists(String table, Context context) {
        boolean isExists = false;
        ArrayList tablesList = getAllDBTables(context);
        for (int i = 0; i < tablesList.size(); i++) {
            if (table.equals(tablesList.get(i))) {
                isExists = true;
                break;
            }
        }
        return isExists;
    }

    public static ArrayList getAllDBTables(Context context) {
        SQLiteDatabase db;
        ArrayList tables = new ArrayList();
        DBHelper dbHelper = new DBHelper(context);
        db = dbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        c.moveToFirst();
        if (c.moveToNext()) {
            while (!c.isAfterLast()) {
                String table = c.getString(c.getColumnIndex("name"));
                if (!table.equals("android_metadata"))
                    tables.add(c.getString(c.getColumnIndex("name")));
                c.moveToNext();
            }
        }
        db.close();
        return tables;
    }

    public static int getIdFromString(Context context, String tableName, String selection, String columnName, String valueColumn) {

        int id = 0;
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();

        try {
            String selectQuery = "SELECT " + selection + " FROM " + tableName + " WHERE " + columnName + "=?";
            Cursor c = sqLiteDatabase.rawQuery(selectQuery, new String[]{valueColumn});
            if (c.moveToFirst()) {
                id = c.getInt(c.getColumnIndex(selection));
            }
            c.close();
        } catch (SQLiteException e) {
            Log.e("DB EXCEPTION", e.toString(), e);
        }
        sqLiteDatabase.close();
        return id;
    }

//    public static void removeAllSchedules(Context context) {
//        DBHelper dbHelper = new DBHelper(context);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//        db.delete(DBHelper.UsedSchedulesHelper.TABLE_NAME, null, null);
//
//    }

    public static boolean checkIsDataAlreadyInDBorNot(Context context,String TableName,
                                                      String dbfield, int fieldValue,String dbfield2, String fieldValue2) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        SQLiteDatabase database=dbHelper.getReadableDatabase();
        String Query = "SELECT * FROM " + TableName + " WHERE " + dbfield + " = " + fieldValue + " AND " +dbfield2 + " = "+ fieldValue2;
        Cursor cursor = database.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        database.close();
        return true;
    }

    public static void delete_byID(SQLiteDatabase db, String table, String select, int id) {
        db.delete(table, select + "=" + id, null);
    }

    public static String getUserGroup(int id, Context context) {
        String groupName = "Group";

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String find = "SELECT * FROM  " + DBHelper.GroupsHelper.TABLE_NAME + " WHERE " + DBHelper.GroupsHelper.COL_ID_GROUP + " = " + id;
        Cursor cursor = sqLiteDatabase.rawQuery(find, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            groupName = cursor.getString(cursor.getColumnIndex(DBHelper.GroupsHelper.COL_GROUP_NAME));
            cursor.moveToNext();
        }
        cursor.close();
        sqLiteDatabase.close();
        return groupName;
    }
}
