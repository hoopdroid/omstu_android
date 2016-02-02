package savindev.myuniversity.notes;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.HashSet;

/**
 * Класс-объект для заметок
 */


public class NoteModel implements Parcelable {


    private int noteId; // идентификатор заметки
    private String name; //имя заметки
    private String text;    //текст заметки
    private NoteType type;  //тип: для дня, для даты, повторяющаяся, мб еще чего придумаем
    private HashSet<Bitmap> pictures;   //здесь будут лежать фотки
    private Priority priority;  //приоритет
    private int isDone; //выполнено ли
    private String sender;  //кто отправитель (если можно будет шарить)
    private String date;  //дата для заметки, здесь же время, если тип привязки такой
    private String pairId; //не помню, в каком формате лежит этот id. он + дата = уникальный идентификатор пары, для которой заметка. только если тип - для пары
    private Access access; //режим доступа для расшаривания



    public NoteModel(int noteId,String name, String sender, int isDone, Priority priority, HashSet<Bitmap> pictures, NoteType type, String text, String date, String pairId, Access access) {
        this.name = name;
        this.sender = sender;
        this.isDone = isDone;
        this.priority = priority;
        this.pictures = pictures;
        this.type = type;
        this.text = text;
        this.date = date;
        this.pairId = pairId;
        this.access = access;
        this.noteId = noteId;
    }

    public NoteModel(String name, String sender, int isDone, Priority priority, HashSet<Bitmap> pictures, NoteType type, String text, String date, String pairId, Access access) {
        this.name = name ;
        this.sender = sender;
        this.isDone = isDone;
        this.priority = priority;
        this.pictures = pictures;
        this.type = type;
        this.text = text;
        this.date = date;
        this.pairId = pairId;
        this.access = access;
        this.noteId = noteId;
    }

    protected NoteModel(Parcel in) {
        noteId = in.readInt();
        name = in.readString();
        text = in.readString();
        isDone = in.readInt();
        sender = in.readString();
        date = in.readString();
        pairId = in.readString();
    }

    public static final Creator<NoteModel> CREATOR = new Creator<NoteModel>() {
        @Override
        public NoteModel createFromParcel(Parcel in) {
            return new NoteModel(in);
        }

        @Override
        public NoteModel[] newArray(int size) {
            return new NoteModel[size];
        }
    };

    public Access getAccess() {
        return access;
    }

    public void setAccess(Access access) {
        this.access = access;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public HashSet<Bitmap> getPictures() {
        return pictures;
    }

    public void setPictures(HashSet<Bitmap> pictures) {
        this.pictures = pictures;
    }

    public Priority getPriority() {

        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public int getDone() {
        return isDone;
    }

    public void setDone(int done) {
        isDone = done;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPairId() {
        return pairId;
    }

    public void setPairId(String pairId) {
        this.pairId = pairId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getIsDone() {
        return isDone;
    }

    public void setIsDone(int isDone) {
        this.isDone = isDone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(noteId);
        dest.writeString(name);
        dest.writeString(text);
        dest.writeInt(isDone);
        dest.writeString(sender);
        dest.writeString(date);
        dest.writeString(pairId);
    }
}
