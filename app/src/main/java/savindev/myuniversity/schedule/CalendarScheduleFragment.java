package savindev.myuniversity.schedule;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
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
    private boolean isGroup;
    private int currentID;
    LinearLayout filtersLayout, detailsLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sPref = getActivity().getSharedPreferences("groups", Context.MODE_PRIVATE);
        adapter = new ScheduleAdapter(new ArrayList<SheduleModel>());
        calendar = new GregorianCalendar();
        View view = null;
        setRetainInstance(true);
        filtersLayout = (LinearLayout) view.findViewById(R.id.filtersLayout);
        detailsLayout = (LinearLayout) view.findViewById(R.id.detailll);

        if (false) //Если данные существуют:
        //TODO при появлении расписания в базе сделать нормальную проверку. Сначала проверять на наличие расписания для основной группы при авторизации. Если нет - для любой
        {
//            isGroup;
//            currentID;

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
        //TODO получить id текущей группы или преподавателя, а также информацию о том, группа это или преподаватель
        String currentSchedule = null; //в формате idGroup=id или idTeacher=id
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
                //TODO проверить наличие записей по IitializationInfo в БД, если нет - попытаться загрузить
                if (false) {
                    if (MainActivity.isNetworkConnected(getActivity())) {
                        giit = new GetInitializationInfoTask(getActivity());
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
                } else {
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.daily_schedule, menu);
        String group = null;
        //TODO получить список используемых расписаний

        //Если имеются используемые расписания
        if (false) {
            Set<String> list = null; //Список используемых расписаний
            data = list.toArray(new String[list.size()]);
            Arrays.sort(data); //Получение списка групп для вывода и сортировка

            SubMenu subMenuGroup = menu.addSubMenu(Menu.NONE, 100, 10, group);
            subMenuGroup.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
            for (int i = 0; i < list.size(); i++) {
                subMenuGroup.add(Menu.NONE, 101 + i, Menu.NONE, data[i]);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cs_filters:
                if (filtersLayout.getVisibility() == View.GONE) {
                    filtersLayout.setVisibility(View.VISIBLE);
                    getActivity().getSharedPreferences("omgupsSettings", Context.MODE_PRIVATE).edit().putBoolean("filter_visible", false).apply();
                } else {
                    filtersLayout.setVisibility(View.GONE);
                    getActivity().getSharedPreferences("omgupsSettings", Context.MODE_PRIVATE).edit().putBoolean("filter_visible", true).apply();
                }
                break;
            case R.id.cs_detail:
                if (detailsLayout.getVisibility() == View.GONE) {
                    detailsLayout.setVisibility(View.VISIBLE);
                    getActivity().getSharedPreferences("omgupsSettings", Context.MODE_PRIVATE).edit().putBoolean("detail_visible", false).apply();
                } else {
                    detailsLayout.setVisibility(View.GONE);
                    getActivity().getSharedPreferences("omgupsSettings", Context.MODE_PRIVATE).edit().putBoolean("detail_visible", true).apply();
                }
                break;
            case 100:
                break;
            case 16908332:
                break; //Для вывода бокового меню
            default:
//                Editor ed = sPref.edit();
//                ed.putString("set", data[item.getItemId() - 101]).apply();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.container, new CalendarScheduleFragment()).commit();
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

        ArrayList<SheduleModel> list;
        ArrayList<String> namesArray;


        ScheduleAdapter(ArrayList<SheduleModel> list) {
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
                holder.day.setText(list.get(position).getDate());
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


        public void add(ArrayList<SheduleModel> data) {
            this.list.addAll(data);
        }
    }

    //Реализует подгрузку данных при достижении конца списка
    public class LoadMoreTask extends AsyncTask<Integer, Void, ArrayList<SheduleModel>> {
        String mDate;
        boolean isGroup;
        int id;

        LoadMoreTask(boolean isGroup, int id) {
            this.isGroup = isGroup;
            this.id = id;
        }

        @Override
        protected ArrayList<SheduleModel> doInBackground(Integer... params) {
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<SheduleModel> data) {
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
