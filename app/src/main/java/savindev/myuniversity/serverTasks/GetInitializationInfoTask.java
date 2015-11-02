package savindev.myuniversity.serverTasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
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

import savindev.myuniversity.Initialization;
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
    private Date lastModifiedDate;



    public GetInitializationInfoTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Возвращать false, если изменений нет
        String refreshDate = null;
        //Получить дату последнего обновления из sqlite
        //Если такой даты не хранится, записать "20000101000000"
//        sPref = context.getSharedPreferences("item_list", Context.MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        dateStr = new String(sPref.getString("DATE", ""));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
        Date date = null;
        try {
            date = (Date)formatter.parse(refreshDate); //хранящаяся дата
        } catch (Exception e1) {
            e1.printStackTrace();
            refreshDate = "20000101000000";
            try {
                date = (Date)formatter.parse(refreshDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String uri = context.getResources().getString(R.string.uri) + "getInitializationInfo?universityAcronym=" +
                context.getResources().getString(R.string.university) + "&lastRefresh=" +
                refreshDate;
        URL url;
        try {
            url = new URL(uri);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            String lastModified = urlConnection.getHeaderField("Last-modified");
            //Сверка полученной и хранящейся даты: если полученная меньше, данных нет
            //Если полученная дата больше, записать новые данные и новую дату
//            lastModifiedDate = (Date)formatter.parse(lastModified); //Дата с сервера
//            if (!refreshDate.equals("")) {
//                if (lastModifiedDate.getTime() == (date.getTime())) {
//                    Log.d("11", "equ");
//                    current = true;
//                    //Если даты совпадают, изменений не требуется
//                    return false;
//                }
//            }
//            ed.putString("DATE",lastModified).apply(); //Если не совпадают, занести новую дату
//          В sqlite
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
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private void parseReply(String reply) throws JSONException {
        //здесь разбор json и раскладка в sqlite
       Initialization init = null;
        JSONObject obj = null;
        obj = new JSONObject(reply);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                JSONObject content = obj.getJSONObject("CONTENT");
                init = Initialization.fromJson(content);
                parsetoSqlite(init);
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


    void parsetoSqlite(Initialization init){

        SQLiteDatabase sqliteDatabase;
        DBHelper helper = new DBHelper(context);
        sqliteDatabase = helper.getWritableDatabase();

        //PARSE UNIVERSITY INFO TO SQLITE

            ContentValues uninfoRow = new ContentValues();
            uninfoRow .put(DBHelper.UniversityInfoHelper.COL_FULLNAME, init.UNIVERSITY_FULLNAME);
            uninfoRow .put(DBHelper.UniversityInfoHelper.COL_SHORTNAME, init.UNIVERSITY_SHORTNAME);
            uninfoRow .put(DBHelper.UniversityInfoHelper.COL_DAYS_IN_WEEK, init.DAYS_IN_WEEK);
            sqliteDatabase.insert(DBHelper.UniversityInfoHelper.TABLE_NAME, null, uninfoRow);

        //PARSE TEACHERS TO SQLITE
           ContentValues teacherRow = new ContentValues();
            for(int index = 0 ; index < init.TEACHERS.size(); ++index) {
                teacherRow.put(DBHelper.TeachersHelper.COL_ID_TEACHER, init.TEACHERS.get(index).ID_TEACHER);
                teacherRow.put(DBHelper.TeachersHelper.COL_ID_DEPARTMENT, init.TEACHERS.get(index).ID_DEPARTMENT);
                teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_LASTNAME, init.TEACHERS.get(index).TEACHER_LASTNAME);
                teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_FIRSTNAME, init.TEACHERS.get(index).TEACHER_FIRSTNAME);
                teacherRow.put(DBHelper.TeachersHelper.COL_TEACHER_MIDDLENAME, init.TEACHERS.get(index).TEACHER_MIDDLENAME);

                Log.d("TEACHERS_INFO", teacherRow.toString());
                sqliteDatabase.insert(DBHelper.TeachersHelper.TABLE_NAME, null, teacherRow);
            }


        //PARSE SEMESTRES TO SQLITE
             ContentValues semestresRow = new ContentValues();
             for(int index = 0 ; index < init.SEMESTERS.size();++index) {
               semestresRow.put(DBHelper.SemestersHelper.COL_ID_SEMESTER, init.SEMESTERS.get(index).ID_SEMESTER);
               semestresRow.put(DBHelper.SemestersHelper.COL_BEGIN_DATE,init.SEMESTERS.get(index).BEGIN_DT);
                 semestresRow.put(DBHelper.SemestersHelper.COL_END_DATE, init.SEMESTERS.get(index).END_DT);

                 Log.d("SEMESTRES_INFO", semestresRow.toString());
                sqliteDatabase.insert(DBHelper.SemestersHelper.TABLE_NAME, null, semestresRow);
        }

        //PARSE PAIRS TO SQLITE
        ContentValues pairsRow = new ContentValues();
        for(int index = 0 ; index < init.PAIRS.size();++index) {
            pairsRow.put(DBHelper.PairsHelper.COL_ID_PAIR, init.PAIRS.get(index).ID_PAIR);
            pairsRow.put(DBHelper.PairsHelper.COL_PAIR_NUMBER ,init.PAIRS.get(index).PAIR_NUMBER);
            pairsRow.put(DBHelper.PairsHelper.COL_BEGIN_TIME,init.PAIRS.get(index).PAIR_BEGIN_TIME);
            pairsRow.put(DBHelper.PairsHelper.COL_END_TIME, init.PAIRS.get(index).PAIR_END_TIME);

            Log.d("PAIRS_INFO", pairsRow.toString());
            sqliteDatabase.insert(DBHelper.PairsHelper.TABLE_NAME, null, pairsRow);
        }

        //TODO ADD GROUPS,DEPARTMENTS,FACULTIES

        //PARSE GROUPS TO SQLITE
        ContentValues groupsRow = new ContentValues();
        for(int index = 0 ; index < init.GROUPS.size();++index) {
            groupsRow.put(DBHelper.GroupsHelper.COL_ID_GROUP, init.GROUPS.get(index).ID_GROUP);
            groupsRow.put(DBHelper.GroupsHelper.COL_GROUP_NAME, init.GROUPS.get(index).GROUP_NAME);
            groupsRow.put(DBHelper.GroupsHelper.COL_ID_FACULTY, init.GROUPS.get(index).ID_FACULTY);

            Log.d("GROUPS_INFO", groupsRow.toString());
            sqliteDatabase.insert(DBHelper.GroupsHelper.TABLE_NAME, null, groupsRow);
        }

        //PARSE DEPARTMENTS TO SQLITE
        ContentValues departmentsRow = new ContentValues();
        for(int index = 0 ; index < init.DEPARTMENTS.size();++index) {
            departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_ID, init.DEPARTMENTS.get(index).ID_DEPARTMENT);
            departmentsRow.put(DBHelper.DepartmentsHelper.COL_FACULTY_ID, init.DEPARTMENTS.get(index).ID_FACULTY);
            departmentsRow.put(DBHelper.DepartmentsHelper.COL_CLASSROOM_ID, init.DEPARTMENTS.get(index).ID_CLASSROOM);
            departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_FULLNAME, init.DEPARTMENTS.get(index).DEPARTMENT_FULLNAME);
            departmentsRow.put(DBHelper.DepartmentsHelper.COL_DEPARTMENT_SHORTNAME, init.DEPARTMENTS.get(index).DEPARTMENT_SHORTNAME);

            Log.d("GROUPS_INFO", departmentsRow.toString());
            sqliteDatabase.insert(DBHelper.DepartmentsHelper.TABLE_NAME, null, departmentsRow);
        }




    }




    @Override
    protected void onPostExecute(Boolean data) { //Предупреждение, если список пустой
        if (!data) {
            Toast.makeText(context, "Сервер недоступен", Toast.LENGTH_LONG).show();
        }

    }

}
