package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import savindev.myuniversity.R;

/**
 * Created by Katena on 20.10.2015.
 */
public class RegistrationTask extends AsyncTask<String, Void, Boolean> {
    private Context context;
    private String item_list = null;
    private String takenJson = "";
    final private int TIMEOUT_MILLISEC = 5000;
    private static InputStream is = null;
    private boolean current = false;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Boolean doInBackground(String... params) {
        //Возвращать false, если изменений нет
        String uri = context.getResources().getString(R.string.uri) + "regisrtation";
        StringBuilder builder = new StringBuilder(uri);
        builder.append("?login=" + params[0]);
        builder.append("&password=" + params[1]);
        builder.append("&lname=" + params[2]);
        builder.append("&name=" + params[3]);
        builder.append("&group=" + params[4]);
        builder.append("&sex=" + params[5]);
        uri = builder.toString();
        URL url;
        try {
            url = new URL(uri);

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(TIMEOUT_MILLISEC);
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
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
            Toast.makeText(context, "Не удалось зарегистрироваться" + '\n'
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
        //здесь анализ результата
    }

}
