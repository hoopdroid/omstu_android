package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import savindev.myuniversity.R;

/**
 * Created by Katena on 20.10.2015.
 */
public class GetMainInfoTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private SharedPreferences sPref;
    private String item_list = null;
    private String takenJson = "";
    final private int TIMEOUT_MILLISEC = 5000;
    private static InputStream is = null;
    private Date lastModifiedDate;
    private boolean current = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(Void... params) {
        //Возвращать false, если изменений нет
        String dateStr = null;
//        sPref = context.getSharedPreferences("item_list", Context.MODE_PRIVATE);
//        SharedPreferences.Editor ed = sPref.edit();
//        dateStr = new String(sPref.getString("DATE", ""));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.getDefault());
        //Вместо выше написанного - получение даты из sqlite
        Date date = null;
        try {
            date = (Date)formatter.parse(dateStr); //хранящаяся дата
        } catch (ParseException e1) {
            e1.printStackTrace();
            dateStr = "20000101000000";
            try {
                date = (Date)formatter.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String uri = context.getResources().getString(R.string.uri);
        URL url;
        try {
            url = new URL(uri + "getMainInfo" + "?last_refresh_dt=" + dateStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            String lastModified = urlConnection.getHeaderField("Last-modified");
            lastModifiedDate = (Date)formatter.parse(lastModified); //Дата с сервера
            if (!dateStr.equals("")) {
                if (lastModifiedDate.getTime() == (date.getTime())) {
                    current = true;
                    //Если даты совпадают, изменений не требуется
                    return false;
                }
            }
//            ed.putString("DATE",lastModified).apply(); //Если не совпадают, занести новую дату
//          В sqlite
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            takenJson = buffer.toString();
            urlConnection.disconnect();
            if (takenJson.isEmpty()) { //Если после всех операций все равно пустой
                return false;
            }
            globalParse();
        } catch (Exception e){
            return false;
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean data) { //Предупреждение, если список пустой
        if (!data && !current) {
            Toast.makeText(context, "Не удалось получить списки" + '\n'
                    + "Возможно, сервер недоступен", Toast.LENGTH_LONG).show();
        }
//        else {
//            if (item != null) {
//                item.setActionView(R.layout.actionbar_finish); //В случае успешной загрузки показать галочку на месте progressbar, через секунду скрыть
//                new CountDownTimer(1000, 1000) {
//                    public void onTick(long millisUntilFinished) {}
//                    public void onFinish() {
//                        item.setVisible(false);
//                    }
//                }.start();
//            }
//        }
    }

    private void globalParse() {
        //здесь разбор json и раскладка в sqlite
    }

}
