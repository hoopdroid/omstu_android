package savindev.myuniversity.schedule;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.db.DBRequest;
import savindev.myuniversity.serverTasks.GetScheduleTask;
import savindev.myuniversity.serverTasks.GetUniversityInfoTask;
import savindev.myuniversity.settings.GroupsActivity;
import savindev.myuniversity.welcomescreen.FirstStartActivity;


public abstract class AbstractSchedule extends DialogFragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ScheduleAdapter adapter;
    private ArrayList<GroupsModel> usedList;
    private GregorianCalendar calendar;
    private LoadMoreTask lmt;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView scheduleList;
    protected boolean isGroup = true;
    protected int currentID = 0;
    protected String currentGroup = "";
    private GroupsModel main;
    protected LinearLayoutManager llm;
    protected GridLayoutManager glm;
    private boolean loading = true;
    protected boolean isLinear;

    protected View preInitializeData(LayoutInflater inflater) { //Объявление общей информации при загрузке фрагмента
        View view = null;
        calendar = new GregorianCalendar();  //Получение текущей даты для начала заполнения расписания
        calendar.add(Calendar.DAY_OF_MONTH, -1); //Чтобы не пропускать день при работе в цикле
//        adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
        SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        usedList = DBHelper.UsedSchedulesHelper.getGroupsModelList(getActivity()); //Список используемых расписаний
        main = DBHelper.UsedSchedulesHelper.getMainGroupModel(getActivity()); //Основное расписание. Его выводить сверху списка, первым открывать при запуске

        if (userInfo.contains("openGroup")) { //Сначала - проверка на выбранную группу (при пересоздании фрагмента)
            currentID = userInfo.getInt("openGroup", 0);
            isGroup = userInfo.getBoolean("openIsGroup", true);
            currentGroup = userInfo.getString("openGroupName", "");
        } else if (main != null) { //Проверка на наличие главной группы авторизованного
            currentID = main.getId();
            isGroup = main.isGroup();
            currentGroup = main.getName();
        } else {
            if (!usedList.isEmpty()) { //Проверка на наличие хоть какой-нибудь группы
                currentID = usedList.get(0).getId();
                isGroup = usedList.get(0).isGroup();
                currentGroup = usedList.get(0).getName();
            }
        }

        if (currentID == 0) { //Если данные не существуют:
            view = inflater.inflate(R.layout.fragment_null_schedule, null); //Если данные не существуют, вывести информацию
            Button login = (Button) view.findViewById(R.id.log_id);
            Button settings = (Button) view.findViewById(R.id.set_id);
            Button update = (Button) view.findViewById(R.id.upd_id);
            login.setOnClickListener(this);
            settings.setOnClickListener(this);
            update.setOnClickListener(this);
        }

        getActivity().registerReceiver(broadcastReceiverUpdate, new IntentFilter("FINISH_UPDATE"));
        getActivity().registerReceiver(broadcastReceiverLMT, new IntentFilter("FINISH_LMT"));
        return view;
    }

    public void postInitializeData() {
        scheduleList.setAdapter(adapter);
        lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, true);
        lmt.execute(14); //Вывод данных на ближайшие 14 дней
        //Реализация подгрузки данных при достижении конца списка
        scheduleList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isLinear) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();
                } else {
                    visibleItemCount = glm.getChildCount();
                    totalItemCount = glm.getItemCount();
                    pastVisiblesItems = glm.findFirstVisibleItemPosition();
                }

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                        loading = false;
                        lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, isLinear);
                        lmt.execute(totalItemCount);
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }


    @Override
    public void onRefresh() {
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);
        GetScheduleTask gst = new GetScheduleTask(getActivity().getBaseContext(), mSwipeRefreshLayout);
        calendar = new GregorianCalendar(); //Чистка адаптера, начало со старой даты
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        adapter.deleteData();
        gst.execute(new GroupsModel(null, currentID, isGroup)); //Выполняем запрос на обновление нужного расписания
    }

    //Перехватчик широковещательных сообщений. Продолжение onRefresh: когда обновление завершилось, обновить ScheduleView
    BroadcastReceiver broadcastReceiverUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, true);
            lmt.execute(14);
        }
    };

    BroadcastReceiver broadcastReceiverLMT = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            loading = true;
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.log_id:
                intent = new Intent(getActivity(), FirstStartActivity.class);
                startActivity(intent);
                break;
            case R.id.set_id:
                if (!DBRequest.isInitializationInfoThere(getActivity())) {
                    if (MainActivity.isNetworkConnected(getActivity())) {
                        GetUniversityInfoTask guit = new GetUniversityInfoTask(getActivity().getBaseContext(), null);
                        guit.execute();
                        try {
                            if (guit.get(7, TimeUnit.SECONDS)) {
                                intent = new Intent(getActivity(), GroupsActivity.class);
                                startActivity(intent);
                            }
                        } catch (InterruptedException | ExecutionException | TimeoutException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Не удалось получить списки" + '\n'
                                + "Проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
                    }
                }
                if (!DBRequest.isInitializationInfoThere(getActivity())) {
                    intent = new Intent(getActivity(), GroupsActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.upd_id:
                // обновить окно
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_main, new DailyScheduleFragment()).commit();
                break;
        }
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        //Если имеются используемые расписания
        if (usedList != null && !usedList.isEmpty() || main != null) {
            usedList.remove(null);
            Collections.sort(usedList, new Comparator<GroupsModel>() { //Отсортировать список по имени
                @Override
                public int compare(GroupsModel lhs, GroupsModel rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
            usedList.add(0, main); //И добавить первым элементом главную запись
            usedList.remove(null);
            SubMenu subMenuGroup = menu.addSubMenu(Menu.NONE, 100, 10, currentGroup);
            subMenuGroup.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            for (int i = 0; i < usedList.size(); i++) {
                subMenuGroup.add(Menu.NONE, 101 + i, Menu.NONE, usedList.get(i).getName());
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences userInfo;
        FragmentTransaction ft;
        switch (item.getItemId()) {
            case 100:
                break; //Для вывода подменю
            case 16908332:
                break; //Для вывода бокового меню
            default:
                //Отобразить новую выбранную группу
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                userInfo.edit().putInt("openGroup", usedList.get(item.getItemId() - 101).getId()).apply(); //Запись по id. потом по нему открывать расписание
                userInfo.edit().putString("openGroupName", usedList.get(item.getItemId() - 101).getName()).apply();
                userInfo.edit().putBoolean("openIsGroup", usedList.get(item.getItemId() - 101).isGroup()).apply();
                ft = getFragmentManager().beginTransaction();
                if (isLinear)
                    ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
                else
                    ft.replace(R.id.content_main, new CalendarScheduleFragment()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(broadcastReceiverUpdate);
        getActivity().unregisterReceiver(broadcastReceiverLMT);
        super.onDetach();
    }



}
