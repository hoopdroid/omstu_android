package savindev.myuniversity.notes;

import android.graphics.Bitmap;

import java.util.Date;
import java.util.HashSet;

public class NoteModel {
    private String name; //имя заметки
    private String text;    //текст заметки
    private NoteType type;  //тип: для дня, для даты, повторяющаяся, мб еще чего придумаем
    private HashSet<Bitmap> pictures;   //здесь будут лежать фотки
    private Priority priority;  //приоритет
    private Boolean isDone; //выполнено ли
    private String sender;  //кто отправитель (если можно будет шарить)
    private Date date;  //дата для заметки, здесь же время, если тип привязки такой
    private String pairId; //не помню, в каком формате лежит этот id. он + дата = уникальный идентификатор пары, для которой заметка. только если тип - для пары
    private Access access; //режим доступа для расшаривания


    public NoteModel(String name, String sender, Boolean isDone, Priority priority, HashSet<Bitmap> pictures, NoteType type, String text, Date date, String pairId, Access access) {
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
    }

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

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getPairId() {
        return pairId;
    }

    public void setPairId(String pairId) {
        this.pairId = pairId;
    }
}
