package savindev.myuniversity.performance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import co.mobiwise.library.ProgressLayout;
import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.serverTasks.DownloadFileTask;
import savindev.myuniversity.serverTasks.GetRaitingFileListTask;

/**
 * Отвечает за внешний вид экрана с успеваемостью студентов
 * На альфа- и бета-версии содержит список с возможностью скачать рейтинг
 */
public class PerformanceFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private PointModel main;
    private Button download;
    private ProgressLayout pl;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean refreshing = false;
    private GetRaitingFileListTask grflt;
    private ArrayList<RatingModel> models;
    private DownloadPerformanceAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        models = new ArrayList<>();
        models = DBHelper.getInstance(getActivity()).getRatingHelper().getRatingModels();
        if (models.isEmpty())
            if (MainActivity.isNetworkConnected(getActivity())) {
                grflt = new GetRaitingFileListTask(getActivity(), mSwipeRefreshLayout);
                grflt.execute();
            } else
                Toast.makeText(getActivity(), "Необходимо подключение к интернету", Toast.LENGTH_LONG).show();


//        final int mainGroupId = getActivity().getSharedPreferences("UserInfo", 0).getInt("UserGroup", 0);
//        Thread downloadList = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final int TIMEOUT_MILLISEC = 5000;
//                //TODO метод для получения id нашего вуза
//                String url = getActivity().getResources().getString(R.string.uri) + "getRaitingFileList?idUniversity=1&lastRefresh="
//                        + getActivity().getResources().getString(R.string.unix);
//                OkHttpClient client = new OkHttpClient();
//                client.setConnectTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
//                client.setReadTimeout(TIMEOUT_MILLISEC, TimeUnit.MILLISECONDS);
//                Request request = new Request.Builder().url(url).build();
//                try {
//                    Response response = client.newCall(request).execute();
//                    JSONArray array = new JSONObject(response.body().string()).getJSONArray("CONTENT");
//                    for (int index = 0; index < array.length(); index++) {
//                        JSONObject object = array.getJSONObject(index);
//                        final int ESTIMATION_POINT_NUMBER = object.optInt("ESTIMATION_POINT_NUMBER");
//                        final String ESTIMATION_POINT_NAME = object.optString("ESTIMATION_POINT_NAME", "");
//                        final JSONArray files = object.getJSONArray("RAITING_FILES");
//                        ArrayList<PointModel> points = new ArrayList<>();
//                        for (int i = 0; i < files.length(); i++) {
//                            JSONObject object1 = files.getJSONObject(i);
//                            final int idGroup = object1.optInt("ID_GROUP", 0);
//                            final int ID_PROGRESS_RAITNG_FILE = object1.optInt("ID_PROGRESS_RAITNG_FILE", 0);
//                            final String name = object1.optString("FILE_NAME", "");
//                            final PointModel model = new PointModel(idGroup, name, ID_PROGRESS_RAITNG_FILE);
//                            if (idGroup == mainGroupId) {
//                                main = new PointModel(mainId, mainName, mainId);
//                                mainId = ID_PROGRESS_RAITNG_FILE;
//                                mainName = name;
//                            }
//                            points.add(model);
//                        }
//                        final RaitingModel model = new RaitingModel(points, ESTIMATION_POINT_NAME, ESTIMATION_POINT_NUMBER);
//                        models.add(model);
//                    }
//                } catch (IOException | JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        downloadList.start();

        View view = inflater.inflate(R.layout.fragment_perfomance, container, false);
        download = (Button) view.findViewById(R.id.download_my_perf);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        //Заполнение списка групп
        ExpandableListView performance = (ExpandableListView) view.findViewById(R.id.perfomance);


        if (models.isEmpty())
            try {
                main = grflt.get();
                models = DBHelper.getInstance(getActivity()).getRatingHelper().getRatingModels();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        else
            setMainGroup();

//        try {
//            downloadList.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        adapter = new DownloadPerformanceAdapter(getActivity().getApplicationContext(), models);
        performance.setAdapter(adapter);

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("FINISH_UPDATE"));
        getActivity().registerReceiver(broadcastReceiverNotFound, new IntentFilter("NOT_FOUND"));
        return view;
    }

    private void setMainGroup() {
        SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if (userInfo.contains("UserGroup")) {//Если есть группа пользователя в настройках, дать возможность на скачивание личного расписания
//            pl = (ProgressLayout) view.findViewById(R.id.progressLayout);
            final int mainGroupId = getActivity().getSharedPreferences("UserInfo", 0).getInt("UserGroup", 0);
            forbr:
            for (RatingModel model : models)
                for (PointModel pModel : model.getPoints())
                    if (pModel.getIdGroup() == mainGroupId) {
                        main = pModel;
                        download.setVisibility(View.VISIBLE);
//            pl.setVisibility(View.VISIBLE);
                        download.setOnClickListener(this);
                        break forbr;
                    }
        }
    }

    private void download() {
        if (refreshing) {
            setMainGroup();
            refreshing = false;
        }
        if (MainActivity.isNetworkConnected(getActivity())) {
            new DownloadFileTask(pl, main, getActivity(), download).execute();
        } else {
            Toast.makeText(getActivity(), "Нет интернета", Toast.LENGTH_SHORT).show();
        }

    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            models = new ArrayList<>();
            DBHelper dbHelper = DBHelper.getInstance(getActivity());
            models = dbHelper.getRatingHelper().getRatingModels();
            adapter.notifyDataSetChanged();
        }
    };

    BroadcastReceiver broadcastReceiverNotFound = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getActivity().getFragmentManager().popBackStack();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.download_my_perf:
                download();
                break;
        }
    }

    @Override
    public void onRefresh() {
        if (MainActivity.isNetworkConnected(getActivity())) {
            refreshing = false;
        }
        mSwipeRefreshLayout.setRefreshing(true);
        GetRaitingFileListTask grflt = new GetRaitingFileListTask(getActivity(), mSwipeRefreshLayout);
        grflt.execute();
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDetach();
    }
}
