package savindev.myuniversity.serverTasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;


/**
 * Запрос на сервер о сборе инициализирующей информации по выбранному ВУЗу
 * В качестве параметров передается акроним вуза и дата последнего обновления
 * Возвращаемая информация: наименование ВУЗа, информация о семестрах ВУЗа,
 * число учебных дней в неделю, информация о парах ВУЗа,
 * факультетах, кафедрах, группах, преподавателях.
 * После запроса на сервер информация разбирается с помощью подклассов класса Parsers
 */
public class GetInitializationInfoTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    final private int TIMEOUT_MILLISEC = 5000;
    int errorCode = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public GetInitializationInfoTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
        super();
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Возвращать false, если изменений нет
        SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
        String refreshDate = settings.getString("init_last_refresh", "20000101000000"); //дата последнего обновления
        String uri = context.getResources().getString(R.string.uri) + "getInitializationInfo?universityAcronym=" +
                context.getResources().getString(R.string.university) + "&lastRefresh=" +
                refreshDate;
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String reply = "";
            reply = buffer.toString();
            urlConnection.disconnect();
            if (reply.isEmpty()) { //Если после всех операций все равно пустой
                return false;
            }
            parseReply(reply);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        finally {
            urlConnection.disconnect();
        }

        return true;
    }

    private void parseReply(String reply) throws JSONException {
        //здесь разбор json и раскладка в sqlite
        Initialization init = null;
        JSONObject obj = null;
        obj = new JSONObject(reply);
        sw:    switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                //Сверка полученной и хранящейся даты: если полученная меньше, данных нет
                //Если полученная дата больше, записать новые данные и новую дату

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
                SharedPreferences settings = context.getSharedPreferences("UserInfo", 0);
                String modified = obj.getString("LAST_REFRESH");
                String date = settings.getString("init_last_refresh", ""); // Старая записанная дата обновления
                if (!(date.equals(""))) { //Если хранящаяся дата не пуста
                    Date lastModifiedDate = null; //Полученная от сервера дата
                    Date oldModifiedDate = null;
                    try {
                        lastModifiedDate = (Date)formatter.parse(modified); //Дата с сервера
                        oldModifiedDate = (Date)formatter.parse(date); //Дата с файла
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (lastModifiedDate.getTime() == (oldModifiedDate.getTime())) {
                        errorCode = 1; //1 - изменения не требуются
                        break sw;
                    }
                }
                settings.edit().putString("init_last_refresh", modified).apply(); //Если не совпадают, занести новую дату
                JSONObject content = obj.getJSONObject("CONTENT");
                init = Initialization.fromJson(content);
                parsetoSqlite(init);
                Log.d("DOWNLOADING DATA", "SUCCESS");
                break;
            case "ERROR":   //Неопознанная ошибка
                Log.i("myuniversity", "Ошибка ERROR от сервера, запрос GetInitializationInfoTask, текст:"
                        + obj.get("CONTENT"));
                break;
            case "WARNING": //Определенная сервером ошибка
                Log.i("myuniversity", "Ошибка WARNING от сервера, запрос GetInitializationInfoTask, текст:"
                        + obj.get("CONTENT"));
                break;
        }


    }


    void parsetoSqlite(Initialization init) {

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();

        //PARSE UNIVERSITY INFO TO SQLITE

        ContentValues uninfoRow = new ContentValues();
        uninfoRow.put(DBHelper.UniversityInfoHelper.COL_FULLNAME, init.UNIVERSITY_FULLNAME);
        uninfoRow.put(DBHelper.UniversityInfoHelper.COL_SHORTNAME, init.UNIVERSITY_SHORTNAME);
        uninfoRow.put(DBHelper.UniversityInfoHelper.COL_DAYS_IN_WEEK, init.DAYS_IN_WEEK);
        sqliteDatabase.insert(DBHelper.UniversityInfoHelper.TABLE_NAME, null, uninfoRow);

        //PARSE TEACHERS TO SQLITE
        ContentValues teacherRow = new ContentValues();
        for (int index = 0; index < init.TEACHERS.size(); index++) {
            if(!init.TEACHERS.get(index).IS_DELETED){
            teacherRow.put(DBHelper.TeachersHelper.COL_ID_TEACHER, init.TEACHERS.get(index).ID_TEACHER);
            teacherRow.put(DBHelper.TeachersHelper.COL_ID_DEPARTMENT, init.TEACHERS.get(index).ID_DEPARTMENT);
            teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_LASTNAME, init.TEACHERS.get(index).TEACHER_LASTNAME);
            teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_FIRSTNAME, init.TEACHERS.get(index).TEACHER_FIRSTNAME);
            teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_MIDDLENAME, init.TEACHERS.get(index).TEACHER_MIDDLENAME);
            sqliteDatabase.insert(DBHelper.TeachersHelper.TABLE_NAME, null, teacherRow);}
            else{
                DBHelper.delete_byID(sqliteDatabase, DBHelper.TeachersHelper.TABLE_NAME, DBHelper.TeachersHelper.COL_ID_TEACHER, init.TEACHERS.get(index).ID_TEACHER);
            }




        }

        //PARSE SEMESTRES TO SQLITE
        ContentValues semestresRow = new ContentValues();
        for (int index = 0; index < init.SEMESTERS.size(); index++) {
            if(!init.SEMESTERS.get(index).IS_DELETED) {
                semestresRow.put(DBHelper.SemestersHelper.COL_ID_SEMESTER, init.SEMESTERS.get(index).ID_SEMESTER);
                semestresRow.put(DBHelper.SemestersHelper.COL_BEGIN_DATE, init.SEMESTERS.get(index).BEGIN_DT);
                semestresRow.put(DBHelper.SemestersHelper.COL_END_DATE, init.SEMESTERS.get(index).END_DT);
                sqliteDatabase.insert(DBHelper.SemestersHelper.TABLE_NAME, null, semestresRow);
            }
            else{
                DBHelper.delete_byID(sqliteDatabase, DBHelper.SemestersHelper.TABLE_NAME, DBHelper.SemestersHelper.COL_ID_SEMESTER,init.SEMESTERS.get(index).ID_SEMESTER);
            }
        }

        //PARSE PAIRS TO SQLITE
        ContentValues pairsRow = new ContentValues();
        for (int index = 0; index < init.PAIRS.size(); index++) {
            if(!init.PAIRS.get(index).IS_DELETED) {
                pairsRow.put(DBHelper.PairsHelper.COL_ID_PAIR, init.PAIRS.get(index).ID_PAIR);
                pairsRow.put(DBHelper.PairsHelper.COL_PAIR_NUMBER, init.PAIRS.get(index).PAIR_NUMBER);
                pairsRow.put(DBHelper.PairsHelper.COL_BEGIN_TIME, init.PAIRS.get(index).PAIR_BEGIN_TIME);
                pairsRow.put(DBHelper.PairsHelper.COL_END_TIME, init.PAIRS.get(index).PAIR_END_TIME);
                sqliteDatabase.insert(DBHelper.PairsHelper.TABLE_NAME, null, pairsRow);
            }
            else {
                DBHelper.delete_byID(sqliteDatabase, DBHelper.PairsHelper.TABLE_NAME, DBHelper.PairsHelper.COL_ID_PAIR,init.PAIRS.get(index).ID_PAIR);
            }
        }

        //PARSE GROUPS TO SQLITE
        ContentValues groupsRow = new ContentValues();
        for (int index = 0; index < init.GROUPS.size(); index++) {
            if(!init.GROUPS.get(index).IS_DELETED) {
                groupsRow.put(DBHelper.GroupsHelper.COL_ID_GROUP, init.GROUPS.get(index).ID_GROUP);
                groupsRow.put(DBHelper.GroupsHelper.COL_GROUP_NAME, init.GROUPS.get(index).GROUP_NAME);
                groupsRow.put(DBHelper.GroupsHelper.COL_ID_FACULTY, init.GROUPS.get(index).ID_FACULTY);
                sqliteDatabase.insert(DBHelper.GroupsHelper.TABLE_NAME, null, groupsRow);
            }
            else{
                DBHelper.delete_byID(sqliteDatabase, DBHelper.GroupsHelper.TABLE_NAME, DBHelper.GroupsHelper.COL_ID_GROUP,init.GROUPS.get(index).ID_GROUP);
            }
        }

        //PARSE DEPARTMENTS TO SQLITE
        ContentValues departmentsRow = new ContentValues();
        for (int index = 0; index < init.DEPARTMENTS.size(); index++) {
            if(!init.DEPARTMENTS.get(index).IS_DELETED) {
                departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_ID, init.DEPARTMENTS.get(index).ID_DEPARTMENT);
                departmentsRow.put(DBHelper.DepartmentsHelper.COL_FACULTY_ID, init.DEPARTMENTS.get(index).ID_FACULTY);
                departmentsRow.put(DBHelper.DepartmentsHelper.COL_CLASSROOM_ID, init.DEPARTMENTS.get(index).ID_CLASSROOM);
                departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_FULLNAME, init.DEPARTMENTS.get(index).DEPARTMENT_FULLNAME);
                departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_SHORTNAME, init.DEPARTMENTS.get(index).DEPARTMENT_SHORTNAME);
                sqliteDatabase.insert(DBHelper.DepartmentsHelper.TABLE_NAME, null, departmentsRow);
            }
            else {
                DBHelper.delete_byID(sqliteDatabase, DBHelper.DepartmentsHelper.TABLE_NAME, DBHelper.DepartmentsHelper.COL_DEPARTMENT_ID,init.DEPARTMENTS.get(index).ID_DEPARTMENT);
            }
        }

        //PARSE FACULTIES TO SQLITE
        ContentValues facultiesRow = new ContentValues();
        for(int index = 0 ; index < init.FACULTIES.size();index++) {
            if(!init.FACULTIES.get(index).IS_DELETED) {
                facultiesRow.put(DBHelper.FacultiesHelper.COL_FACULTY_ID, init.FACULTIES.get(index).ID_FACULTY);
                facultiesRow.put(DBHelper.FacultiesHelper.COL_FACULTY_FULLNAME, init.FACULTIES.get(index).FACULTY_FULLNAME);
                facultiesRow.put(DBHelper.FacultiesHelper.COL_FACULTY_SHORTNAME, init.FACULTIES.get(index).FACULTY_SHORTNAME);
                sqliteDatabase.insert(DBHelper.FacultiesHelper.TABLE_NAME, null, facultiesRow);
            }
            else {
                DBHelper.delete_byID(sqliteDatabase, DBHelper.FacultiesHelper.TABLE_NAME, DBHelper.FacultiesHelper.COL_FACULTY_ID,init.FACULTIES.get(index).ID_FACULTY);
            }
        }


    }


    @Override
    protected void onPostExecute(Boolean data) { //Предупреждение, если список пустой
        if (mSwipeRefreshLayout != null) { //Если вызывалось из фрагмента настроек групп
            mSwipeRefreshLayout.setRefreshing(false); //Завершить показывать прогресс
            if (data && errorCode != 1) { //Имеется новое содержимое, обновить данные
                context.sendBroadcast(new Intent("FINISH_UPDATE")); //Отправить запрос на обновление
            }
        }

        if (!data) {
            Toast.makeText(context, "Сервер недоступен", Toast.LENGTH_LONG).show();
        }

    }

}
