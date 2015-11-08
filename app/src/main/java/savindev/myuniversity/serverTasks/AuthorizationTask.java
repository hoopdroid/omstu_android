package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.HashSet;

import savindev.myuniversity.MainActivity;
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


        //Первый запрос
        String uri = context.getResources().getString(R.string.uri) + "getSalt?universityAcronym=" +
                context.getResources().getString(R.string.university) + "&login=" +
                params[0]; //Строка запроса на получение соли по логину. params[0] - логин
        String result = query(uri);
        if (result == null) {
            return false;
        }
        String salt = "";
        try {
            salt = parseSalt(result); // Разбор результата
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        if (salt == null || salt.isEmpty()) { //Если соль пуста, дальше работать нет смысла. Выдать ошибку
            return false;

        } else { //Иначе - второй запрос на сервер
            uri = context.getResources().getString(R.string.uri) + "authorization?universityAcronym=" +
                    context.getResources().getString(R.string.university) + "&login=" +
                    params[0] + "&passwordHash=" + md5(params[1] + salt).toUpperCase();
            result = query(uri);
            if (result == null) {
                return false;
            }
        }

        try {
            return parseContent(result);
        } catch (JSONException e) {
            return false;
        }
    }

    private String query(String uri) { //Запрос к серверу. по uri возвращает ответ
        URL url;
        String reply;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(uri);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
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
            urlConnection.disconnect();
        }
        return reply;
    }

    private static final String md5(final String s) {
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
        JSONObject obj = null;
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
        JSONObject obj = null;
        obj = new JSONObject(result);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                JSONObject content = obj.getJSONObject("CONTENT");
                int id = content.getInt("ID_USER");
                String lastname = content.getString("USER_LASTNAME");
                String firstname = content.getString("USER_FIRSTNAME");
                String middlename = content.getString("USER_MIDDLENAME");
                int groupId = content.getInt("ID_GROUP");
                //Это все рапихать по sqlite, предварительно получить по id группы саму группу

                //savesettings();
                //Записать эту группу в лист активных расписаний как основную
                //Получить список id не-основных активных расписаний
                //Проверить, если ли среди них id группы авторизовавшегося - основной
                //Для записи в таблицу: id = id, name = (взять из локальной базы по id), isGroup=true (пока преподы не предусмотрены), lastrefresh = 20000101000000
                //Если нет, провести следующие действия:
                //Получить расписание для этой группы, если ранее оно не было получено
//                GetScheduleTask gst = new GetScheduleTask(context, null);
//                gst.execute(тут параметры основной группы);

                SharedPreferences settings = context.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();


                //TODO Add all user info in Preferences
                editor.putString("UserLastName",lastname);
                editor.putString("UserFirstName", firstname);
                editor.putString("UserMiddleName", middlename);
                editor.putInt("UserGroup", groupId);
                editor.commit();

                DBHelper dbHelper = DBHelper.getInstance(context);
                dbHelper.getUsedSchedulesHelper().setSchedule(context, 1,groupId,"group","true","20000101000000");



                //После выполнения вышеописанных процедур удалить следующий код:
                GetScheduleTask gst = new GetScheduleTask(context, null);
                gst.execute(new GroupsModel(null, id, true, "20000101000000"));


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


    private void saveSettings(){
        //TODO ADD
    }
}
