package savindev.myuniversity.serverTasks;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScheduleDates { //Класс для парсинга дополнительных расписаний, по датам

    public final int ID_SCHEDULE;
    public final ArrayList<DATE> DATES;

    public ScheduleDates(int ID_SCHEDULE, JSONArray dates) {
        this.ID_SCHEDULE = ID_SCHEDULE;
        this.DATES = DATE.fromJson(dates);
    }

    public static ScheduleDates fromJson(final JSONObject object) {
        final int ID_SCHEDULE = object.optInt("ID_SCHEDULE", 0);
        JSONArray DATES = null;
        try {
            DATES = object.getJSONArray("DATES");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ScheduleDates(ID_SCHEDULE, DATES);
    }

    public static ArrayList<ScheduleDates> fromJson(final JSONArray array) {
        final ArrayList<ScheduleDates> ScheduleDates = new ArrayList<>();
        for (int index = 0; index < array.length(); ++index) {
            try {
                final ScheduleDates sched = fromJson(array.getJSONObject(index));
                if (null != sched) ScheduleDates.add(sched);
            } catch (final JSONException ignored) {
            }
        }
        return ScheduleDates;
    }

    public static class DATE { //Класс для парсинга дополнительных расписаний, по датам

        public final String DATE;
        public final boolean IS_CANCELED;
        public final boolean IS_DELETED;

        public DATE(String DATE, boolean IS_CANCELED, boolean IS_DELETED) {
            this.DATE = DATE;
            this.IS_CANCELED = IS_CANCELED;
            this.IS_DELETED = IS_DELETED;
        }

        public static DATE fromJson(final JSONObject object) {
            final String DATE = object.optString("DATE", "");
            final boolean IS_CANCELED = object.optBoolean("IS_CANCELED", false);
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new DATE(DATE, IS_CANCELED, IS_DELETED);
        }

        public static ArrayList<DATE> fromJson(final JSONArray array) {
            final ArrayList<DATE> dates = new ArrayList<>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final DATE data = fromJson(array.getJSONObject(index));
                    if (null != data) dates.add(data);
                } catch (final JSONException ignored) {
                }
            }
            return dates;
        }
    }
}