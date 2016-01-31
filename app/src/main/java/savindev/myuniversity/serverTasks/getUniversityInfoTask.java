package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.welcomescreen.FirstStartActivity;


/**
 * Запрос на сервер о сборе инициализирующей информации по выбранному ВУЗу
 * В качестве параметров передается акроним вуза и дата последнего обновления
 * Возвращаемая информация: наименование ВУЗа, информация о семестрах ВУЗа,
 * число учебных дней в неделю, информация о парах ВУЗа,
 * факультетах, кафедрах, группах, преподавателях.
 * После запроса на сервер информация разбирается с помощью подклассов класса Parsers
 */
public class GetUniversityInfoTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private int errorCode = 0;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SharedPreferences settings;


    public GetUniversityInfoTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
        super();
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final int TIMEOUT_MILLISEC = 5000;
        //Возвращать false, если изменений нет
        settings = context.getSharedPreferences("settings", 0);
        String refreshDate = settings.getString("init_last_refresh", context.getResources().getString(R.string.unix)); //дата последнего обновления
        String url;
        url = context.getResources().getString(R.string.uri) + "getUniversityInfo?universityAcronym=" +
                context.getResources().getString(R.string.university) + "&lastRefresh=" + refreshDate;
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            parseReply(response.body().string());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void parseReply(String reply) throws JSONException {
        //здесь разбор json и раскладка в sqlite
        JSONObject obj = new JSONObject(reply);
        switch (obj.get("STATE").toString()) {//определение типа полученного результата
            case "MESSAGE": //Получен адекватный результат
                //Сверка полученной и хранящейся даты: если полученная меньше, данных нет
                //Если полученная дата больше, записать новые данные и новую дату

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
                String modified = obj.getString("LAST_REFRESH");
                String date = settings.getString("init_last_refresh", ""); // Старая записанная дата обновления
                if (!(date.equals(""))) { //Если хранящаяся дата не пуста
                    try {
                        Date lastModifiedDate = formatter.parse(modified); //Дата с сервера
                        Date oldModifiedDate = formatter.parse(date); //Дата с файла
                        if (lastModifiedDate.getTime() == (oldModifiedDate.getTime())) {
                            errorCode = 1; //1 - изменения не требуются
                            break;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                JSONObject content = obj.getJSONObject("CONTENT");
                parsetoSqlite(UniversityInfo.fromJson(content));
                settings.edit().putString("init_last_refresh", modified).apply(); //Если не совпадают, занести новую дату
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
            case "NOT_FOUND": //Нет новых данных
                errorCode = 1;
                break;
        }
    }

    void parsetoSqlite(UniversityInfo init) {
        DBHelper dbHelper = DBHelper.getInstance(context);
        dbHelper.getUniversityInfoHelper().setUniversityInfo(context, init);
        dbHelper.getTeachersHelper().setTeachers(context, init);
        dbHelper.getSemestersHelper().setSemesters(context, init);
        dbHelper.getPairsHelper().setPairs(context, init);
        dbHelper.getGroupsHelper().setGroups(context, init);
        dbHelper.getFacultiesHelper().setFaculties(context, init);
        dbHelper.getDepartmentsHelper().setDepartments(context, init);
        dbHelper.getCampusesHelper().setCampuses(init, context);
        dbHelper.getClassroomsHelper().setClassrooms(init, context);
        dbHelper.getBuildingsHelper().setBuildings(init, context);

    }


    @Override
    protected void onPostExecute(Boolean data) { //Предупреждение, если список пустой
        if(FirstStartActivity.btnSkip!=null)
        FirstStartActivity.btnSkip.setEnabled(true);

        if(FirstStartActivity.progressBar!=null)
            FirstStartActivity.progressBar.setVisibility(View.GONE);

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
