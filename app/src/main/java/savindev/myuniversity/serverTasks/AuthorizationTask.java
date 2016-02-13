package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import savindev.myuniversity.R;
import savindev.myuniversity.db.UsedSchedulesHelper;
import savindev.myuniversity.schedule.GroupsModel;

/**
 * Запрос на сервер об авторизации пользователя. Происходит только по запросу пользователя
 * Первым этапом - отправка на сервер логина и получение соли
 * Соль пароля алгоритмом В_ВЕРХНИЙ_РЕГИСТР(MD5(Чистый пароль + соль))
 * Отправка на сервер пары логин - соленый пароль, по результату авторизация
 */

public class AuthorizationTask extends AsyncTask<String, Void, Boolean> {
    private Context context;
    static final private int TIMEOUT_MILLISEC = 5000;
    private int errorCode = 0;
    private String login;
    String passwordHash;

    public AuthorizationTask(Context context) {
        super();
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        //Возвращать false при провале авторизации
        login = params[0];
        //Первый запрос
        JSONObject json = new JSONObject(); //Составление json для отправки в post
        try {
            json.put("UNIVERSITY_ACRONYM", context.getResources().getString(R.string.university));
            json.put("LOGIN", login);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = context.getResources().getString(R.string.uri) + "getSalt"; //Строка запроса на получение соли по логину
        String result = query(url, json);
        if (result == null) {
            return false;
        }
        String salt;
        try {
            salt = parseSalt(result); // Разбор результата
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        if (salt == null || salt.isEmpty()) //Если соль пуста, дальше работать нет смысла. Выдать ошибку
            return false;

        //Второй запрос
        json = new JSONObject(); //Составление json для отправки в post
        passwordHash = md5(params[1] + salt).toUpperCase();
        try {
            json.put("UNIVERSITY_ACRONYM", context.getResources().getString(R.string.university));
            json.put("LOGIN", login);
            json.put("PASSWORD_HASH", passwordHash);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        url = context.getResources().getString(R.string.uri) + "authorization";
        result = query(url, json);
        if (result == null) {
            return false;
        }
        try {
            return parseContent(result);
        } catch (JSONException e) {
            return false;
        }
    }

    private String query(String url, JSONObject json) { //Запрос к серверу. по uri возвращает ответ
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        client.setWriteTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder().url(url).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String parseSalt(String result) throws JSONException {
        JSONObject obj;
        obj = new JSONObject(result);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                return obj.getJSONObject("CONTENT").getString("SALT");
            case "ERROR":   //Неопознанная ошибка
                if (obj.getJSONObject("CONTENT").get("ERROR_CODE").toString().equals("GET_SALT-002")) {
                    errorCode = 1;
                } else {
                    Log.i("myuniversity", "Ошибка ERROR от сервера, запрос AuthorizationTask.getSalt, текст:"
                            + obj.get("CONTENT"));
                }
                break;
            case "WARNING": //Определенная сервером ошибка
                Log.i("myuniversity", "Ошибка WARNING от сервера, запрос AuthorizationTask.getSalt, текст:"
                        + obj.get("CONTENT"));
                break;
        }
        return null;
    }

    private boolean parseContent(String result) throws JSONException {
        JSONObject obj;
        obj = new JSONObject(result);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                JSONObject content = obj.getJSONObject("CONTENT");
                int groupId = content.getInt("ID_GROUP");

                //Сохранение данных о пользователе
                SharedPreferences settings = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("UserLastName", content.getString("USER_LASTNAME"));
                editor.putString("UserFirstName", content.getString("USER_FIRSTNAME"));
                editor.putString("UserMiddleName", content.getString("USER_MIDDLENAME"));
                editor.putInt("UserGroup", groupId);
                editor.putString("email", login);
                editor.putString("password", passwordHash);
                editor.putInt("UserId", content.getInt("ID_USER"));
                editor.apply();

                UsedSchedulesHelper.setUsedSchedule(context, groupId, true, true, "20000101000000"); //запись нового основного в таблицу

                ArrayList<GroupsModel> models = UsedSchedulesHelper.getGroupsModelList(context); //Получить список id не-основных активных расписаний
                forbreak:
                {
                    for (GroupsModel model : models) { //Проверить, если ли среди них id группы авторизовавшегося - основной
                        if (model.getId() == groupId) {
                            break forbreak;
                        }
                    }
                    //Получить расписание для этой группы, если ранее оно не было получено

                    GetScheduleTask gst = new GetScheduleTask(context, null);
                    gst.execute(UsedSchedulesHelper.getMainGroupModel(context));
                }
                return true;
            case "ERROR":   //Неопознанная ошибка
                if (obj.getJSONObject("CONTENT").getString("ERROR_CODE").equals("AUTHORIZATION-002")) {
                    errorCode = 2;
                } else {
                    Log.i("myuniversity", "Ошибка ERROR от сервера, запрос AuthorizationTask.getSalt, текст:"
                            + obj.get("CONTENT"));
                }
                break;
            case "WARNING": //Определенная сервером ошибка
                Log.i("myuniversity", "Ошибка WARNING от сервера, запрос AuthorizationTask.getSalt, текст:"
                        + obj.get("CONTENT"));
                break;
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean data) { //Предупреждение, если список пустой
        if (!data) {
            switch (errorCode) {
                case 1:
                    Toast.makeText(context, "Не существует такого логина. Зарегистрируйтесь", Toast.LENGTH_LONG).show(); //Вывод сообщения об ошибке
                    break;
                case 2:
                    Toast.makeText(context, "Неверный пароль", Toast.LENGTH_LONG).show(); //Вывод сообщения об ошибке. Предложить восстановить пароль
                    break;
                default:
                    Toast.makeText(context, "Сервер недоступен", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

}
