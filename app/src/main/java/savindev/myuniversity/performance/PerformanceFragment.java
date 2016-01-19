package savindev.myuniversity.performance;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;

/**
 * Отвечает за внешний вид экрана с успеваемостью студентов
 * На альфа- и бета-версии содержит список с возможностью скачать рейтинг
 */
public class PerformanceFragment extends Fragment implements View.OnClickListener {
    private ProgressBar pbar;
    private int mainId;
    private String mainName;
    private Button download;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final ArrayList<DownloadModel> models = new ArrayList<>();
        final int mainGroupId = getActivity().getSharedPreferences("UserInfo", 0).getInt("UserGroup", 0);
        Thread downloadList = new Thread(new Runnable() {
            @Override
            public void run() {
                final int TIMEOUT_MILLISEC = 5000;
                //TODO метод для получения id нашего вуза
                String url = getActivity().getResources().getString(R.string.uri) + "getRaitingFileList?idUniversity=1&lastRefresh="
                        + getActivity().getResources().getString(R.string.unix);
                OkHttpClient client = new OkHttpClient();
                client.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
                client.setReadTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("CONTENT");
                    for (int index = 0; index < array.length(); index++) {
                        JSONObject object = array.getJSONObject(index);
                        final int ESTIMATION_POINT_NUMBER = object.optInt("ESTIMATION_POINT_NUMBER");
                        final String ESTIMATION_POINT_NAME = object.optString("ESTIMATION_POINT_NAME", "");
                        final JSONArray files = object.getJSONArray("RAITING_FILES");
                        ArrayList<PointModel> points = new ArrayList<>();
                        int ii = files.length();
                        for (int i = 0; i < files.length(); i++) {
                            JSONObject object1 = files.getJSONObject(i);
                            final int idGroup = object1.optInt("ID_GROUP", 0);
                            final int ID_PROGRESS_RAITNG_FILE = object1.optInt("ID_PROGRESS_RAITNG_FILE", 0);
                            final String name = object1.optString("FILE_NAME", "");
                            final PointModel model = new PointModel(idGroup, name, ID_PROGRESS_RAITNG_FILE);
                            if (idGroup == mainGroupId) {
                                mainId = ID_PROGRESS_RAITNG_FILE;
                                mainName = name;
                            }
                            points.add(model);
                        }
                        final DownloadModel model = new DownloadModel(points, ESTIMATION_POINT_NAME, ESTIMATION_POINT_NUMBER);
                        models.add(model);
                    }
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        downloadList.start();

        View view = inflater.inflate(R.layout.fragment_perfomance, container, false);
        SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        pbar = (ProgressBar) view.findViewById(R.id.progress);
        if (userInfo.contains("UserGroup")) {//Если есть группа пользователя в настройках, дать возможность на скачивание личного расписания
           download = (Button) view.findViewById(R.id.download_my_perf);
            download.setVisibility(View.VISIBLE);
            download.setOnClickListener(this);
        }

        //Заполнение списка групп
        ExpandableListView performance = (ExpandableListView) view.findViewById(R.id.perfomance);
        try {
            downloadList.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        DownloadPerformanceAdapter adapter = new DownloadPerformanceAdapter(getActivity().getApplicationContext(), models);
        performance.setAdapter(adapter);



        return view;
    }

    private void download() {
        if (MainActivity.isNetworkConnected(getActivity())) {
            PointModel model = new PointModel(mainId, mainName, mainId);
            new DownloadRaitingTask(pbar, model, getActivity(), download).execute();

//            String url = getActivity().getResources().getString(R.string.uri) + "getRaitingFile?idProgressRaitingFile=" + mainId;
//            Uri downloadUri = Uri.parse(url);
//            final String destFolder = "/" + mainName + ".xlsx";
//            final Uri destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                    + destFolder);
//            pbar.setVisibility(View.VISIBLE);
//            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
//                    .setDestinationURI(destinationUri)
//                    .setPriority(DownloadRequest.Priority.LOW)
//                    .setDownloadListener(new DownloadStatusListener() {
//                        @Override
//                        public void onDownloadComplete(int id) {
//                            pbar.setProgress(100);
//                            download.setText("Открыть");
//                            download.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    MimeTypeMap mime = MimeTypeMap.getSingleton();
//                                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                                            + destFolder);
//                                    Intent intent = new Intent();
//                                    intent.setAction(Intent.ACTION_VIEW);
//                                    Uri uri = Uri.fromFile(file);
//                                    intent.setDataAndType(uri, mime.getMimeTypeFromExtension("xlsx"));
//                                    startActivity(intent);
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onDownloadFailed(int id, int errorCode, String errorMessage) {
//                            Toast.makeText(getActivity(), "Не удалось", Toast.LENGTH_SHORT).show();
//                            Log.i("myuniversity", "Ошибка от сервера, запрос getPerfomance, текст:"
//                                    + errorMessage);
//                            pbar.setProgress(0);
//                        }
//
//                        @Override
//                        public void onProgress(int id, long totalBytes, long arg3, int progress) {
//                            pbar.setProgress(progress);
//                        }
//                    });
//            downloadManager.add(downloadRequest);
        } else {
            Toast.makeText(getActivity(), "Нет интернета", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_my_perf:
                download();
                break;
        }
    }


}
