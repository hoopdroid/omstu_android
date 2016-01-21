package savindev.myuniversity.performance;

import android.net.Uri;

public class PointModel {
    private int idGroup;
    private String name;
    private int ID_PROGRESS_RAITNG_FILE;
    private boolean isDeleted;

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    private Uri fileUri;

    public PointModel(int idGroup, String name, int ID_PROGRESS_RAITNG_FILE) {
        this.idGroup = idGroup;
        this.name = name;
        this.ID_PROGRESS_RAITNG_FILE = ID_PROGRESS_RAITNG_FILE;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID_PROGRESS_RAITNG_FILE() {
        return ID_PROGRESS_RAITNG_FILE;
    }

    public void setID_PROGRESS_RAITNG_FILE(int ID_PROGRESS_RAITNG_FILE) {
        this.ID_PROGRESS_RAITNG_FILE = ID_PROGRESS_RAITNG_FILE;
    }


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
