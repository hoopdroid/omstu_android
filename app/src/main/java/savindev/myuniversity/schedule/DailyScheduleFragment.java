package savindev.myuniversity.schedule;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

public class DailyScheduleFragment extends DialogFragment
        implements OnClickListener, OnScrollListener, SwipeRefreshLayout.OnRefreshListener {
    /**
     * Класс, отображающий расписание на определенный срок в виде списка предметов с параметрами
     */
    private ScheduleAdapter adapter;
    private String[] data;
    private GregorianCalendar calendar;
    private LoadMoreTask lmt;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ListView scheduleList;
    private GetInitializationInfoTask giit;
    private boolean isGroup;
    private int currentID;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
        View view = null;
        calendar = new GregorianCalendar();


        if (false) //Если данные существуют:
        //TODO при появлении расписания в базе сделать нормальную проверку. Сначала проверять на наличие расписания для основной группы при авторизации. Если нет - для любой
        {
//            isGroup;
//            currentID;

            view = inflater.inflate(R.layout.fragment_daily_schedule, null);
            scheduleList = (ListView) view.findViewById(R.id.schedule);
            scheduleList.setAdapter(adapter);
            lmt = new LoadMoreTask(isGroup, currentID);
            lmt.execute(14); //Вывод данных на ближайшие 14 дней
            scheduleList.setOnScrollListener(this);
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
        GetScheduleTask gst = null;
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);
        gst = new GetScheduleTask(getActivity(), mSwipeRefreshLayout);
        //TODO адекватно заполнить эту штуку
        GroupsModel currentSchedule = new GroupsModel(null, 1, true, "20000101000000");
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
        Intent intent;
        switch (v.getId()) {
            case R.id.log_id:
                intent = new Intent(getActivity(), FirstStartActivity.class);
                startActivity(intent);
                break;
            case R.id.set_id:
                // переход к окну настройки
                //TODO проверить наличие записей по IitializationInfo в БД, если нет - попытаться загрузить
                if (false) {
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
            case 100:
                break; //Для вывода подменю
            case 16908332:
                break; //Для вывода бокового меню
            default:
                //Отобразить новую выбранную группу
//                Editor ed = sPref.edit();
//                ed.putString("set", data[item.getItemId() - 101]).apply();
//                FragmentTransaction ft = getFragmentManager().beginTransaction();
//                ft.replace(R.id.container, new DailyScheduleFragment()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }


    //При пролистывании вниз
    @Override
    public void onScroll(AbsListView view, int firstVisible, int visibleCount, int totalCount) {
        boolean loadMore = firstVisible + visibleCount >= totalCount;

        if (loadMore && lmt.getStatus() == AsyncTask.Status.FINISHED) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            lmt = new LoadMoreTask(isGroup, currentID);
            lmt.execute(totalCount);
        }
    }

    static class ViewHolder {
        //список
        public TextView n;
        public TextView time;
        public TextView name;
        public TextView teacher;
        public TextView auditory;
        public TextView tipe;
        public TextView date;
        public LinearLayout pairContainer;
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
                rowView = inflater.inflate(R.layout.schedule_line, null, true);
                holder = new ViewHolder();
                holder.n = (TextView) rowView.findViewById(R.id.pairNumber);
                holder.time = (TextView) rowView.findViewById(R.id.pairTime);
                holder.name = (TextView) rowView.findViewById(R.id.pairName);
                holder.teacher = (TextView) rowView.findViewById(R.id.teacher);
                holder.auditory = (TextView) rowView.findViewById(R.id.auditory);
                holder.tipe = (TextView) rowView.findViewById(R.id.pairType);
                holder.date = (TextView) rowView.findViewById(R.id.date);
                holder.pairContainer = (LinearLayout) rowView.findViewById(R.id.pairContainer);
                rowView.setTag(holder);
            } else {
                holder = (ViewHolder) rowView.getTag();
            }

            holder.n.setText(list.get(position).getN());
            //holder.time.setText(list.get(position).getTime());
            holder.name.setText(list.get(position).getName());
            holder.teacher.setText(list.get(position).getTeacher());
            //holder.auditory.setText(list.get(position).getAuditory());
            holder.tipe.setText(list.get(position).getTipe());
           // holder.date.setText(list.get(position).getDate());


            if (list.get(position).getClassroom().isEmpty()) {
                holder.date.setVisibility(View.GONE);
            } else {
                holder.date.setVisibility(View.VISIBLE);
            }

            return rowView;
        }

        public void add(ArrayList<ScheduleModel> data) {
            this.list.addAll(data);
        }

    }

    //Реализует подгрузку данных при достижении конца списка
    public class LoadMoreTask extends AsyncTask<Integer, Void, ArrayList<ScheduleModel>> {
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
            if (data == null || data.isEmpty()) {
                Toast.makeText(getActivity(), "Данные закончились", Toast.LENGTH_SHORT).show();
                return;
            }
            //Обновить адаптер и вернуть на последнюю просмотренную позицию
            adapter.add(data);
            adapter.notifyDataSetChanged();
            int index = scheduleList.getFirstVisiblePosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            scheduleList.setSelectionFromTop(index, top);
        }
    }

}
