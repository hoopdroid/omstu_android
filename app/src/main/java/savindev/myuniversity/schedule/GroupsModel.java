package savindev.myuniversity.schedule;

/**
 * Класс-объект для используемых групп
 */

public class GroupsModel {

    private int id;
    private boolean isGroup;
    private String name;
    private String lastRefresh;

    public boolean isFileSchedule() {
        return isFileSchedule;
    }

    public void setFileSchedule(boolean fileSchedule) {
        isFileSchedule = fileSchedule;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    private boolean isFileSchedule;
    private boolean selected;

    public GroupsModel(String name, int id, boolean isGroup, String lastRefresh) {
        this.name = name;
        this.id = id;
        this.isGroup = isGroup;
        this.lastRefresh = lastRefresh;
    }

    public GroupsModel(String name, int id, boolean isFileSchedule, boolean isGroup) {
        this.name = name;
        this.id = id;
        this.isFileSchedule = isFileSchedule;
        this.isGroup = isGroup;
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupsModel that = (GroupsModel) o;
        return id == that.id && isGroup == that.isGroup && selected == that.selected && name.equals(that.name) && lastRefresh.equals(that.lastRefresh);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (isGroup ? 1 : 0);
        result = 31 * result + name.hashCode();
        result = 31 * result + lastRefresh.hashCode();
        result = 31 * result + (selected ? 1 : 0);
        return result;
    }
}
