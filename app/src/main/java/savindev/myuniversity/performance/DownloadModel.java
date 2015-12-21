package savindev.myuniversity.performance;

public class DownloadModel {
    private int idGroup;
    private String name;
    private int ID_PROGRESS_RAITNG_FILE;

    public DownloadModel(int idGroup, String name, int ID_PROGRESS_RAITNG_FILE) {
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
}
