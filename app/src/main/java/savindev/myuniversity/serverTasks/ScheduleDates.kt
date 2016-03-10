package savindev.myuniversity.serverTasks

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ScheduleDates(//Класс для парсинга дополнительных расписаний, по датам

        val ID_SCHEDULE: Int, dates: JSONArray) {
    val DATES: ArrayList<DATE>

    init {
        this.DATES = DATE.fromJson(dates)
    }

    class DATE(//Класс для парсинга дополнительных расписаний, по датам

            val DATE: String, val IS_CANCELED: Boolean, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): DATE? {
                val DATE = `object`.optString("DATE", "")
                val IS_CANCELED = `object`.optBoolean("IS_CANCELED", false)
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return DATE(DATE, IS_CANCELED, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<DATE> {
                val dates = ArrayList<DATE>()
                for (index in 0..array.length() - 1) {
                    try {
                        val data = fromJson(array.getJSONObject(index))
                        if (null != data) dates.add(data)
                    } catch (ignored: JSONException) {
                    }

                }
                return dates
            }
        }
    }

    companion object {

        fun fromJson(`object`: JSONObject): ScheduleDates? {
            val ID_SCHEDULE = `object`.optInt("ID_SCHEDULE", 0)
            var DATES: JSONArray? = null
            try {
                DATES = `object`.getJSONArray("DATES")
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return ScheduleDates(ID_SCHEDULE, DATES!!)
        }

        fun fromJson(array: JSONArray): ArrayList<ScheduleDates> {
            val ScheduleDates = ArrayList<ScheduleDates>()
            for (index in 0..array.length() - 1) {
                try {
                    val sched = fromJson(array.getJSONObject(index))
                    if (null != sched) ScheduleDates.add(sched)
                } catch (ignored: JSONException) {
                }

            }
            return ScheduleDates
        }
    }
}