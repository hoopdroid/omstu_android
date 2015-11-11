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
import android.support.v4.widget.SwipeRefreshLayout;
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

public class CalendarScheduleFragment extends Fragment implements OnClickListener, OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener {

    SharedPreferences sPref;
    ListView pairs;
    String[] data;
    LoadMoreTask lmt;
    ScheduleAdapter adapter;
    GridView grid;
    GregorianCalendar calendar;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private GetInitializationInfoTask giit;
    private ArrayList<GroupsModel> usedList;
    private boolean isGroup = true;
    private int currentID = 0;
    private GroupsModel main;
    LinearLayout filtersLayout, detailsLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sPref = getActivity().getSharedPreferences("groups", Context.MODE_PRIVATE);
        adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
        calendar = new GregorianCalendar();
        View view = null;
        setRetainInstance(true);
        filtersLayout = (LinearLayout) view.findViewById(R.id.filtersLayout);
        detailsLayout = (LinearLayout) view.findViewById(R.id.detailll);
        SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        usedList = DBHelper.UsedSchedulesHelper.getGroupsModelList(getActivity()); //Список используемых расписаний
        main = DBHelper.UsedSchedulesHelper.getMainGroupModel(getActivity()); //Основное расписание. Его выводить сверху списка, первым открывать при запуске

        if (userInfo.contains("openGroup")) {
            currentID = userInfo.getInt("openGroup", 0);
            isGroup = userInfo.getBoolean("openIsGroup", true);
        } else if (main != null) {
            currentID = main.getId();
            isGroup = main.isGroup();
        } else {
            if (usedList != null) {
                currentID = usedList.get(0).getId();
                isGroup = usedList.get(0).isGroup();
            }
        }

        if (currentID != 0) //Если данные существуют:
        {

            //заполнение основной сетки ифнормацией в соответствии с настройками
            grid = (GridView) view.findViewById(R.id.calendarSchedule);
            grid.setAdapter(adapter);
            lmt = new LoadMoreTask(isGroup, currentID);
            lmt.execute(14);
            grid.setOnScrollListener(this);
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
        GetScheduleTask gst = new GetScheduleTask(getActivity(), mSwipeRefreshLayout);
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
            lmt = new LoadMoreTask(isGroup, currentID);
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
                        giit = new GetInitializationInfoTask(getActivity(), null);
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
        inflater.inflate(R.menu.daily_schedule, menu);
        String group = "Группа";

        usedList.add(DBHelper.UsedSchedulesHelper.getMainGroupModel(getActivity()));
        GroupsModel main = DBHelper.UsedSchedulesHelper.getMainGroupModel(getActivity()); //Основное расписание. Его выводить сверху списка

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
            SubMenu subMenuGroup = menu.addSubMenu(Menu.NONE, 100, 10, (main == null ? "Группа" : main.getName()));
            subMenuGroup.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            for (int i = 0; i < usedList.size(); i++) {
                subMenuGroup.add(Menu.NONE, 101 + i, Menu.NONE, usedList.get(i).getName());
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 100:
                break; //Для вывода подменю
            case 16908332:
                break; //Для вывода бокового меню
            default:
                //Отобразить новую выбранную группу
                SharedPreferences settings = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE); //Записать новую выбранную группу в файл для его открытия
                settings.edit().putInt("openGroup", usedList.get(item.getItemId() - 101).getId()); //Запись по id. потом по нему открывать расписание
                settings.edit().putBoolean("openIsGroup", usedList.get(item.getItemId() - 101).isGroup()).apply();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.container, new DailyScheduleFragment()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
        boolean loadMore = firstVisible + visibleCount >= totalCount;

        if (loadMore && lmt.getStatus() == AsyncTask.Status.FINISHED) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            lmt = new LoadMoreTask(isGroup, currentID);
            lmt.execute(7);
        }
    }


    static class ViewHolder {
        //список
        public TextView day;
    }

    public class ScheduleAdapter extends BaseAdapter {
        //Адаптер для заполнения списка расписания

        ArrayList<ScheduleModel> list;
        ArrayList<String> namesArray;


        ScheduleAdapter(ArrayList<ScheduleModel> list) {
            this.list = list;
            Set<String> name = null;

            if (name != null) {
                namesArray = new ArrayList<String>(name);
            }
        }

        // кол-во элементов
        @Override
        public int getCount() {
            return list.size();
        }

        // элемент по позиции
        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        // id по позиции
        @Override
        public long getItemId(int position) {
            return position;
        }

        // пункт списка
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            ViewHolder holder;
            // Очищает сущетсвующий шаблон, если параметр задан
            // Работает только если базовый шаблон для всех классов один и тот же
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.one_calendar_pair, null, true);
                holder = new ViewHolder();
                holder.day = (TextView) rowView.findViewById(R.id.tv);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }
            int conumns = 3; //TODO брать из инит.инфо, формировать как число учебных дней в неделю + 2 (колонка на день недели, колонка на пару)
            if (position % conumns == 0) { //если остаток 0, то это день недели
                holder.day.setText(list.get(position).getN());
                holder.day.setBackgroundColor(Color.WHITE);
            } else if (position % conumns == conumns - 1) { //если остаток conumns-1, то это число
                // holder.day.setText(list.get(position).getDate());
                holder.day.setBackgroundColor(Color.WHITE);
            } else { //в остальных случаях это пара, требуется заполнять
                if (list.get(position) == null) { //если нуль, оставить ячейку пустой
                    holder.day.setVisibility(View.INVISIBLE);
                } else {
                    holder.day.setText(list.get(position).getName()); //добавлять текст, только если указано в настройках
                    holder.day.setVisibility(View.VISIBLE);
                    holder.day.setBackgroundColor(Color.GREEN);
                }
            }
            return rowView;
        }


        public void add(ArrayList<ScheduleModel> data) {
            this.list.addAll(data);
        }
    }

    //Реализует подгрузку данных при достижении конца списка
    public class LoadMoreTask extends AsyncTask<Integer, Void, ArrayList<ScheduleModel>> {
        String mDate;
        boolean isGroup;
        int id;

        LoadMoreTask(boolean isGroup, int id) {
            this.isGroup = isGroup;
            this.id = id;
        }

        @Override
        protected ArrayList<ScheduleModel> doInBackground(Integer... params) {
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<ScheduleModel> data) {
            if (data.isEmpty()) {
                Toast.makeText(getActivity(), "Данные закончились", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.add(data);
            adapter.notifyDataSetChanged();
            int index = grid.getFirstVisiblePosition();
            int top = (grid.getChildAt(0) == null) ? 0 : grid.getChildAt(0).getTop();
            grid.setSelectionFromTop(index, top);
        }
    }


}
