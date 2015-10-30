package savindev.myuniversity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * В данном файле собраны подклассы для парсинга json'а инит. информации
 * Каждый подкласс имеет структуру, конструкторы, методы составления объектов
 * и ArrayList'ов из этих объектов.
 * Несколько подклассов связываются в основной класс, представляющий собой полноценный
 * json разбираемого запроса
 */
public class Initialization {
    public final String UNIVERSITY_FULLNAME;
    public final String UNIVERSITY_SHORTNAME;
    public final int DAYS_IN_WEEK;
    public final ArrayList<SEMESTER> SEMESTERS;
    public final ArrayList<PAIR> PAIRS;
    public final ArrayList<FACULTY> FACULTIES;
    public final ArrayList<DEPARTMENT> DEPARTMENTS;
    public final ArrayList<GROUP> GROUPS;
    public final ArrayList<TEACHER> TEACHERS;

    public Initialization(String UNIVERSITY_FULLNAME, String UNIVERSITY_SHORTNAME,
                          int DAYS_IN_WEEK, JSONArray SEMESTERS,
                          JSONArray FACULTIES, JSONArray DEPARTMENTS,
                          JSONArray GROUPS, JSONArray TEACHERS,JSONArray Pairs) {
        this.UNIVERSITY_FULLNAME = UNIVERSITY_FULLNAME;
        this.UNIVERSITY_SHORTNAME = UNIVERSITY_SHORTNAME;
        this.DAYS_IN_WEEK = DAYS_IN_WEEK;
        this.SEMESTERS = SEMESTER.fromJson(SEMESTERS);
        this.FACULTIES = FACULTY.fromJson(FACULTIES);
        this.DEPARTMENTS = DEPARTMENT.fromJson(DEPARTMENTS);
        this.GROUPS = GROUP.fromJson(GROUPS);
        this.TEACHERS = TEACHER.fromJson(TEACHERS);
        this.PAIRS = PAIR.fromJson(Pairs);
    }

