package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.GroupsModel;

/**
 * Запрос на сервер об авторизации пользователя. Происходит только по запросу пользователя
 * <p/>
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
        String uri;
        if (context.getSharedPreferences("settings", Context.MODE_PRIVATE).getBoolean("test", false)) {
            uri = context.getResources().getString(R.string.uri_test) + "getSalt?universityAcronym=" +
                    context.getResources().getString(R.string.university) + "&login=" + login; //Строка запроса на получение соли по логину
        } else {
            uri = context.getResources().getString(R.string.uri) + "getSalt?universityAcronym=" +
                    context.getResources().getString(R.string.university) + "&login=" + login; //Строка запроса на получение соли по логину
        }
        String result = query(uri);
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
        if (salt == null || salt.isEmpty()) { //Если соль пуста, дальше работать нет смысла. Выдать ошибку
            return false;

        } else { //Иначе - второй запрос на сервер
            passwordHash = md5(params[1] + salt).toUpperCase();
            uri = context.getResources().getString(R.string.uri) + "authorization?universityAcronym=" +
                    context.getResources().getString(R.string.university) + "&login=" +
                    params[0] + "&passwordHash=" + passwordHash;
            result = query(uri);
            if (result == null) {
                return false;
            }
        }

//        try {
//            return parseContent(result);
//        } catch (JSONException e) {
            return false;
//        }
    }

    private String query(String uri) { //Запрос к серверу. по uri возвращает ответ
        URL url;
        String reply;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            urlConnection.setReadTimeout(TIMEOUT_MILLISEC);
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            reply = buffer.toString();
            if (reply.isEmpty()) { //Если после всех операций все равно пустой
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return reply;
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
                if (obj.getJSONObject("CONTENT").get("ERROR_CODE").toString().equals("E0001")) {
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

                DBHelper.UsedSchedulesHelper.setUsedSchedule(context, groupId, true, true, "20000101000000"); //запись нового основного в таблицу

                ArrayList<GroupsModel> models = DBHelper.UsedSchedulesHelper.getGroupsModelList(context); //Получить список id не-основных активных расписаний
                forbreak:
                {
                    for (GroupsModel model : models) { //Проверить, если ли среди них id группы авторизовавшегося - основной
                        if (model.getId() == groupId) {
                            break forbreak;
                        }
                    }
                    //Получить расписание для этой группы, если ранее оно не было получено

                    GetScheduleTask gst = new GetScheduleTask(context);
                    gst.execute(DBHelper.UsedSchedulesHelper.getMainGroupModel(context));
                }


                return true;
            case "ERROR":   //Неопознанная ошибка
                if (obj.getJSONObject("CONTENT").get("ERROR_CODE").toString().equals("E0001")) {
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
