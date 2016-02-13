package savindev.myuniversity.settings;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.github.jorgecastilloprz.FABProgressCircle;
import com.github.jorgecastilloprz.listeners.FABProgressListener;
import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.db.SchedulesHelper;
import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.serverTasks.GetScheduleTask;
import savindev.myuniversity.serverTasks.GetUniversityInfoTask;


public class GroupsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private ExpandableListView list;
    private ExpListAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private FloatingActionButton fab;
    private FABProgressCircle fabProgressCircle;
    private boolean fail = false;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_groups_settings, container, false);
        list = (ExpandableListView) view.findViewById(R.id.list);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fabProgressCircle = (FABProgressCircle) view.findViewById(R.id.fabProgressCircle);
        fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabProgressCircle.show();
                save();
            }
        });
        fabProgressCircle.attachListener(new FABProgressListener() {
            @Override
            public void onFABProgressAnimationEnd() {

            }
        });
        parse();

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("FINISH_UPDATE"));
        getActivity().registerReceiver(broadcastReceiverDownloadFinish, new IntentFilter("FINISH_DOWNLOAD"));
        MainActivity.fab.hide();
        return view;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            parse();
        }
    };

    BroadcastReceiver broadcastReceiverDownloadFinish = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (fail) {
                fabProgressCircle.hide();
            } else {
                fab.hide();
                fabProgressCircle.hide();
            }
        }
    };

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        GetUniversityInfoTask guit = new GetUniversityInfoTask(getActivity().getBaseContext(), mSwipeRefreshLayout);
        guit.execute(); //Выполняем запрос на получение нужных расписаний
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_settings, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Поиск");
        searchView.setMaxWidth(getActivity().getResources().getDisplayMetrics().widthPixels / 2);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void parse() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity().getBaseContext());
        ArrayList<String> faculty = dbHelper.getFacultiesHelper().getFaculties(getActivity());
        ArrayList<String> departments = dbHelper.getDepartmentsHelper().getDepartments(getActivity());
        ArrayList<String> parents = new ArrayList<>(); //Список родителей, состоит из факультетов и кафедр
        parents.addAll(faculty);
        parents.addAll(departments);
        //Создаем лист с группами
        ArrayList<ArrayList<GroupsModel>> models = new ArrayList<>();
        for (int i = 0; i < faculty.size(); i++) {
            models.add(dbHelper.getGroupsHelper().getGroups(getActivity(), faculty.get(i)));
        }
        for (int i = 0; i < departments.size(); i++) {
            models.add(dbHelper.getTeachersHelper().getTeachers(getActivity(), departments.get(i)));
        }

        adapter = new ExpListAdapter(getActivity().getApplicationContext(), parents, models, fab);
        list.setAdapter(adapter); //При тыке на пункт меню
    }

    public void save() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArrayList<GroupsModel> addList = adapter.getAddList(); //Запрос на сервер для загрузки, внести в новый список
                ArrayList<GroupsModel> deleteList = adapter.getDeleteList(); //удалить из БД расписаний и из списка групп

                if (!addList.isEmpty()) {
                    //Запрос к базе
                    GetScheduleTask gst = new GetScheduleTask(getActivity().getBaseContext(), null);
                    if (MainActivity.isNetworkConnected(getActivity())) {
                        for (GroupsModel model : addList) {
                            model.setLastRefresh(getActivity().getResources().getString(R.string.unix)); //Установка даты последнего обновления - нет обновлений
                        }
                        gst.execute(addList.toArray(new GroupsModel[addList.size()])); //Выполняем запрос на получение нужных расписаний
                        try {
                            fail = gst.get() == -1;
                        } catch (Exception e) {
                            fail = true;
                            e.printStackTrace();
                        }
                    } else {
                        fail = true;
                    }
                    //Занести новые данные в список - в getScheduleTask, после успешной загрузки
                }

                if (!deleteList.isEmpty()) { //Если есть группы для удаления - удалить
                    for (GroupsModel model : deleteList) {
                        if (model.isGroup())
                            SchedulesHelper.deleteGroupSchedule(getActivity().getBaseContext(), model.getId());
                        else
                            SchedulesHelper.deleteTeacherchedule(getActivity().getBaseContext(), model.getId());
                    }
                }
                adapter.deleteLists(); //Подчистить на случай повторного сохранения
                getActivity().sendBroadcast(new Intent("FINISH_DOWNLOAD"));
            }
        }).start();
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(broadcastReceiverDownloadFinish);
        super.onDetach();
    }
}


