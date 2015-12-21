package savindev.myuniversity.notes;

/**
 * Created by Илья on 21.12.2015.
 */
public class NoteModel {

    private int noteId;
    private int scheduleId;
    private String lessonName;
    private String noteDate;
    private String noteText;
    private String photoLink;
    private int priority;

    public NoteModel( int noteId,int scheduleId, String lessonName, String noteDate, String noteText, String photoLink, int priority) {
        this.noteId = noteId;
        this.scheduleId = scheduleId;
        this.lessonName = lessonName;
        this.noteDate = noteDate;
        this.noteText = noteText;
        this.photoLink = photoLink;
        this.priority = priority;
    }

    public NoteModel(int scheduleId, String lessonName, String noteDate, String noteText, String photoLink, int priority) {
        this.scheduleId = scheduleId;
        this.lessonName = lessonName;
        this.noteDate = noteDate;
        this.noteText = noteText;
        this.photoLink = photoLink;
        this.priority = priority;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public String getLessonName() {
        return lessonName;
    }

    public void setLessonName(String lessonName) {
        this.lessonName = lessonName;
    }

    public String getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(String noteDate) {
        this.noteDate = noteDate;
    }

    public String getNoteText() {
        return noteText;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
