package savindev.myuniversity.perfomance;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;
import com.thin.downloadmanager.ThinDownloadManager;

import java.util.ArrayList;

import savindev.myuniversity.DownloadListAdapter;
import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.GroupsModel;

/**
 * Отвечает за внешний вид экрана с успеваемостью студентов
 * На альфа- и бета-версии содержит список с возможностью скачать рейтинг
 */
public class PerfomanceFragment extends Fragment {
    private static final int DOWNLOAD_THREAD_POOL_SIZE = 5;
    private ProgressBar pbar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        pbar = (ProgressBar)view.findViewById(R.id.progress);
        if (userInfo.contains("UserGroup")) {//Если есть группа пользователя в настройках, дать возможность на скачивание личного расписания
            view.findViewById(R.id.download_my_perf).setVisibility(View.VISIBLE);
            pbar.setVisibility(View.VISIBLE);
        }
        ThinDownloadManager downloadManager = new ThinDownloadManager(DOWNLOAD_THREAD_POOL_SIZE); //Для загрузки файлов

        //Заполнение списка групп
        ExpandableListView perfomance = (ExpandableListView) view.findViewById(R.id.perfomance);
        DBHelper dbHelper = DBHelper.getInstance(getActivity().getBaseContext());
        ArrayList<String> faculty = dbHelper.getFacultiesHelper().getFaculties(getActivity());
        //Создаем лист с группами
        ArrayList<ArrayList<GroupsModel>> models = new ArrayList<>();
        for (int i = 0; i < faculty.size(); i++) {
            models.add(dbHelper.getGroupsHelper().getGroups(getActivity(), faculty.get(i)));
        }

        PerfomanceListAdapter adapter = new PerfomanceListAdapter(getActivity().getApplicationContext(), faculty, models, downloadManager);
        perfomance.setAdapter(adapter);
        return view;
    }

    public void download(View view) {
        SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        if (MainActivity.isNetworkConnected(getActivity())) {
            GroupsModel main = DBHelper.getInstance(getActivity()).getUsedSchedulesHelper().getMainGroupModel(getActivity());
            String url = R.string.uri + "getPerfomance?group_id=" + main.getId();
            Uri downloadUri = Uri.parse(url);
            String destFolder = "/" + main.getName() + "-рейтинг.xlsx";
            Uri destinationUri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    + destFolder);
            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .setDestinationURI(destinationUri)
                    .setPriority(DownloadRequest.Priority.LOW)
                    .setDownloadListener(new DownloadStatusListener() {
                        @Override
                        public void onDownloadComplete(int id) {
                            pbar.setProgress(100);
                        }
                        @Override
                        public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                            Toast.makeText(getActivity(), "Не удалось", Toast.LENGTH_SHORT);
                            Log.i("myuniversity", "Ошибка от сервера, запрос getPerfomance, текст:"
                                    + errorMessage);
                            pbar.setProgress(0);
                        }
                        @Override
                        public void onProgress(int id, long totalBytes, long arg3, int progress) {
                            pbar.setProgress(progress);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Нет интернета", Toast.LENGTH_SHORT);
        }
    }

    public class PerfomanceListAdapter extends DownloadListAdapter {
        public PerfomanceListAdapter(Context context, ArrayList<String> names,
                                     ArrayList<ArrayList<GroupsModel>> groups, ThinDownloadManager downloadManager) {
            super(context, names, groups, downloadManager, true);
        }
    }

}
