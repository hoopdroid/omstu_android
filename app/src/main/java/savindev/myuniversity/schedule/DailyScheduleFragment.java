package savindev.myuniversity.schedule;

import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.db.DBRequest;
import savindev.myuniversity.serverTasks.GetScheduleTask;
import savindev.myuniversity.serverTasks.getUniversityInfoTask;
import savindev.myuniversity.settings.GroupsActivity;
import savindev.myuniversity.welcomescreen.FirstStartActivity;

public class DailyScheduleFragment extends DialogFragment
        implements OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    /**
     * Класс, отображающий расписание на определенный срок в виде списка предметов с параметрами
     */
    private ScheduleAdapter adapter;
    private ArrayList<GroupsModel> usedList;
    private GregorianCalendar calendar;
    private LoadMoreTask lmt;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView scheduleList;
    private boolean isGroup = true;
    private int currentID = 0;
    private String currentGroup = "";
    private GroupsModel main;
    private LinearLayoutManager llm;
    private boolean loading = true;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        calendar = new GregorianCalendar();  //Получение текущей даты для начала заполнения расписания
        calendar.add(Calendar.DAY_OF_MONTH, -1); //Чтобы не пропускать день при работе в цикле

        adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
        View view = null;
        calendar = new GregorianCalendar();
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
            view = inflater.inflate(R.layout.fragment_daily_schedule, null);
            scheduleList = (RecyclerView) view.findViewById(R.id.schedule);
            llm = new LinearLayoutManager(getActivity());
            if (savedInstanceState != null) {
                llm.scrollToPosition(savedInstanceState.getInt("currentPosition"));
            }
            scheduleList.setLayoutManager(llm);
            scheduleList.setAdapter(adapter);
            lmt = new LoadMoreTask();
            lmt.execute(14); //Вывод данных на ближайшие 14 дней
            //Реализация подгрузки данных при достижении конца списка
            scheduleList.setOnScrollListener(new RecyclerView.OnScrollListener() {
                int pastVisiblesItems, visibleItemCount, totalItemCount;

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

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
        setRetainInstance(true);

        return view;
    }


    @Override
    public void onRefresh() {
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);
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
        calendar = new GregorianCalendar();
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
        Intent intent;
        switch (v.getId()) {
            case R.id.log_id:
                intent = new Intent(getActivity(), FirstStartActivity.class);
                startActivity(intent);
                break;
            case R.id.set_id:
                if (!DBRequest.isInitializationInfoThere(getActivity())) {
                    if (MainActivity.isNetworkConnected(getActivity())) {
                        getUniversityInfoTask guit = new getUniversityInfoTask(getActivity().getBaseContext(), null);
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
        inflater.inflate(R.menu.daily_schedule, menu);

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
            case R.id.transition:
                //Переход на календарный вид
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                userInfo.edit().putInt("openGroup", currentID).apply(); //Запись по id. потом по нему открывать расписание
                userInfo.edit().putString("openGroupName", currentGroup).apply();
                userInfo.edit().putBoolean("openIsGroup", isGroup).apply();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new CalendarScheduleFragment()).commit();
                break;
            default:
                //Отобразить новую выбранную группу
                userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                userInfo.edit().putInt("openGroup", usedList.get(item.getItemId() - 101).getId()).apply(); //Запись по id. потом по нему открывать расписание
                userInfo.edit().putString("openGroupName", usedList.get(item.getItemId() - 101).getName()).apply();
                userInfo.edit().putBoolean("openIsGroup", usedList.get(item.getItemId() - 101).isGroup()).apply();
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

        public class ScheduleViewHolder extends RecyclerView.ViewHolder {

            private TextView pairNumber;
            private TextView pairTime;
            private TextView pairName;
            private TextView pairTeacher;
            private TextView pairAuditory;
            private TextView pairType;
            private TextView pairDate;
            private CardView cv;

            ScheduleViewHolder(View itemView) {
                super(itemView);
                cv = (CardView) itemView.findViewById(R.id.cv);
                pairNumber = (TextView) itemView.findViewById(R.id.pairNumber);
                pairTime = (TextView) itemView.findViewById(R.id.pairTime);
                pairName = (TextView) itemView.findViewById(R.id.pairName);
                pairTeacher = (TextView) itemView.findViewById(R.id.pairTeacher);
                pairAuditory = (TextView) itemView.findViewById(R.id.pairAuditory);
                pairType = (TextView) itemView.findViewById(R.id.pairType);
                pairDate = (TextView) itemView.findViewById(R.id.pairDate);
            }
        }

        List<ScheduleModel> models;

        ScheduleAdapter(List<ScheduleModel> models) {
            this.models = models;
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_line, viewGroup, false);
            ScheduleViewHolder svh = new ScheduleViewHolder(v);
            return svh;
        }

        @Override
        public void onBindViewHolder(ScheduleViewHolder scheduleViewHolder, int i) {
            scheduleViewHolder.pairNumber.setText(models.get(i).getN());
            String a = models.get(i).getStartTime();
            String b = models.get(i).getEndTime();
            scheduleViewHolder.pairTime.setText(a + "-" + b);
            scheduleViewHolder.pairName.setText(models.get(i).getName());
            scheduleViewHolder.pairTeacher.setText(models.get(i).getTeacher());
            scheduleViewHolder.pairAuditory.setText(models.get(i).getClassroom());
            scheduleViewHolder.pairType.setText(models.get(i).getTipe());
            scheduleViewHolder.pairDate.setText(models.get(i).getDate());
            if (i == 0 || !models.get(i).getDate().equals(models.get(i-1).getDate())) {
                scheduleViewHolder.pairDate.setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                scheduleViewHolder.pairDate.setVisibility(View.VISIBLE);
            } else {
                scheduleViewHolder.pairDate.setVisibility(View.GONE);
            }
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

        @Override
        protected ArrayList<ScheduleModel> doInBackground(Integer... params) {
            ArrayList<ScheduleModel> data = new ArrayList();
            for (int i = 0; i < params[0]; i++) { //Добавить число записей, равное params[0]
                calendar.add(Calendar.DAY_OF_MONTH, 1); //Каждый раз работа со следующим днем
                //TODO подумать, как нужно обрабатывать выходные дни
                ArrayList<ScheduleModel> daySchedule;
                daySchedule = DBHelper.SchedulesHelper.getSchedules(getActivity(),
                        "" + calendar.get(Calendar.YEAR) + calendar.get(Calendar.MONTH) + calendar.get(Calendar.DAY_OF_MONTH),
                        currentID, isGroup);  //Получение расписания на день
                //TODO костыль, удалить после организации сортировки по дате начала в БД
                Collections.sort(daySchedule,  new Comparator<ScheduleModel>() {
                    @Override
                    public int compare(ScheduleModel arg0, ScheduleModel arg1) {
                        return arg0.getStartTime().compareTo(arg1.getStartTime());
                    }
                });
                data.addAll(daySchedule);
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
            int index = llm.findFirstVisibleItemPosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            llm.scrollToPositionWithOffset(index, top);
            loading = true;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("currentPosition", llm.onSaveInstanceState());
    }

//    @Override
//    public void onViewStateRestored(Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//        if(savedInstanceState != null) {
//            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("currentPosition");
//            llm.onRestoreInstanceState(savedRecyclerLayoutState);
//        }
//    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDetach();
    }


}