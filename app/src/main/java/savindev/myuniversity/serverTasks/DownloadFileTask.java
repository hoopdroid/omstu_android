package savindev.myuniversity.serverTasks;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import co.mobiwise.library.ProgressLayout;
import savindev.myuniversity.R;
import savindev.myuniversity.performance.PointModel;


public class DownloadFileTask extends AsyncTask<Void, String, Boolean> {
    private ProgressLayout pl;
    private PointModel model;
    private Context context;
    private View cv;
    private File file;
    private JSONObject json;

    public DownloadFileTask(ProgressLayout pl, PointModel model, Context context, View cv) {
        this.pl = pl;
        this.model = model;
        this.context = context;
        this.cv = cv;
    }

    @Override
    protected void onPreExecute() {
        pl.setCurrentProgress(0);
    }

    @Override
    protected Boolean doInBackground(Void[] params) {
        final int TIMEOUT_MILLISEC = 5000;
        String url = context.getResources().getString(R.string.uri) + "getRaitingFile?idProgressRaitingFile=" +
                model.getID_PROGRESS_RAITNG_FILE();
        FileOutputStream f = null;
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setConnectTimeout(TIMEOUT_MILLISEC);
            c.connect();
            int lengthOfFile = c.getContentLength();
            //this is where the file will be seen after the download
            file = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), model.getName() + ".xlsx");
            if (!file.exists()) {
                file.createNewFile();
            }
            f = new FileOutputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //file input is from the url
            InputStream in = c.getInputStream();
            //here’s the download code
            byte[] buffer = new byte[1024];
            int len1;
            long total = 0;
            while ((len1 = in.read(buffer)) > 0) {
                total += len1;
                publishProgress("" + (int) ((total * 100) / lengthOfFile));
//                f.write(buffer, 0, len1);
                baos.write(buffer, 0, len1);
            }

            try {
                json = new JSONObject(baos.toString("UTF-8")); //Получилось преобразовать в json - значит, в файле ошибка
                return false;
            } catch (JSONException e) {
                //Если не получилось, все ок и у нас файл
                f.write(baos.toByteArray());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (f != null)
                    f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        model.setFileUri(Uri.fromFile(file));
        return true;
    }

    protected void onProgressUpdate(String[] progress) {
        pl.setCurrentProgress(Integer.parseInt(progress[0]));
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            pl.setCurrentProgress(100);
            if (cv.getId() == R.id.download_my_perf)
                ((TextView) cv).setText("Открыть");
            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromFile(file);
                    MimeTypeMap mime = MimeTypeMap.getSingleton();
                    intent.setDataAndType(uri, mime.getMimeTypeFromExtension("xlsx"));
                    PackageManager packageManager = context.getPackageManager();
                    List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                    if (!activities.isEmpty())
                        context.getApplicationContext().startActivity(intent);
                }
            });
        } else {
            if (json != null) {
                String type = json.optString("STATE");
                String message = json.optJSONObject("CONTENT").optString("ERROR_MESSAGE");
                Toast.makeText(context, type + ": " + message, Toast.LENGTH_LONG).show();
            }
            pl.cancel();

        }
    }


}
