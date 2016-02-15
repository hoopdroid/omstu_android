package savindev.myuniversity.serverTasks;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.performance.PointModel;
import savindev.myuniversity.performance.RatingModel;

public class GetRaitingFileListTask extends AsyncTask<Void, Void, PointModel> {
    private Context context;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public GetRaitingFileListTask(Context context, SwipeRefreshLayout mSwipeRefreshLayout) {
        super();
        this.context = context;
        this.mSwipeRefreshLayout = mSwipeRefreshLayout;
    }


    @Override
    protected PointModel doInBackground(Void... params) {
        final int TIMEOUT_MILLISEC = 5000;
        //TODO метод для получения id нашего вуза
        String url = context.getResources().getString(R.string.uri) + "getRaitingFileList?idUniversity=1&lastRefresh="
                + context.getResources().getString(R.string.unix);
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        client.setReadTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
        Request request = new Request.Builder().url(url).build();
        final int mainGroupId = context.getSharedPreferences("UserInfo", 0).getInt("UserGroup", 0);
        PointModel result = new PointModel(0, null, 0);
        final ArrayList<RatingModel> models = new ArrayList<>();
        try {
            Response response = client.newCall(request).execute();
            JSONObject reply = new JSONObject(response.body().string());

            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
            String modified = reply.getString("LAST_REFRESH");
            String date = context.getSharedPreferences("settings", 0).getString("raiting_last_refresh", ""); // Старая записанная дата обновления
            if (!(date.equals(""))) { //Если хранящаяся дата не пуста
                try {
                    Date lastModifiedDate = formatter.parse(modified); //Дата с сервера
                    Date oldModifiedDate = formatter.parse(date); //Дата с файла
                    if (lastModifiedDate.getTime() == (oldModifiedDate.getTime())) {
                        return null;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            JSONArray array = reply.getJSONArray("CONTENT");
            for (int index = 0; index < array.length(); index++) {
                JSONObject object = array.getJSONObject(index);
                final int ESTIMATION_POINT_NUMBER = object.optInt("ESTIMATION_POINT_NUMBER");
                final String ESTIMATION_POINT_NAME = object.optString("ESTIMATION_POINT_NAME", "");
                final JSONArray files = object.getJSONArray("RAITING_FILES");
                ArrayList<PointModel> points = new ArrayList<>();
                for (int i = 0; i < files.length(); i++) {
                    JSONObject object1 = files.getJSONObject(i);
                    final int idGroup = object1.optInt("ID_GROUP", 0);
                    final int ID_PROGRESS_RAITNG_FILE = object1.optInt("ID_PROGRESS_RAITNG_FILE", 0);
                    final String name = object1.optString("FILE_NAME", "");
                    final PointModel model = new PointModel(idGroup, name, ID_PROGRESS_RAITNG_FILE);
                    if (idGroup == mainGroupId) {
                        result = model;
                    }
                    points.add(model);
                }
                final RatingModel model = new RatingModel(points, ESTIMATION_POINT_NAME, ESTIMATION_POINT_NUMBER);
                models.add(model);
            }
            DBHelper.getInstance(context).getRatingHelper().setRatingModels(context, models);
            context.getSharedPreferences("settings", 0).edit().putString("raiting_last_refresh", modified).apply();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(PointModel data) {
        if (mSwipeRefreshLayout != null) { //Если вызывалось из фрагмента настроек групп
            mSwipeRefreshLayout.setRefreshing(false); //Завершить показывать прогресс
        }
        if (data != null) { //Имеется новое содержимое, обновить данные
            Toast.makeText(context, "Файлы рейтинга обновлены!", Toast.LENGTH_LONG).show();
            context.sendBroadcast(new Intent("FINISH_UPDATE")); //Отправить запрос на обновление
        } else
            Toast.makeText(context, "Не удалось получить расписание" + '\n'
                    + "Проверьте соединение с сервером", Toast.LENGTH_LONG).show();
    }
}