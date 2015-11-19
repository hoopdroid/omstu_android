package savindev.myuniversity.schedule;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.serverTasks.GetInitializationInfoTask;
import savindev.myuniversity.serverTasks.GetScheduleTask;
import savindev.myuniversity.settings.GroupsActivity;
import savindev.myuniversity.welcomescreen.FirstStartActivity;

public class CalendarScheduleFragment extends Fragment implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ListView pairs;
    String[] data;
    LoadMoreTask lmt;
    ScheduleAdapter adapter;
    int pairCount;
    private RecyclerView scheduleList;
    GregorianCalendar calendar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private GetInitializationInfoTask giit;
    private ArrayList<GroupsModel> usedList;
    private boolean isGroup = true;
    private String currentGroup = "";
    private int currentID = 0;
    private GroupsModel main;
    LinearLayout filtersLayout, detailsLayout;
    private GridLayoutManager glm;
    private boolean loading = true;
    int monthCount;

    private ArrayList<ScheduleModel> models;
    private int i;


    void inialize() {
        models = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 28; k++) {
                    ScheduleModel model = new ScheduleModel(0, 0, 0, 0, 0, 0, Integer.toString(j), "1", "2", i + "." + k,
                            "a", "b", "c", "d", "e", false);
                    models.add(model);
                }
            }
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        inialize();
        i = 0;
        monthCount = 0;

        View header = LayoutInflater.from(getActivity()).inflate(R.layout.one_calendar_pair, scheduleList, false);
        adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>(), header);
        calendar = new GregorianCalendar();
        View view = null;
        setRetainInstance(true);

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

        if (currentID != 0) //Если данные существуют:
        {
            view = inflater.inflate(R.layout.fragment_calendar_schedule, null);
            filtersLayout = (LinearLayout) view.findViewById(R.id.filtersLayout);
            detailsLayout = (LinearLayout) view.findViewById(R.id.detailll);
            if (userInfo.getBoolean("openDetails", true)) {
                detailsLayout.setVisibility(View.VISIBLE); //Скрыть подробности
            } else {
                detailsLayout.setVisibility(View.GONE); //Показать подробности
            }
            if (userInfo.getBoolean("openFilters", true)) {
                filtersLayout.setVisibility(View.VISIBLE); //Скрыть подробности
            } else {
                filtersLayout.setVisibility(View.GONE); //Показать подробности
            }


            //заполнение основной сетки ифнормацией в соответствии с настройками
            scheduleList = (RecyclerView) view.findViewById(R.id.calendarSchedule);
            //TODO получить из базы число пар за день  int pairCount = DBHelper.getPairCount(context);
            pairCount = 5;
            //*2 + 2: *2 - каждая пара занимает двойной размер, +2 - дополнительные поля нормального размера на дату и день недели
            glm = new GridLayoutManager(getActivity(), pairCount * 2 + 2); //2 поля на дату и день недели
            if (savedInstanceState != null) {
                glm.scrollToPosition(savedInstanceState.getInt("currentPosition"));
            }
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { //Установка длины ячейки
                @Override
                public int getSpanSize(int position) {
                    if (adapter.isHeader(position)) {
                        monthCount = monthCount + 1;
                        return glm.getSpanCount(); //новый месяц, ячейка с месяцем на всю строку
                    } else if ((position-monthCount) % (pairCount+2) == 0 || (position-monthCount) % (pairCount+2) == pairCount + 1)
                        return 1;
                    else
                        return 2; //Ячейки с парами занимают в 2 раза больше места ячеек с датами
                }
            });
            scheduleList.setLayoutManager(glm);
            scheduleList.setAdapter(adapter);
            lmt = new LoadMoreTask();
            lmt.execute(14);
            //Реализация подгрузки данных при достижении конца списка
            scheduleList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                int pastVisiblesItems, visibleItemCount, totalItemCount;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    visibleItemCount = glm.getChildCount();
                    totalItemCount = glm.getItemCount();
                    pastVisiblesItems = glm.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            lmt = new LoadMoreTask();
                            lmt.execute(totalItemCount);
                        }
                    }
                }
            });
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            mSwipeRefreshLayout.setOnRefreshListener(this);

        } else {
            view = inflater.inflate(R.layout.fragment_null_schedule, null); //Если данные не существуют, вывести информацию
            Button login = (Button) view.findViewById(R.id.log_id);
            Button settings = (Button) view.findViewById(R.id.set_id);
            Button update = (Button) view.findViewById(R.id.upd_id);
            login.setOnClickListener(this);
            settings.setOnClickListener(this);
            update.setOnClickListener(this);
        }

        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("FINISH_UPDATE"));
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onRefresh() {
        // начинаем показывать прогресс
        GetScheduleTask gst = new GetScheduleTask(getActivity().getBaseContext(), mSwipeRefreshLayout);
        GroupsModel currentSchedule = null;
        if (main.getId() == currentID && main.isGroup() == isGroup) { //Проверка на совпадение с главной группкой
            currentSchedule = main;
        } else {
            br:
            for (GroupsModel model : usedList) {
                if (model.getId() == currentID && model.isGroup() == isGroup) {
                    currentSchedule = model;
                    break br;
                }
            }
        }
        gst.execute(currentSchedule); //Выполняем запрос на обновление нужного расписания
    }

    //Перехватчик широковещательных сообщений. Продолжение onRefresh: когда обновление завершилось, обновить ScheduleView
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            lmt = new LoadMoreTask();
            lmt.execute(14);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.log_id:
                Intent intent = new Intent(getActivity(), FirstStartActivity.class);
                startActivity(intent);
                break;
            case R.id.set_id:
                // переход к окну настройки
                if (!DBHelper.isInitializationInfoThere(getActivity())) {
                    if (MainActivity.isNetworkConnected(getActivity())) {
                        giit = new GetInitializationInfoTask(getActivity().getBaseContext(), null);
                        giit.execute();
                        try {
                            if (giit.get(7, TimeUnit.SECONDS)) {
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
                if (!DBHelper.isInitializationInfoThere(getActivity())) {
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
        inflater.inflate(R.menu.calendar_schedule, menu);

        usedList.add(DBHelper.UsedSchedulesHelper.getMainGroupModel(getActivity()));
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
            case R.id.cs_filters:
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                if (userInfo.getBoolean("openFilters", true)) {
                    userInfo.edit().putBoolean("openFilters", false).apply();
                    filtersLayout.setVisibility(View.GONE); //Скрыть фильтры
                } else {
                    userInfo.edit().putBoolean("openFilters", true).apply();
                    filtersLayout.setVisibility(View.VISIBLE); //Показать фильтры
                }
                break;
            case R.id.cs_detail:
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                if (userInfo.getBoolean("openDetails", true)) {
                    userInfo.edit().putBoolean("openDetails", false).apply();
                    detailsLayout.setVisibility(View.GONE); //Скрыть подробности
                } else {
                    userInfo.edit().putBoolean("openDetails", true).apply();
                    detailsLayout.setVisibility(View.VISIBLE); //Показать подробности
                }
                break;
            case R.id.transition:
                //Переход на листовой вид
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                userInfo.edit().putInt("openGroup", currentID).apply(); //Запись по id. потом по нему открывать расписание
                userInfo.edit().putString("openGroupName", currentGroup).apply();
                userInfo.edit().putBoolean("openIsGroup", isGroup).apply();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
                break;
            default:
                //Отобразить новую выбранную группу
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE); //Записать новую выбранную группу в файл для его открытия
                userInfo.edit().putInt("openGroup", usedList.get(item.getItemId() - 101).getId()); //Запись по id. потом по нему открывать расписание
                userInfo.edit().putString("openGroupName", usedList.get(item.getItemId() - 101).getName()).apply();
                userInfo.edit().putBoolean("openIsGroup", usedList.get(item.getItemId() - 101).isGroup()).apply();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
        private static final int ITEM_VIEW_TYPE_HEADER = 0;
        private static final int ITEM_VIEW_TYPE_ITEM = 1;
        private final View header;

        public class ScheduleViewHolder extends RecyclerView.ViewHolder {

            private TextView pair;

            ScheduleViewHolder(View itemView) {
                super(itemView);
                pair = (TextView) itemView.findViewById(R.id.pair);
            }
        }

        List<ScheduleModel> models;

        public boolean isHeader(int position) {
            return (position == 0 || !models.get(position).getDate().substring(0,1).equals(models.get(position - 1).getDate().substring(0,1)));
        }

        ScheduleAdapter(List<ScheduleModel> models, View header) {
            this.models = models;
            if (header == null) {
                throw new IllegalArgumentException("header may not be null");
            }
            this.header = header;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            if (viewType == ITEM_VIEW_TYPE_HEADER) {
                return new ScheduleViewHolder(header);
            }
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_calendar_pair, viewGroup, false);
            return new ScheduleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ScheduleViewHolder scheduleViewHolder, int i) {
            if (isHeader(i)) {
                scheduleViewHolder.pair.setText(models.get(i).getDate());
                return;
            }
            if ((i-monthCount) % (pairCount+2) == 0) { //Если 1 - это день недели
                scheduleViewHolder.pair.setText(models.get(i).getDate().substring(0, 1));
            } else if ((i-monthCount) % (pairCount+2) == pairCount + 1) { //Если последний - дата
                scheduleViewHolder.pair.setText(models.get(i).getDate().substring(1));
            } else // Иначе это пара
                scheduleViewHolder.pair.setText(models.get(i).getName());

        }

        @Override
        public int getItemViewType(int position) {
            return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
        }

        @Override
        public int getItemCount() {
            return models.size();
        }

        public void add(ArrayList<ScheduleModel> data) {
            this.models.addAll(data);
        }
    }

    //Реализует подгрузку данных при достижении конца списка
    public class LoadMoreTask extends AsyncTask<Integer, Void, ArrayList<ScheduleModel>> {
        String mDate;

        @Override
        protected ArrayList<ScheduleModel> doInBackground(Integer... params) {
            ArrayList data = new ArrayList();
            int j = i;
            for (; i < j + params[0]; i++) {
                if (i < models.size()) {
                    data.add(models.get(i));
                }
            }

            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleModel> data) {
            if (data == null || data.isEmpty()) {
                Toast.makeText(getActivity(), "Данные закончились", Toast.LENGTH_SHORT).show();
                return;
            }
            //Обновить адаптер и вернуть на последнюю просмотренную позицию
            adapter.add(data);
            adapter.notifyDataSetChanged();
            int index = glm.findFirstVisibleItemPosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            glm.scrollToPositionWithOffset(index, top);
            loading = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentPosition", glm.onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("currentPosition");
            glm.onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDetach();
    }


}
