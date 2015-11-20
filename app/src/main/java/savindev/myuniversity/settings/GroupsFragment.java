package savindev.myuniversity.settings;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.Toast;

import java.util.ArrayList;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.serverTasks.GetInitializationInfoTask;
import savindev.myuniversity.serverTasks.GetScheduleTask;


public class GroupsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    ExpandableListView list;
    SharedPreferences sPref;
    ExpListAdapter adapter;
    MenuItem refreshItem;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    MenuItem searchItem;
    SearchView searchView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_groups_settings, container, false);
        list = (ExpandableListView) view.findViewById(R.id.list);

        sPref = getActivity().getSharedPreferences("item_list", Context.MODE_PRIVATE);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        parse();

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("FINISH_UPDATE"));
        return view;
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            parse();
        }
    };

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        GetInitializationInfoTask giit = new GetInitializationInfoTask(getActivity().getBaseContext(), mSwipeRefreshLayout);
        giit.execute(); //Выполняем запрос на получение нужных расписаний
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.groups_settings, menu);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint("Поиск");
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
        refreshItem = (MenuItem) menu.findItem(R.id.download_pb);
        refreshItem.setActionView(R.layout.actionbar_progress);
        refreshItem.setVisible(false);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                save();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void parse() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity().getBaseContext());
        ArrayList<String> faculty = dbHelper.getFacultiesHelper().getFaculties(getActivity());
        ArrayList<String> departments = dbHelper.getDepartmentsHelper().getDepartments(getActivity());
        ArrayList<String> parents = new ArrayList<String>(); //Список родителей, состоит из факультетов и кафедр
        parents.addAll(faculty);
        parents.addAll(departments);
        //Создаем лист с группами
        ArrayList<ArrayList<GroupsModel>> models = new ArrayList<ArrayList<GroupsModel>>();
        for (int i = 0; i < faculty.size(); i++) {
            models.add(dbHelper.getGroupsHelper().getGroups(getActivity(), faculty.get(i)));
        }
        for (int i = 0; i < departments.size(); i++) {
            models.add(dbHelper.getTeachersHelper().getTeachers(getActivity(), departments.get(i)));
        }


        adapter = new ExpListAdapter(getActivity().getApplicationContext(), parents, models);
        list.setAdapter(adapter); //При тыке на пункт меню
    }

    public void save() {
        ArrayList<GroupsModel> addList = adapter.getAddList(); //Запрос на сервер для загрузки, внести в новый список
        ArrayList<GroupsModel> deleteList = adapter.getDeleteList(); //удалить из БД расписаний и из списка групп

        if (!addList.isEmpty()) {
            //Запрос к базе
            GetScheduleTask gst = new GetScheduleTask(getActivity().getBaseContext(), null);
            if (MainActivity.isNetworkConnected(getActivity())) {
                refreshItem.setActionView(R.layout.actionbar_progress); //Показать загрузку данных
                refreshItem.setVisible(true);
                for (GroupsModel model : addList) {
                    model.setLastRefresh("20000101000000"); //Установка даты последнего обновления - нет обновлений
                }
                gst.execute(addList.toArray(new GroupsModel[addList.size()])); //Выполняем запрос на получение нужных расписаний
                try {  //TODO сделать красивое отображение загрузки
                    if (gst.get() == -1) {
                        refreshItem.setVisible(false);
                    } else {
                        refreshItem.setActionView(R.layout.actionbar_finish); //В случае успешной загрузки показать галочку на месте progressbar, через секунду скрыть
                        new CountDownTimer(500, 500) {
                            public void onTick(long millisUntilFinished) {
                            }

                            public void onFinish() {
                                refreshItem.setVisible(false);
                            }
                        }.start();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getActivity(), "Не удалось получить списки" + '\n'
                        + "Проверье соединение с интернетом", Toast.LENGTH_LONG).show();
            }
            //Занести новые данные в список - в getScheduleTask, после успешной загрузки
        }

        if (!deleteList.isEmpty()) { //Если есть группы для удаления - удалить
            for (GroupsModel model : deleteList) {
                DBHelper.UsedSchedulesHelper.deleteSchedule(getActivity().getBaseContext(), model.getId());
            }
            //TODO удалять расписания из БД
        }
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDetach();
    }
}