    public static Initialization fromJson(final JSONObject object) {
        JSONArray SEMESTERS = null;
        JSONArray FACULTIES = null;
        JSONArray DEPARTMENTS = null;
        JSONArray GROUPS = null;
        JSONArray TEACHERS = null;
        JSONArray PAIRS = null;
        String UNIVERSITY_FULLNAME = "";
        String UNIVERSITY_SHORTNAME = "";
        int DAYS_IN_WEEK = 0;
        try {
            SEMESTERS = object.getJSONArray("SEMESTERS");
            FACULTIES = object.getJSONArray("FACULTIES");
            DEPARTMENTS = object.getJSONArray("DEPARTMENTS");
            GROUPS = object.getJSONArray("GROUPS");
            TEACHERS = object.getJSONArray("TEACHERS");
            PAIRS = object.getJSONArray("PAIRS");
            UNIVERSITY_FULLNAME = object.getString("UNIVERSITY_FULLNAME");
            UNIVERSITY_SHORTNAME = object.getString("UNIVERSITY_SHORTNAME");
            DAYS_IN_WEEK = object.getInt("DAYS_IN_WEEK");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return new Initialization(UNIVERSITY_FULLNAME, UNIVERSITY_SHORTNAME,
                DAYS_IN_WEEK, SEMESTERS, FACULTIES, DEPARTMENTS, GROUPS, TEACHERS,PAIRS);
    }

    public static class SEMESTER { //Классы для парсинга: семестры
        public final int ID_SEMESTER;
        public final String BEGIN_DT;
        public final String END_DT;
        public final boolean IS_DELETED;

        public SEMESTER(int ID_SEMESTER, String BEGIN_DT, String END_DT, boolean IS_DELETED) {
            this.ID_SEMESTER = ID_SEMESTER;
            this.BEGIN_DT = BEGIN_DT;
            this.END_DT = END_DT;
            this.IS_DELETED = IS_DELETED;
        }

        public static SEMESTER fromJson(final JSONObject object) {
            final int ID_SEMESTER = object.optInt("ID_SEMESTER", 0);
            final String BEGIN_DT = object.optString("BEGIN_DT", "");
            final String END_DT = object.optString("END_DT", "");
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new SEMESTER(ID_SEMESTER, BEGIN_DT, END_DT, IS_DELETED);
        }

        public static ArrayList<SEMESTER> fromJson(final JSONArray array) {
            final ArrayList<SEMESTER> SEMESTERS = new ArrayList<SEMESTER>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final SEMESTER semester = fromJson(array.getJSONObject(index));
                    if (null != semester) SEMESTERS.add(semester);
                } catch (final JSONException ignored) {
                }
            }
            return SEMESTERS;
        }
    }

    public static class PAIR { //Классы для парсинга: пары
        public final int ID_PAIR;
        public final int PAIR_NUMBER;
        public final String PAIR_BEGIN_TIME;
        public final String PAIR_END_TIME;
        public final boolean IS_DELETED;

        public PAIR(int ID_PAIR, int PAIR_NUMBER, String PAIR_BEGIN_TIME, String PAIR_END_TIME, boolean IS_DELETED) {
            this.ID_PAIR = ID_PAIR;
            this.PAIR_NUMBER = PAIR_NUMBER;
            this.PAIR_BEGIN_TIME = PAIR_BEGIN_TIME;
            this.PAIR_END_TIME = PAIR_END_TIME;
            this.IS_DELETED = IS_DELETED;
        }

        public static PAIR fromJson(final JSONObject object) {
            final int ID_PAIR = object.optInt("ID_PAIR", 0);
            final int PAIR_NUMBER = object.optInt("PAIR_NUMBER", 0);
            final String PAIR_BEGIN_TIME = object.optString("PAIR_BEGIN_TIME", "");
            final String PAIR_END_TIME = object.optString("PAIR_END_TIME", "");
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new PAIR(ID_PAIR, PAIR_NUMBER, PAIR_BEGIN_TIME, PAIR_END_TIME, IS_DELETED);
        }

        public static ArrayList<PAIR> fromJson(final JSONArray array) {
            final ArrayList<PAIR> PAIRS = new ArrayList<PAIR>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final PAIR pair = fromJson(array.getJSONObject(index));
                    if (null != pair) PAIRS.add(pair);
                } catch (final JSONException ignored) {
                }
            }
            return PAIRS;
        }
    }

    public static class FACULTY { //Классы для парсинга: факультеты

        public final int ID_FACULTY;
        public final String FACULTY_FULLNAME;
        public final String FACULTY_SHORTNAME;
        public final boolean IS_DELETED;

        public FACULTY(int ID_FACULTY, String FACULTY_FULLNAME, String FACULTY_SHORTNAME, boolean IS_DELETED) {
            this.ID_FACULTY = ID_FACULTY;
            this.FACULTY_FULLNAME = FACULTY_FULLNAME;
            this.FACULTY_SHORTNAME = FACULTY_SHORTNAME;
            this.IS_DELETED = IS_DELETED;
        }

        public static FACULTY fromJson(final JSONObject object) {
            final int ID_FACULTY = object.optInt("ID_FACULTY", 0);
            final String FACULTY_FULLNAME = object.optString("FACULTY_FULLNAME", "");
            final String FACULTY_SHORTNAME = object.optString("FACULTY_SHORTNAME", "");
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new FACULTY(ID_FACULTY, FACULTY_FULLNAME, FACULTY_SHORTNAME, IS_DELETED);
        }

        public static ArrayList<FACULTY> fromJson(final JSONArray array) {
            final ArrayList<FACULTY> FACULTIES = new ArrayList<FACULTY>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final FACULTY faculty = fromJson(array.getJSONObject(index));
                    if (null != faculty) FACULTIES.add(faculty);
                } catch (final JSONException ignored) {
                }
            }
            return FACULTIES;
        }
    }

    public static class DEPARTMENT { //Классы для парсинга: кафедры

        public final int ID_DEPARTMENT;
        public final int ID_FACULTY;
        public final int ID_CLASSROOM;
        public final String DEPARTMENT_FULLNAME;
        public final String DEPARTMENT_SHORTNAME;
        public final boolean IS_DELETED;

        public DEPARTMENT(int ID_DEPARTMENT, int ID_FACULTY, int ID_CLASSROOM,
                          String DEPARTMENT_FULLNAME, String DEPARTMENT_SHORTNAME, boolean IS_DELETED) {
            this.ID_DEPARTMENT = ID_DEPARTMENT;
            this.ID_FACULTY = ID_FACULTY;
            this.ID_CLASSROOM = ID_CLASSROOM;
            this.DEPARTMENT_FULLNAME = DEPARTMENT_FULLNAME;
            this.DEPARTMENT_SHORTNAME = DEPARTMENT_SHORTNAME;
            this.IS_DELETED = IS_DELETED;
        }

        public static DEPARTMENT fromJson(final JSONObject object) {
            final int ID_DEPARTMENT = object.optInt("ID_DEPARTMENT", 0);
            final int ID_FACULTY = object.optInt("ID_FACULTY", 0);
            final int ID_CLASSROOM = object.optInt("FACULTY_NAME", 0);
            final String DEPARTMENT_FULLNAME = object.optString("DEPARTMENT_FULLNAME", "");
            final String DEPARTMENT_SHORTNAME = object.optString("DEPARTMENT_SHORTNAME", "");
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new DEPARTMENT(ID_DEPARTMENT, ID_FACULTY, ID_CLASSROOM,
            DEPARTMENT_FULLNAME, DEPARTMENT_SHORTNAME, IS_DELETED);
        }

        public static ArrayList<DEPARTMENT> fromJson(final JSONArray array) {
            final ArrayList<DEPARTMENT> DEPARTMENTS = new ArrayList<DEPARTMENT>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final DEPARTMENT department = fromJson(array.getJSONObject(index));
                    if (null != department) DEPARTMENTS.add(department);
                } catch (final JSONException ignored) {
                }
            }
            return DEPARTMENTS;
        }
    }

    public static class GROUP { //Классы для парсинга: группы

        public final int ID_GROUP;
        public final int ID_FACULTY;
        public final String GROUP_NAME;
        public final boolean IS_DELETED;


        public GROUP(int ID_GROUP, int ID_FACULTY, String GROUP_NAME, boolean IS_DELETED) {
            this.ID_GROUP = ID_GROUP;
            this.ID_FACULTY = ID_FACULTY;
            this.GROUP_NAME = GROUP_NAME;
            this.IS_DELETED = IS_DELETED;
        }

        public static GROUP fromJson(final JSONObject object) {
            final int ID_GROUP= object.optInt("ID_GROUP", 0);
            final int ID_FACULTY= object.optInt("ID_FACULTY", 0);
            final String GROUP_NAME = object.optString("GROUP_NAME", "");
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new GROUP(ID_GROUP, ID_FACULTY, GROUP_NAME, IS_DELETED);
        }

        public static ArrayList<GROUP> fromJson(final JSONArray array) {
            final ArrayList<GROUP> GROUPS = new ArrayList<GROUP>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final GROUP group = fromJson(array.getJSONObject(index));
                    if (null != group) GROUPS.add(group);
                } catch (final JSONException ignored) {
                }
            }
            return GROUPS;
        }
    }

    public static class TEACHER { //Классы для парсинга: преподы

        public final int ID_TEACHER;
        public final int ID_DEPARTMENT;
        public final String TEACHER_LASTNAME;
        public final String TEACHER_FIRSTNAME;
        public final String TEACHER_MIDDLENAME;
        public final String GENDER;
        public final boolean IS_DELETED;

        public TEACHER(int ID_TEACHER, int ID_DEPARTMENT, String TEACHER_LASTNAME, String TEACHER_FIRSTNAME,
                       String TEACHER_MIDDLENAME, String GENDER, boolean IS_DELETED) {
            this.ID_TEACHER = ID_TEACHER;
            this.ID_DEPARTMENT = ID_DEPARTMENT;
            this.TEACHER_LASTNAME = TEACHER_LASTNAME;
            this.TEACHER_FIRSTNAME = TEACHER_FIRSTNAME;
            this.TEACHER_MIDDLENAME = TEACHER_MIDDLENAME;
            this.GENDER = GENDER;
            this.IS_DELETED = IS_DELETED;
        }

        public static TEACHER fromJson(final JSONObject object) {
            final int ID_TEACHER= object.optInt("ID_TEACHER", 0);
            final int ID_DEPARTMENT= object.optInt("ID_DEPARTMENT", 0);
            final String TEACHER_LASTNAME = object.optString("TEACHER_LASTNAME", "");
            final String TEACHER_FIRSTNAME = object.optString("TEACHER_FIRSTNAME", "");
            final String TEACHER_MIDDLENAME = object.optString("TEACHER_MIDDLENAME", "");
            final String GENDER = object.optString("GENDER", "");
            final boolean IS_DELETED = object.optBoolean("IS_DELETED", false);
            return new TEACHER(ID_TEACHER, ID_DEPARTMENT, TEACHER_LASTNAME, TEACHER_FIRSTNAME,
                    TEACHER_MIDDLENAME, GENDER, IS_DELETED);
        }

        public static  ArrayList<TEACHER> fromJson(final JSONArray array) {
            final ArrayList<TEACHER> TEACHERS = new ArrayList<TEACHER>();
            for (int index = 0; index < array.length(); ++index) {
                try {
                    final TEACHER teacher = fromJson(array.getJSONObject(index));
                    if (null != teacher) TEACHERS.add(teacher);
                } catch (final JSONException ignored) {
                }
            }
            return TEACHERS;
        }
    }

}

