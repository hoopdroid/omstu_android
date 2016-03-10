package savindev.myuniversity.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

import savindev.myuniversity.notes.Priority;

/**
 * Заметки пользователя к учебным занятиям
 */
public class NotesHelper {

    protected static final String TABLE_NAME = "Notes";
    protected static final String COL_ID_NOTE = "note_id";
    protected static final String COL_NOTE_NAME = "note_name";
    protected static final String COL_SENDER_NAME = "sender_name";
    protected static final String COL_IS_DONE = "is_done";
    protected static final String COL_PRIORITY = "priority";
    protected static final String COL_PHOTO_LINK = "photo_link";
    protected static final String COL_TYPE_PAIR = "pair_type";
    protected static final String COL_TYPE_TIME = "pair_time";
    protected static final String COL_TYPE_REPEAT = "pair_repeat";
    protected static final String COL_TYPE_DATE = "pair_date";
    protected static final String COL_NOTE_TEXT = "note_text";
    protected static final String COL_NOTE_DATE = "note_date";
    protected static final String COL_PAIR_ID = "pair_id";
    protected static final String COL_NOTE_TIME = "time";
    protected static final String COL_REPEAT_TIME = "repeat_time";
    protected static final String COL_ACCESS = "pair_access";

    private DBHelper dbHelper;

    public NotesHelper(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void create(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID_NOTE + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_NOTE_NAME + " TEXT," +
                COL_SENDER_NAME + " TEXT," +
                COL_IS_DONE + " INTEGER," +
                COL_PRIORITY + " TEXT," +
                COL_PHOTO_LINK + " TEXT," +
                COL_TYPE_PAIR + " INTEGER," +
                COL_TYPE_TIME + " INTEGER," +
                COL_TYPE_REPEAT + " INTEGER," +
                COL_TYPE_DATE + " INTEGER," +
                COL_NOTE_TEXT + " TEXT," +
                COL_NOTE_DATE + " TEXT," +
                COL_PAIR_ID + " TEXT," +
                COL_NOTE_TIME + " TEXT," +
                COL_REPEAT_TIME + " TEXT," +
                COL_ACCESS + " TEXT" +
                ");");
    }

    public void setPairNote(NoteModel noteModel) {


        SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();

        ContentValues noteRow = new ContentValues();
        noteRow.put(COL_NOTE_NAME, noteModel.getName());
        noteRow.put(COL_SENDER_NAME, "UserName");
        noteRow.put(COL_IS_DONE, 0);
        noteRow.put(COL_PRIORITY, noteModel.getPriority().toString());
        noteRow.put(COL_TYPE_PAIR, 1);
        noteRow.put(COL_NOTE_TEXT, noteModel.getText());
        noteRow.put(COL_PAIR_ID, noteModel.getPairId());
        noteRow.put(COL_NOTE_DATE, noteModel.getDate());

        sqliteDatabase.insert(TABLE_NAME, null, noteRow);

        sqliteDatabase.close();

    }


    public ArrayList<NoteModel> getAllNotes() {
        ArrayList<NoteModel> noteModelArrayList = new ArrayList<>();

        SQLiteDatabase db;

        db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_NOTE_DATE + " IS NOT NULL", null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                NoteModel noteModel = new NoteModel(
                        cursor.getInt(cursor.getColumnIndex(COL_ID_NOTE)),
                        cursor.getString(cursor.getColumnIndex(COL_NOTE_NAME)),
                        null,
                        cursor.getInt(cursor.getColumnIndex(COL_IS_DONE)),
                        Priority.fromString(cursor.getString(cursor.getColumnIndex(COL_PRIORITY))),
                        null,
                        null,
                        cursor.getString(cursor.getColumnIndex(COL_NOTE_TEXT)),
                        cursor.getString(cursor.getColumnIndex(COL_NOTE_DATE)),
                        null,
                        null
                );
                noteModelArrayList.add(noteModel);
                cursor.moveToNext();

            }
            cursor.close();
        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);
        }

        db.close();

        return noteModelArrayList;

    }


    public ArrayList<NoteModel> getPairNotes(int scheduleId, String scheduleDate) {

        ArrayList<NoteModel> noteModelArrayList = new ArrayList<>();

        SQLiteDatabase db;

        String pairId = Integer.toString(scheduleId) + scheduleDate;
        db = dbHelper.getReadableDatabase();

        Cursor cursor;
        try {
            cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_PAIR_ID + " = " + pairId, null);
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                NoteModel noteModel = new NoteModel(
                        cursor.getInt(cursor.getColumnIndex(COL_ID_NOTE)),
                        cursor.getString(cursor.getColumnIndex(COL_NOTE_NAME)),
                        cursor.getString(cursor.getColumnIndex(COL_SENDER_NAME)),
                        cursor.getInt(cursor.getColumnIndex(COL_IS_DONE)),
                        Priority.fromString(cursor.getString(cursor.getColumnIndex(COL_PRIORITY))),
                        null,
                        null,
                        cursor.getString(cursor.getColumnIndex(COL_NOTE_TEXT)),
                        cursor.getString(cursor.getColumnIndex(COL_NOTE_DATE)),
                        cursor.getString(cursor.getColumnIndex(COL_PAIR_ID)),
                        null
                );
                noteModelArrayList.add(noteModel);
                cursor.moveToNext();
            }
            cursor.close();

        } catch (SQLiteException e) {
            Log.e("SQLITE DB EXCEPTION", e.toString(), e);
        }

        db.close();

        return noteModelArrayList;

    }

    public void deleteNote(int idNote) {
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID_NOTE + "=" + idNote, null);
        db.close();
    }

    public void setNoteIsDone(int idNote) {
        SQLiteDatabase db;
        db = dbHelper.getWritableDatabase();
        ContentValues isDoneValue = new ContentValues();
        isDoneValue.put(COL_IS_DONE, 1);

        db.update(TABLE_NAME, isDoneValue, COL_ID_NOTE + " = " + idNote, null);
        db.close();

    }
}
