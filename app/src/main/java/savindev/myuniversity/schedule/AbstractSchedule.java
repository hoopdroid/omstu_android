package savindev.myuniversity.schedule;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.TreeMap;
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

/**
 * Абстрактный класс, собирающий в себе общие методы для расписания-списка и расписания-сетки
 * Содержит общую инициализацию, обработку пролистывания, кнопок, меню, обновления, отсоединения фрагмента
 */

public abstract class AbstractSchedule extends DialogFragment
        implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ScheduleAdapter adapter;
    private ArrayList<GroupsModel> usedList;
    private GregorianCalendar calendar;
    protected LoadMoreTask lmt;
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView scheduleList;
    protected boolean isGroup;
    protected int currentID = 0;
    protected String currentGroup = "";
    private GroupsModel main;
    protected LinearLayoutManager llm;
    protected GridLayoutManager glm;
    private boolean loading = true;
    protected boolean isLinear;
    private int lastFirstVisiblePosition;
    private TreeMap<GregorianCalendar, Integer> positions;
    private AlertDialog calendarDialog;

    protected View preInitializeData(LayoutInflater inflater, ViewGroup container) { //Объявление общей информации при загрузке фрагмента
        View view = null;
        calendar = new GregorianCalendar();  //Получение текущей даты для начала заполнения расписания
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.DAY_OF_MONTH, -1); //Чтобы не пропускать день при работе в цикле
        SharedPreferences setting = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        usedList = DBHelper.UsedSchedulesHelper.getGroupsModelList(getActivity()); //Список используемых расписаний
        main = DBHelper.UsedSchedulesHelper.getMainGroupModel(getActivity()); //Основное расписание. Его выводить сверху списка, первым открывать при запуске

        if (setting.contains("openGroup")) { //Сначала - проверка на выбранную группу (при пересоздании фрагмента)
            currentID = setting.getInt("openGroup", 0);
            isGroup = setting.getBoolean("openIsGroup", true);
            currentGroup = setting.getString("openGroupName", "");
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
            view = inflater.inflate(R.layout.fragment_null_schedule, container, false); //Если данные не существуют, вывести информацию
            Button login = (Button) view.findViewById(R.id.log_id);
            Button settings = (Button) view.findViewById(R.id.set_id);
            Button update = (Button) view.findViewById(R.id.upd_id);
            login.setOnClickListener(this);
            settings.setOnClickListener(this);
            update.setOnClickListener(this);
        }

        getActivity().registerReceiver(broadcastReceiverUpdate, new IntentFilter("FINISH_UPDATE"));
        getActivity().registerReceiver(broadcastReceiverLMT, new IntentFilter("FINISH_LMT"));
        onCreateDialog(null); //Создание диалога с календариком
        return view;
    }

    public void postInitializeData() {
        scheduleList.setAdapter(adapter);
        lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, isLinear);
        lmt.execute(8); //Вывод данных на ближайшие 8 дней
        setPositions(lmt);
        //Реализация подгрузки данных при достижении конца списка
        scheduleList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int pastVisiblesItems, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                visibleItemCount = scheduleList.getLayoutManager().getChildCount();
                totalItemCount = scheduleList.getLayoutManager().getItemCount();
                if (isLinear) {
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();
                } else {
                    pastVisiblesItems = glm.findFirstVisibleItemPosition();
                }

                if (loading) {
                    if ((visibleItemCount + pastVisiblesItems) >= totalItemCount-3) {
                        loading = false;
                        lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, isLinear);
                        lmt.execute(totalItemCount);
                        setPositions(lmt);
                    }
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(this);
//        if (isLinear) {
//            llm.scrollToPosition(lastFirstVisiblePosition);
//        } else {
//            glm.scrollToPosition(lastFirstVisiblePosition);
//        }

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    private void setPositions(final LoadMoreTask lmt) { //Специально для обновления данных в отдельном потоке
        new Thread(new Runnable() {
            public void run() {//Этот метод будет выполняться в побочном потоке
                try {
                    if (positions == null)
                        positions = lmt.get();
                    else
                        positions.putAll(lmt.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected int getPostition(final GregorianCalendar date) {
        //TODO время семестра нормально получать.
//        if (DBHelper.getInstance(getActivity()).getSemestersHelper().getSemesterStartDate(getActivity(), new GregorianCalendar().getTime()).compareTo(date) < 0)
//            return 0; //Вызываемая дата раньше начала семестра, не надо это показывать
        GregorianCalendar date2 = (GregorianCalendar) date.clone(); //Непонятный по своей природе костыль: без этого дата после before() вырастает на месяц
        if (new GregorianCalendar().after(date)) { //Если запрошенная дата меньше текущей
            calendar = date; //Перезагрузить адаптер, начиная с указанной даты
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            adapter.deleteData();
            loading = false;
            lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, isLinear);
            lmt.execute(14);
            try {
                positions = lmt.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
//        long a = date.getTimeInMillis();
//        long b = positions.lastKey().getTimeInMillis();
//        GregorianCalendar c = new GregorianCalendar();
//        c.set(2015, 12, 18);
//        long d = c.getTimeInMillis();
        if (positions.lastKey().before(date2)) { //Если в адаптере последняя дата меньше, догрузить до нужной
            int days = (int) ((date.getTimeInMillis() - positions.lastKey().getTimeInMillis()) / 86400000); //Получение числа дней между последней загруженной датой и заданной
            loading = false;
            lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, isLinear);
            lmt.execute(days + 7); //С маленьким запасом
            try {
                positions.putAll(lmt.get());
                lmt.onPostExecute(lmt.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //Вернуть позицию для ключа или ближайшего ключа, если точного нет
        if (positions.floorEntry(date) == null)
            return 0;
        return positions.floorEntry(date).getValue();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Отпускает диалоговое окно
            }
        });
        final View dialog = getActivity().getLayoutInflater()
                .inflate(R.layout.calendar_dialog, null);
        CalendarView calendar = (CalendarView) dialog.findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {
                String selectedDate = (month + 1) + "-" + dayOfMonth + "-" + year + " ";
                int p = getPostition(new GregorianCalendar(year, month, dayOfMonth));
                if (p != 0)
                    scheduleList.scrollToPosition(p + 1);
                else
                    Toast.makeText(getActivity(), "Неверная дата", Toast.LENGTH_SHORT).show();
                calendarDialog.dismiss();
            }
        });
        calendarDialog = builder.setView(dialog).create();
        return calendarDialog;
    }

    @Override
    public void onRefresh() {
        // начинаем показывать прогресс
        mSwipeRefreshLayout.setRefreshing(true);
        if (MainActivity.isNetworkConnected(getActivity())) { //Если есть интернет - попробовать обновить БД
            GetScheduleTask gst = new GetScheduleTask(getActivity().getBaseContext(), mSwipeRefreshLayout);
            GroupsModel model = null; //Достать активную группу для обновления. Нельзя создавать новую модель, т.к. нужна дата
            if (main != null && currentID == main.getId() && isGroup == main.isGroup())
                model = main;
            else
                for (GroupsModel m : usedList) {
                    if (currentID == m.getId() && isGroup == m.isGroup()) {
                        model = m;
                        break;
                    }
                }
            gst.execute(model); //Выполняем запрос на обновление нужного расписания
        } else { //Если нет - просто перезагрузить страничку
            update();
        }
    }

    private void update() {
        mSwipeRefreshLayout.setRefreshing(false);
        calendar = new GregorianCalendar(); //Чистка адаптера, начало со старой даты
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        adapter.deleteData();
        loading = false;
        lmt = new LoadMoreTask(getActivity(), calendar, currentID, isGroup, adapter, scheduleList, isLinear);
        lmt.execute(14);
        if (positions != null)
            positions.clear();
        setPositions(lmt);
    }

    //Перехватчик широковещательных сообщений. Продолжение onRefresh: когда обновление завершилось, обновить ScheduleView
    BroadcastReceiver broadcastReceiverUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            update();
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
                if (!DBRequest.isUniversityInfoThere(getActivity())) {
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
                if (!DBRequest.isUniversityInfoThere(getActivity())) {
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
        MenuItem item = menu.findItem(R.id.calendar); //Установка текущей даты для навигационного календаря
        GregorianCalendar c = new GregorianCalendar();
        item.setTitle(c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR));

        //Если имеются используемые расписания
        if (!(usedList == null || usedList.isEmpty())) {
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
        SharedPreferences settings;
        FragmentTransaction ft;
        switch (item.getItemId()) {
            case 100:
                break; //Для вывода подменю
            case 16908332:
                break; //Для вывода бокового меню
            case R.id.calendar:
                calendarDialog.show();
                break;
            default:
                //Отобразить новую выбранную группу
                settings = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
                settings.edit().putInt("openGroup", usedList.get(item.getItemId() - 101).getId()).apply(); //Запись по id. потом по нему открывать расписание
                settings.edit().putString("openGroupName", usedList.get(item.getItemId() - 101).getName()).apply();
                settings.edit().putBoolean("openIsGroup", usedList.get(item.getItemId() - 101).isGroup()).apply();
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


    //Нерабочий код - попытка сохранить позицию recyclerView при повороте
//    @Override
//    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
//        super.onViewStateRestored(savedInstanceState);
//
//        if (savedInstanceState != null) {
//            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable("recycle");
//            scheduleList.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
//            lastFirstVisiblePosition = savedInstanceState.getInt("recycle_position");
////            if (isLinear) {
////                llm.scrollToPosition(lastFirstVisiblePosition);
////            } else {
////                glm.scrollToPosition(lastFirstVisiblePosition);
////            }
//            int a = 3;
//            return;
//        }
//    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putParcelable("recycle", scheduleList.getLayoutManager().onSaveInstanceState());
////        int lastFirstVisiblePosition;
//        if (isLinear) {
//            lastFirstVisiblePosition = ((LinearLayoutManager) scheduleList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//        } else {
//            lastFirstVisiblePosition = ((GridLayoutManager) scheduleList.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
//        }
//        outState.putInt("recycle_position", lastFirstVisiblePosition);
//        int a = 3;
//    }


}
