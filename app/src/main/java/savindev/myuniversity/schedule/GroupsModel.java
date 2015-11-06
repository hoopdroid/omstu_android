package savindev.myuniversity.schedule;

/**
 * Created by Katena on 05.11.2015.
 */
public class GroupsModel {

    private int id;
    private boolean isGroup;
    private String name;
    private String lastRefresh;



    public GroupsModel(String name, int id, boolean isGroup, String lastRefresh) {
        this.name = name;
        this.id = id;
        this.isGroup = isGroup;
        this.lastRefresh = lastRefresh;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getLastRefresh() {
        return lastRefresh;
    }

    public void setLastRefresh(String lastRefresh) {
        this.lastRefresh = lastRefresh;
    }
}
