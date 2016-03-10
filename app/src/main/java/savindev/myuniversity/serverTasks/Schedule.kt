package savindev.myuniversity.serverTasks


import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList

class Schedule(//Класс для парсинга основных расписаний

        val ID_SCHEDULE: Int, val ID_SEMESTER: Int, val ID_PAIR: Int, val ID_GROUP: Int,
        val ID_TEACHER: Int, val DISCIPLINE_NAME: String, val DISCIPLINE_TYPE: String,
        val SCHEDULE_FIRST_DATE: String, val SCHEDULE_INTERVAL: Int,
        val ID_CLASSROOM: Int, val SUBGROUP_NUMBER: Int, val IS_DELETED: Boolean) {
    companion object {

        fun fromJson(`object`: JSONObject): Schedule? {
            val ID_SCHEDULE = `object`.optInt("ID_SCHEDULE", 0)
            val ID_SEMESTER = `object`.optInt("ID_SEMESTER", 0)
            val ID_PAIR = `object`.optInt("ID_PAIR", 0)
            val ID_GROUP = `object`.optInt("ID_GROUP", 0)
            val ID_TEACHER = `object`.optInt("ID_TEACHER", 0)
            val DISCIPLINE_NAME = `object`.optString("DISCIPLINE_NAME", "")
            val DISCIPLINE_TYPE = `object`.optString("DISCIPLINE_TYPE", "")
            val SCHEDULE_FIRS_DATE = `object`.optString("SCHEDULE_FIRS_DATE", "")
            val SCHEDULE_INTERVAL = `object`.optInt("SCHEDULE_INTERVAL", 0)
            val ID_CLASSROOM = `object`.optInt("ID_CLASSROOM", 0)
            val SUBGROUP_NUMBER = `object`.optInt("SUBGROUP_NUMBER", 0)
            val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
            return Schedule(ID_SCHEDULE, ID_SEMESTER, ID_PAIR, ID_GROUP, ID_TEACHER,
                    DISCIPLINE_NAME, DISCIPLINE_TYPE, SCHEDULE_FIRS_DATE,
                    SCHEDULE_INTERVAL, ID_CLASSROOM, SUBGROUP_NUMBER, IS_DELETED)
        }

        fun fromJson(array: JSONArray): ArrayList<Schedule> {
            val schedule = ArrayList<Schedule>()
            for (index in 0..array.length() - 1) {
                try {
                    val sched = fromJson(array.getJSONObject(index))
                    if (null != sched) schedule.add(sched)
                } catch (ignored: JSONException) {
                }

            }
            return schedule
        }
    }
}