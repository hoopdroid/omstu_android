package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.GroupsModel;

/**
 * Запрос на сервер о сборе расписания для выбранных групп и преподавателей (возможно несколько)
 * В качестве параметров в теле запроса передается список id групп и преподавателей с датой последнего обновления по каждому
 * Возвращаемая информация: основное расписание и модификации для него, в общем виде для всех запрошенных групп
 * После запроса на сервер информация разбирается с помощью класса Schedule
 */

public class GetScheduleTask extends AsyncTask<GroupsModel, Void, Integer> {
    private Context context;
    private GroupsModel[] params;
    private int errorCode;

    public GetScheduleTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
        super();
        this.context = context;
    }


    @Override
    protected Integer doInBackground(GroupsModel... params) {
        final int TIMEOUT_MILLISEC = 5000;
        this.params = params;
        JSONArray GROUPS = new JSONArray(); //Составление json для отправки в post
        JSONArray TEACHERS = new JSONArray();
        for (GroupsModel param : params) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("LAST_REFRESH", param.getLastRefresh());
                if (param.isGroup()) {
                    obj.put("ID_GROUP", param.getId());
                    GROUPS.put(obj);
                } else {
                    obj.put("ID_TEACHER", param.getId());
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = context.getResources().getString(R.string.uri) + "getSchedule";

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        RequestBody body = RequestBody.create(JSON, obj.toString());
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            replyParse(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return 1;
    }

    @Override
    protected void onPostExecute(Integer data) {

        if (data > 0 && errorCode != 1) { //Имеется новое содержимое, обновить данные
            Toast.makeText(context, "Расписание обновлено!", Toast.LENGTH_LONG).show();
        }
        if (data == -1)
            Toast.makeText(context, "Не удалось получить расписание" + '\n'
                    + "Проверьте соединение с сервером", Toast.LENGTH_LONG).show();
        context.sendBroadcast(new Intent("FINISH_UPDATE")); //Отправить запрос на обновление
    }


    private void replyParse(String reply) throws JSONException {
        //здесь разбор json и раскладка в sqlite
        if (errorCode != 200)
            return;
        JSONObject obj = new JSONObject(reply);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                JSONObject content = obj.getJSONObject("CONTENT");
                String lastResresh = obj.getString("LAST_REFRESH"); //дата обновления, в таблицу дат
                ArrayList<Schedule> sched;
                ArrayList<ScheduleDates> scheddates;
                try {
                    sched = Schedule.fromJson(content.getJSONArray("SCHEDULES"));
                    DBHelper dbHelper = DBHelper.getInstance(context);
                    dbHelper.getSchedulesHelper().setSchedule(context, sched);
                } catch (JSONException e) { //Поле оказалось нулевым?
                    e.printStackTrace();
                }
//                try {
//                    scheddates = ScheduleDates.fromJson(content.getJSONArray("SCHEDULE_DATES"));
//                } catch (JSONException e) {
//                    //Поле оказалось нулевым?
//                    e.printStackTrace();
//                }
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
            case "NOT_FOUND": //Нет новых данных
                errorCode = 1;
                break;
        }
    }

    private void addToScheduleList(String lastRefresh) { //Внос в список используемых расписаний
        for (GroupsModel model : params) {
            if (DBHelper.UsedSchedulesHelper.getGroupsModelList(context).contains(model) ||
                    DBHelper.UsedSchedulesHelper.getMainGroupModel(context) != null &&
                            DBHelper.UsedSchedulesHelper.getMainGroupModel(context).equals(model)) { //Если уже имеется - обновить дату
                DBHelper.UsedSchedulesHelper.updateRefreshDate(context, model.getId(), model.isGroup(), lastRefresh);
            } else { //Не имеется, добавить
                DBHelper.UsedSchedulesHelper.setUsedSchedule(context, model.getId(), model.isGroup(), false, lastRefresh);
            }
        }
    }
}