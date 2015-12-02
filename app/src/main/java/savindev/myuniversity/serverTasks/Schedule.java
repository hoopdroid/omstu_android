package savindev.myuniversity.serverTasks;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Schedule { //Класс для парсинга основных расписаний

    public final int ID_SCHEDULE;
    public final int ID_SEMESTER;
    public final int ID_PAIR;
    public final int ID_GROUP;
    public final int ID_TEACHER;
    public final String DISCIPLINE_NAME;
    public final String DISCIPLINE_TYPE;
    public final String SCHEDULE_FIRST_DATE;
    public final int SCHEDULE_INTERVAL;
    public final int ID_CLASSROOM;
    public final int SUBGROUP_NUMBER;
    public final boolean IS_DELETED;

    public Schedule(int ID_SCHEDULE, int ID_SEMESTER, int ID_PAIR, int ID_GROUP,
                    int ID_TEACHER, String DISCIPLINE_NAME, String DISCIPLINE_TYPE,
                    String SCHEDULE_FIRS_DATE, int SCHEDULE_INTERVAL,
                    int ID_CLASSROOM, int SUBGROUP_NUMBER,   boolean IS_DELETED) {
        this.ID_SCHEDULE = ID_SCHEDULE;
        this.ID_SEMESTER = ID_SEMESTER;
        this.ID_PAIR = ID_PAIR;
        this.ID_GROUP = ID_GROUP;
        this.ID_TEACHER = ID_TEACHER;
        this.DISCIPLINE_NAME = DISCIPLINE_NAME;
        this.DISCIPLINE_TYPE = DISCIPLINE_TYPE;
        this.SCHEDULE_FIRST_DATE = SCHEDULE_FIRS_DATE;
        this.SCHEDULE_INTERVAL = SCHEDULE_INTERVAL;
        this.ID_CLASSROOM = ID_CLASSROOM;
        this.SUBGROUP_NUMBER = SUBGROUP_NUMBER;
        this.IS_DELETED = IS_DELETED;
    }

    public static Schedule fromJson(final JSONObject object) {
        final int ID_SCHEDULE = object.optInt("ID_SCHEDULE", 0);
        final int ID_SEMESTER = object.optInt("ID_SEMESTER", 0);
        final int ID_PAIR = object.optInt("ID_PAIR", 0);
        final int ID_GROUP = object.optInt("ID_GROUP", 0);
        final int ID_TEACHER = object.optInt("ID_TEACHER", 0);
        final String DISCIPLINE_NAME = object.optString("DISCIPLINE_NAME", "");
        final String DISCIPLINE_TYPE = object.optString("DISCIPLINE_TYPE", "");
        final String SCHEDULE_FIRS_DATE = object.optString("SCHEDULE_FIRS_DATE", "");
        final int SCHEDULE_INTERVAL = object.optInt("SCHEDULE_INTERVAL", 0);
        final int ID_CLASSROOM = object.optInt("ID_CLASSROOM", 0);
        final int SUBGROUP_NUMBER = object.optInt("SUBGROUP_NUMBER", 0);
        final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
        return new Schedule(ID_SCHEDULE, ID_SEMESTER, ID_PAIR, ID_GROUP, ID_TEACHER,
                DISCIPLINE_NAME, DISCIPLINE_TYPE, SCHEDULE_FIRS_DATE,
                SCHEDULE_INTERVAL, ID_CLASSROOM, SUBGROUP_NUMBER, IS_DELETED);
    }

    public static ArrayList<Schedule> fromJson(final JSONArray array) {
        final ArrayList<Schedule> schedule = new ArrayList<>();
        for (int index = 0; index < array.length(); ++index) {
            try {
                final Schedule sched = fromJson(array.getJSONObject(index));
                if (null != sched) schedule.add(sched);
            } catch (final JSONException ignored) {
            }
        }
        return schedule;
    }
}