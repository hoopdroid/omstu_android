package savindev.myuniversity.serverTasks

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * В данном файле собраны подклассы для парсинга json'а инит. информации
 * Каждый подкласс имеет структуру, конструкторы, методы составления объектов
 * и ArrayList'ов из этих объектов.
 * Несколько подклассов связываются в основной класс, представляющий собой полноценный
 * json разбираемого запроса
 */
class UniversityInfo(val UNIVERSITY_FULLNAME: String, val UNIVERSITY_SHORTNAME: String,
                     val DAYS_IN_WEEK: Int, SEMESTERS: JSONArray?, FACULTIES: JSONArray?,
                     DEPARTMENTS: JSONArray?, GROUPS: JSONArray?, TEACHERS: JSONArray?,
                     PAIRS: JSONArray?, CAMPUSES: JSONArray?, CLASSROOMS: JSONArray?,
                     BUILDINGS: JSONArray?) {
    val SEMESTERS: ArrayList<SEMESTER>
    val PAIRS: ArrayList<PAIR>
    val FACULTIES: ArrayList<FACULTY>
    val DEPARTMENTS: ArrayList<DEPARTMENT>
    val GROUPS: ArrayList<GROUP>
    val TEACHERS: ArrayList<TEACHER>
    val CAMPUSES: ArrayList<CAMPUS>
    val CLASSROOMS: ArrayList<CLASSROOM>
    val BUILDINGS: ArrayList<BUILDING>

    init {
        if (SEMESTERS != null)
            this.SEMESTERS = SEMESTER.fromJson(SEMESTERS)
        else
            this.SEMESTERS = ArrayList<SEMESTER>()
        if (FACULTIES != null)
            this.FACULTIES = FACULTY.fromJson(FACULTIES)
        else
            this.FACULTIES = ArrayList<FACULTY>()
        if (DEPARTMENTS != null)
            this.DEPARTMENTS = DEPARTMENT.fromJson(DEPARTMENTS)
        else
            this.DEPARTMENTS = ArrayList<DEPARTMENT>()
        if (GROUPS != null)
            this.GROUPS = GROUP.fromJson(GROUPS)
        else
            this.GROUPS = ArrayList<GROUP>()
        if (TEACHERS != null)
            this.TEACHERS = TEACHER.fromJson(TEACHERS)
        else
            this.TEACHERS = ArrayList<TEACHER>()
        if (PAIRS != null)
            this.PAIRS = PAIR.fromJson(PAIRS)
        else
            this.PAIRS = ArrayList<PAIR>()
        if (CAMPUSES != null)
            this.CAMPUSES = CAMPUS.fromJson(CAMPUSES)
        else
            this.CAMPUSES = ArrayList<CAMPUS>()
        if (CLASSROOMS != null)
            this.CLASSROOMS = CLASSROOM.fromJson(CLASSROOMS)
        else
            this.CLASSROOMS = ArrayList<CLASSROOM>()
        if (BUILDINGS != null)
            this.BUILDINGS = BUILDING.fromJson(BUILDINGS)
        else
            this.BUILDINGS = ArrayList<BUILDING>()
    }

    class SEMESTER(//Классы для парсинга: семестры
            val ID_SEMESTER: Int, val BEGIN_DT: String, val END_DT: String, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): SEMESTER? {
                val ID_SEMESTER = `object`.optInt("ID_SEMESTER", 0)
                val BEGIN_DT = `object`.optString("BEGIN_DT", "")
                val END_DT = `object`.optString("END_DT", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return SEMESTER(ID_SEMESTER, BEGIN_DT, END_DT, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<SEMESTER> {
                val SEMESTERS = ArrayList<SEMESTER>()
                for (index in 0..array.length() - 1) {
                    try {
                        val semester = fromJson(array.getJSONObject(index))
                        if (null != semester) SEMESTERS.add(semester)
                    } catch (ignored: JSONException) {
                    }

                }
                return SEMESTERS
            }
        }
    }

    class PAIR(//Классы для парсинга: пары
            val ID_PAIR: Int, val PAIR_NUMBER: Int, val PAIR_BEGIN_TIME: String, val PAIR_END_TIME: String, val IS_FILE_SCHEDULE: Boolean, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): PAIR? {
                val ID_PAIR = `object`.optInt("ID_PAIR", 0)
                val PAIR_NUMBER = `object`.optInt("PAIR_NUMBER", 0)
                val PAIR_BEGIN_TIME = `object`.optString("PAIR_BEGIN_TIME", "")
                val PAIR_END_TIME = `object`.optString("PAIR_END_TIME", "")
                val IS_FILE_SCHEDULE = `object`.optBoolean("IS_FILE_SCHEDULE", false)
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return PAIR(ID_PAIR, PAIR_NUMBER, PAIR_BEGIN_TIME, PAIR_END_TIME, IS_FILE_SCHEDULE, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<PAIR> {
                val PAIRS = ArrayList<PAIR>()
                for (index in 0..array.length() - 1) {
                    try {
                        val pair = fromJson(array.getJSONObject(index))
                        if (null != pair) PAIRS.add(pair)
                    } catch (ignored: JSONException) {
                    }

                }
                return PAIRS
            }
        }
    }

    class FACULTY(//Классы для парсинга: факультеты

            val ID_FACULTY: Int, val FACULTY_FULLNAME: String, val FACULTY_SHORTNAME: String, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): FACULTY? {
                val ID_FACULTY = `object`.optInt("ID_FACULTY", 0)
                val FACULTY_FULLNAME = `object`.optString("FACULTY_FULLNAME", "")
                val FACULTY_SHORTNAME = `object`.optString("FACULTY_SHORTNAME", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return FACULTY(ID_FACULTY, FACULTY_FULLNAME, FACULTY_SHORTNAME, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<FACULTY> {
                val FACULTIES = ArrayList<FACULTY>()
                for (index in 0..array.length() - 1) {
                    try {
                        val faculty = fromJson(array.getJSONObject(index))
                        if (null != faculty) FACULTIES.add(faculty)
                    } catch (ignored: JSONException) {
                    }

                }
                return FACULTIES
            }
        }
    }

    class DEPARTMENT(//Классы для парсинга: кафедры

            val ID_DEPARTMENT: Int, val ID_FACULTY: Int, val ID_CLASSROOM: Int,
            val DEPARTMENT_FULLNAME: String, val DEPARTMENT_SHORTNAME: String, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): DEPARTMENT? {
                val ID_DEPARTMENT = `object`.optInt("ID_DEPARTMENT", 0)
                val ID_FACULTY = `object`.optInt("ID_FACULTY", 0)
                val ID_CLASSROOM = `object`.optInt("FACULTY_NAME", 0)
                val DEPARTMENT_FULLNAME = `object`.optString("DEPARTMENT_FULLNAME", "")
                val DEPARTMENT_SHORTNAME = `object`.optString("DEPARTMENT_SHORTNAME", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return DEPARTMENT(ID_DEPARTMENT, ID_FACULTY, ID_CLASSROOM,
                        DEPARTMENT_FULLNAME, DEPARTMENT_SHORTNAME, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<DEPARTMENT> {
                val DEPARTMENTS = ArrayList<DEPARTMENT>()
                for (index in 0..array.length() - 1) {
                    try {
                        val department = fromJson(array.getJSONObject(index))
                        if (null != department) DEPARTMENTS.add(department)
                    } catch (ignored: JSONException) {
                    }

                }
                return DEPARTMENTS
            }
        }
    }

    class GROUP(//Классы для парсинга: группы

            val ID_GROUP: Int, val ID_FACULTY: Int, val GROUP_NAME: String, val IS_FILE_SCHEDULE: Boolean, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): GROUP? {
                val ID_GROUP = `object`.optInt("ID_GROUP", 0)
                val ID_FACULTY = `object`.optInt("ID_FACULTY", 0)
                val GROUP_NAME = `object`.optString("GROUP_NAME", "")
                val IS_FILE_SCHEDULE = `object`.optBoolean("IS_FILE_SCHEDULE", false)
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return GROUP(ID_GROUP, ID_FACULTY, GROUP_NAME, IS_FILE_SCHEDULE, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<GROUP> {
                val GROUPS = ArrayList<GROUP>()
                for (index in 0..array.length() - 1) {
                    try {
                        val group = fromJson(array.getJSONObject(index))
                        if (null != group) GROUPS.add(group)
                    } catch (ignored: JSONException) {
                    }

                }
                return GROUPS
            }
        }
    }

    class TEACHER(//Классы для парсинга: преподы

            val ID_TEACHER: Int, val ID_DEPARTMENT: Int, val TEACHER_LASTNAME: String, val TEACHER_FIRSTNAME: String,
            val TEACHER_MIDDLENAME: String, val GENDER: String, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): TEACHER? {
                val ID_TEACHER = `object`.optInt("ID_TEACHER", 0)
                val ID_DEPARTMENT = `object`.optInt("ID_DEPARTMENT", 0)
                val TEACHER_LASTNAME = `object`.optString("TEACHER_LASTNAME", "")
                val TEACHER_FIRSTNAME = `object`.optString("TEACHER_FIRSTNAME", "")
                val TEACHER_MIDDLENAME = `object`.optString("TEACHER_MIDDLENAME", "")
                val GENDER = `object`.optString("GENDER", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return TEACHER(ID_TEACHER, ID_DEPARTMENT, TEACHER_LASTNAME, TEACHER_FIRSTNAME,
                        TEACHER_MIDDLENAME, GENDER, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<TEACHER> {
                val TEACHERS = ArrayList<TEACHER>()
                for (index in 0..array.length() - 1) {
                    try {
                        val teacher = fromJson(array.getJSONObject(index))
                        if (null != teacher) TEACHERS.add(teacher)
                    } catch (ignored: JSONException) {
                    }

                }
                return TEACHERS
            }
        }
    }

    class CAMPUS(//Классы для парсинга: кампуса

            val ID_CAMPUS: Int, val CAMPUS_NAME: String, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): CAMPUS? {
                val ID_CAMPUS = `object`.optInt("ID_CAMPUS", 0)
                val CAMPUS_NAME = `object`.optString("CAMPUS_NAME", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return CAMPUS(ID_CAMPUS, CAMPUS_NAME, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<CAMPUS> {
                val CAMPUSES = ArrayList<CAMPUS>()
                for (index in 0..array.length() - 1) {
                    try {
                        val campus = fromJson(array.getJSONObject(index))
                        if (null != campus) CAMPUSES.add(campus)
                    } catch (ignored: JSONException) {
                    }

                }
                return CAMPUSES
            }
        }
    }

    class CLASSROOM(//Классы для парсинга: аудитории

            val ID_CLASSROOM: Int, val ID_BUILDING: Int, val CLASSROOM_FLOOR: Int, val CLASSROOM_FULLNAME: String,
            val CLASSROOM_DESCRIPTION: String, val CLASSROOM_NAME: String, val CLASSROOM_TYPE_NAME: String, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): CLASSROOM? {
                val ID_CLASSROOM = `object`.optInt("ID_CLASSROOM", 0)
                val ID_BUILDING = `object`.optInt("ID_BUILDING", 0)
                val CLASSROOM_FLOOR = `object`.optInt("CLASSROOM_FLOOR", 0)
                val CLASSROOM_FULLNAME = `object`.optString("CLASSROOM_FULLNAME", "")
                val CLASSROOM_DESCRIPTION = `object`.optString("CLASSROOM_DESCRIPTION", "")
                val CLASSROOM_NAME = `object`.optString("CLASSROOM_NAME", "")
                val CLASSROOM_TYPE_NAME = `object`.optString("CLASSROOM_TYPE_NAME", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                return CLASSROOM(ID_CLASSROOM, ID_BUILDING, CLASSROOM_FLOOR, CLASSROOM_FULLNAME,
                        CLASSROOM_DESCRIPTION, CLASSROOM_NAME, CLASSROOM_TYPE_NAME, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<CLASSROOM> {
                val CLASSROOMS = ArrayList<CLASSROOM>()
                for (index in 0..array.length() - 1) {
                    try {
                        val classroom = fromJson(array.getJSONObject(index))
                        if (null != classroom) CLASSROOMS.add(classroom)
                    } catch (ignored: JSONException) {
                    }

                }
                return CLASSROOMS
            }
        }
    }

    class BUILDING(//Классы для парсинга: здания

            val ID_BUILDING: Int, val ID_CAMPUS: Int, val BUILDING_TYPE_NAME: String, val BUILDING_NUMBER: Int,
            val BUILDING_FLOOR_COUNT: Int, val BUILDING_NAME: String, val HAS_GROUND_FLOOR: Boolean, val IS_DELETED: Boolean) {
        companion object {

            fun fromJson(`object`: JSONObject): BUILDING? {
                val ID_BUILDING = `object`.optInt("ID_BUILDING", 0)
                val ID_CAMPUS = `object`.optInt("ID_CAMPUS", 0)
                val BUILDING_TYPE_NAME = `object`.optString("BUILDING_TYPE_NAME", "")
                val BUILDING_NUMBER = `object`.optInt("BUILDING_NUMBER", 0)
                val BUILDING_FLOOR_COUNT = `object`.optInt("BUILDING_FLOOR_COUNT", 0)
                val BUILDING_NAME = `object`.optString("BUILDING_NAME", "")
                val IS_DELETED = `object`.optBoolean("IS_DELETED", false)
                val HAS_GROUND_FLOOR = `object`.optBoolean("HAS_GROUND_FLOOR", false)
                return BUILDING(ID_BUILDING, ID_CAMPUS, BUILDING_TYPE_NAME, BUILDING_NUMBER,
                        BUILDING_FLOOR_COUNT, BUILDING_NAME, HAS_GROUND_FLOOR, IS_DELETED)
            }

            fun fromJson(array: JSONArray): ArrayList<BUILDING> {
                val BUILDINGS = ArrayList<BUILDING>()
                for (index in 0..array.length() - 1) {
                    try {
                        val building = fromJson(array.getJSONObject(index))
                        if (null != building) BUILDINGS.add(building)
                    } catch (ignored: JSONException) {
                    }

                }
                return BUILDINGS
            }
        }
    }

    companion object {

        fun fromJson(`object`: JSONObject): UniversityInfo {

            val SEMESTERS = `object`.optJSONArray("SEMESTERS")
            val FACULTIES = `object`.optJSONArray("FACULTIES")
            val DEPARTMENTS = `object`.optJSONArray("DEPARTMENTS")
            val GROUPS = `object`.optJSONArray("GROUPS")
            val TEACHERS = `object`.optJSONArray("TEACHERS")
            val PAIRS = `object`.optJSONArray("PAIRS")
            val CAMPUSES = `object`.optJSONArray("CAMPUSES")
            val CLASSROOMS = `object`.optJSONArray("CLASSROOMS")
            val BUILDING = `object`.optJSONArray("BUILDINGS")
            val UNIVERSITY_FULLNAME = `object`.optString("UNIVERSITY_FULLNAME")
            val UNIVERSITY_SHORTNAME = `object`.optString("UNIVERSITY_SHORTNAME")
            val DAYS_IN_WEEK = `object`.optInt("DAYS_IN_WEEK")

            return UniversityInfo(UNIVERSITY_FULLNAME, UNIVERSITY_SHORTNAME,
                    DAYS_IN_WEEK, SEMESTERS, FACULTIES, DEPARTMENTS, GROUPS, TEACHERS, PAIRS,
                    CAMPUSES, CLASSROOMS, BUILDING)
        }
    }

}

