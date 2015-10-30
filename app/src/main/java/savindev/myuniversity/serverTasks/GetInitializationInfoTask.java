package savindev.myuniversity.serverTasks;

import android.content.Context;
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
        int i = init.DEPARTMENTS.get(1).ID_CLASSROOM; //Пример получения объекта (а вообще тут точку остановки для просмотра всего объекта удобно ставить)
    }


    @Override
    protected void onPostExecute(Boolean data) { //Предупреждение, если список пустой
        if (!data) {
            Toast.makeText(context, "Сервер недоступен", Toast.LENGTH_LONG).show();
        }

    }

}
