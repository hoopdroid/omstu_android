package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.GroupsModel;

public class GetScheduleTask extends AsyncTask<GroupsModel, Void, Integer> {
    private Context context;
    private final static int TIMEOUT_MILLISEC = 5000;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GroupsModel[] params;
    private int errorCode;

    public GetScheduleTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
        super();
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }


    @Override
    protected Integer doInBackground(GroupsModel... params) {

        this.params = params;
        String body = null; //Тело запроса
        JSONArray GROUPS = new JSONArray(); //Составление json для отправки в post
        JSONArray TEACHERS = new JSONArray();
        for (int i = 0; i < params.length; i++) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("LAST_REFRESH", params[i].getLastRefresh());
                if (params[i].isGroup()) {
                    obj.put("ID_GROUP", params[i].getId());
                    GROUPS.put(obj);
                } else {
                    obj.put("ID_TEACHER", params[i].getId());
                    TEACHERS.put(obj);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        JSONObject obj = new JSONObject();
        try {
            obj.put("GROUPS", (GROUPS.length() == 0) ? JSONObject.NULL : GROUPS);
            obj.put("TEACHERS", (TEACHERS.length() == 0) ? JSONObject.NULL : TEACHERS);
//        if (GROUPS.length() == 0) {
//            obj.put("GROUPS", JSONObject.NULL);
//        } else {
//            obj.put("GROUPS", GROUPS);
//        }
//        if (TEACHERS.length() == 0) {
//            obj.put("TEACHERS", JSONObject.NULL);
//        } else {
//            obj.put("TEACHERS", TEACHERS);
//        }

            body = obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String uri = context.getResources().getString(R.string.uri) + "getSchedule";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            urlConnection.setReadTimeout(TIMEOUT_MILLISEC);
            urlConnection.setDoOutput(true); //Отправка json
            OutputStream output = urlConnection.getOutputStream();
            output.write(body.getBytes("UTF-8"));
            output.close();
            errorCode = urlConnection.getResponseCode();

            InputStream inputStream = urlConnection.getErrorStream(); //Получение результата
            if (inputStream == null) {
                inputStream = urlConnection.getInputStream();
            }
            StringBuffer buffer = new StringBuffer();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reader.close();
            String reply = buffer.toString();

            if (reply.isEmpty()) { //Если ответ пустой, вернуть ошибку
                return -1;
            }
            replyParse(reply);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            urlConnection.disconnect();
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer data) {
        if (mSwipeRefreshLayout != null) { //Если вызывалось из фрагмента расписания
            mSwipeRefreshLayout.setRefreshing(false); //Завершить показывать прогресс
            if (data > 0) { //Имеется новое содержимое, обновить данные
                context.sendBroadcast(new Intent("FINISH_UPDATE")); //Отправить запрос на обновление
            }
        }
        if (data == -1)
            Toast.makeText(context, "Не удалось получить расписание" + '\n'
                    + "Проверьте соединение с сервером", Toast.LENGTH_LONG).show();
    }


    private boolean replyParse(String reply) throws JSONException {
        //здесь разбор json и раскладка в sqlite
        if (errorCode != 200) {
            //TODO обрабатывать коды возврата
            return false;
        }
        JSONObject obj = new JSONObject(reply);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                JSONObject content = obj.getJSONObject("CONTENT");
                String lastResresh = obj.getString("LAST_REFRESH"); //дата обновления, в таблицу дат
                ArrayList<Schedule> sched = null;
                ArrayList<ScheduleDates> scheddates = null;
                try {
                    sched = Schedule.fromJson(content.getJSONArray("SCHEDULES"));
                    parsetoSqlite(sched);
                } catch (JSONException e) {
                    //Поле оказалось нулевым?
                    e.printStackTrace();
                }
                try {
                    scheddates = ScheduleDates.fromJson(content.getJSONArray("SCHEDULE_DATES"));
//                    parsetoSqlite(scheddates);
                } catch (JSONException e) {
                    //Поле оказалось нулевым?
                    e.printStackTrace();
                }
                addToScheduleList(lastResresh);
                break;
            case "ERROR":   //Неопознанная ошибка
                Log.i("myuniversity", "Ошибка ERROR от сервера, запрос GetScheduleTask, текст:"
                        + obj.get("CONTENT"));
                break;
            case "WARNING": //Определенная сервером ошибка
                Log.i("myuniversity", "Ошибка WARNING от сервера, запрос GetScheduleTask, текст:"
                        + obj.get("CONTENT"));
                break;
        }
        return false;
    }

    private void parsetoSqlite(ArrayList<Schedule> sched) {
        //TODO Parse Schedule to SQlite
        DBHelper dbHelper = DBHelper.getInstance(context);
        dbHelper.getSchedulesHelper().setSchedule(context, sched);


    }

    private void addToScheduleList(String lastResresh) { //Внос в список используемых расписаний
        for (GroupsModel model : params) {
            if (DBHelper.UsedSchedulesHelper.getGroupsModelList(context).contains(model)) {
                //Если уже имеется - обновить дату
                //TODO обновить дату при появлении метода в БД
            } else if (DBHelper.UsedSchedulesHelper.getMainGroupModel(context) != null &&
                    DBHelper.UsedSchedulesHelper.getMainGroupModel(context).equals(model)) {
                //Если уже имеется - обновить дату
                //TODO обновить дату при появлении метода в БД
            } else {
                //Не имеется, добавить
                DBHelper.UsedSchedulesHelper.setUsedSchedule(context, model.getId(), model.isGroup(), false, lastResresh);
            }
        }
    }
}