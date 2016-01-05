package savindev.myuniversity.schedule;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.PairInfoActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;

/**
 * Класс, отображающий расписание на определенный срок в виде сетки с предметами. Имеет различные методы фильтрации предметов
 * и возможность отображения подробностей для любого из них
 */

public class CalendarScheduleFragment extends AbstractSchedule {

    private LinearLayout filtersLayout, detailsLayout;
    private ArrayList<String> filterType, filterName;
    private SparseBooleanArray chosenType, chosenName;
    private ListView pairNames, pairTypes;
    private TextView number, time, name, teacher, auditory, type;
    private View drawerView;
    private Drawer drawer;
    private Button next;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = preInitializeData(inflater, container);

        if (view == null) {//Если данные существуют:
            int monthCount = 0;
            SharedPreferences userInfo = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
            view = inflater.inflate(R.layout.fragment_calendar_schedule, container, false);
            drawerView = inflater.inflate(R.layout.filters_drawer, container, false);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

            isLinear = false;

            detailsLayout = (LinearLayout) view.findViewById(R.id.detailll);
            next = (Button)view.findViewById(R.id.next);
            if (userInfo.getBoolean("openDetails", true)) {
                detailsLayout.setVisibility(View.VISIBLE); //Скрыть подробности
            } else {
                detailsLayout.setVisibility(View.GONE); //Показать подробности
            }

            scheduleList = (RecyclerView) view.findViewById(R.id.calendarSchedule);
            int pairCount = DBHelper.getInstance(getActivity()).getPairsHelper().getPairsInDay(getActivity());
            //*2 + 2: *2 - каждая пара занимает двойной размер, +2 - дополнительные поля нормального размера на дату и день недели
            glm = new GridLayoutManager(getActivity(), pairCount * 2 + 2); //2 поля на дату и день недели
            scheduleList.setLayoutManager(glm);
            adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { //Установка длины ячейки
                @Override
                public int getSpanSize(int position) {
                    switch (adapter.getItemViewType(position)) {
                        case ScheduleAdapter.VIEW_PAIR:
                            return 2;
                        case ScheduleAdapter.VIEW_DATE:
                            return 1;
                        case ScheduleAdapter.VIEW_DAY:
                            return 1;
                        case ScheduleAdapter.VIEW_MONTH:
                            return glm.getSpanCount();
                        default:
                            return 0;
                    }
                }
            });
            postInitializeData();

            pairNames = (ListView) drawerView.findViewById(R.id.pairNames);
            pairTypes = (ListView) drawerView.findViewById(R.id.pairTypes);
            initializeFiltersLists();

            number = (TextView) view.findViewById(R.id.detailNumber);
            time = (TextView) view.findViewById(R.id.detailTime);
            name = (TextView) view.findViewById(R.id.detailName);
            teacher = (TextView) view.findViewById(R.id.detailTeacher);
            auditory = (TextView) view.findViewById(R.id.detailAuditory);
            type = (TextView) view.findViewById(R.id.detailType);
            MainActivity.fab.hide();


        }
        return view;
    }


    private void initializeFiltersLists() {
        filterType = new ArrayList<>(DBHelper.getInstance(getActivity()).getSchedulesHelper().getGroupLessonsTypes(getActivity(), currentID, isGroup));
        filterName = new ArrayList<>(DBHelper.getInstance(getActivity()).getSchedulesHelper().getGroupLessons(getActivity(), currentID, isGroup));

        pairNames.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_filters, filterName));
        pairTypes.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.list_filters, filterType));
        for (int i = 0; i < filterName.size(); i++) {
            pairNames.setItemChecked(i, true);
        }
        for (int i = 0; i < filterType.size(); i++) {
            pairTypes.setItemChecked(i, true);
        }
        chosenType = pairTypes.getCheckedItemPositions();
        chosenName = pairNames.getCheckedItemPositions();
        pairNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                chosenName = ((ListView) parent).getCheckedItemPositions();
                adapter.notifyDataSetChanged();
            }
        });
        pairTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                chosenType = ((ListView) parent).getCheckedItemPositions();
                adapter.notifyDataSetChanged();
            }
        });


        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        DrawerBuilder b = new DrawerBuilder(getActivity());
        drawer = b.withCustomView(drawerView).withDisplayBelowStatusBar(true).withDrawerGravity(Gravity.END).buildForFragment();
//        drawer.getDrawerLayout().setVisibility(View.GONE);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.calendar_schedule, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        SharedPreferences userInfo;
        FragmentTransaction ft;
        switch (item.getItemId()) {
            case R.id.cs_detail:
                userInfo = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
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
                MainActivity.setOpen(currentID, isGroup, currentGroup); //Запись по id. потом по нему открывать расписание
                ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public class ScheduleAdapter extends savindev.myuniversity.schedule.ScheduleAdapter {
        public static final int VIEW_DATE = 1;
        public static final int VIEW_DAY = 2;
        public static final int VIEW_MONTH = 3;
        public static final int VIEW_PAIR = 4;

        ScheduleAdapter(List<ScheduleModel> models) {
            super(getActivity(), models);
        }

        @Override
        public int getItemViewType(int position) {
            if (models.isEmpty())
                return 0;
            if (models.get(position) == null)
                return VIEW_PAIR;
            switch (models.get(position).getCellType()) {
                case PAIR:
                    return VIEW_PAIR;
                case DAY:
                    return VIEW_DAY;
                case DATE:
                    return VIEW_DATE;
                case MONTH:
                    return VIEW_MONTH;
            }
            return 0;
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(final ViewGroup parent, final int position) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_calendar_pair, parent, false);

            return new ScheduleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ScheduleViewHolder scheduleViewHolder, final int i) {
            if (models.get(i) == null) {// Окно, точно пара
                scheduleViewHolder.pairName.setVisibility(View.INVISIBLE);
            } else
                switch (models.get(i).getCellType()) {    //Заполнение ячейки в соответствии с ее типом
                    case DAY:
                        scheduleViewHolder.pairName.setText(models.get(i).getDate());
                        scheduleViewHolder.pairName.setVisibility(View.VISIBLE);
                        scheduleViewHolder.pairName.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                        break;
                    case DATE:
                        scheduleViewHolder.pairName.setText(models.get(i).getDate());
                        scheduleViewHolder.pairName.setVisibility(View.VISIBLE);
                        scheduleViewHolder.pairName.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                        break;
                    case PAIR:
                        if (!checkedPairFilters(models.get(i))) {
                            scheduleViewHolder.pairName.setVisibility(View.GONE);
                            scheduleViewHolder.cv.setBackgroundColor(getActivity().getResources().getColor(R.color.primary));
                            scheduleViewHolder.cv.setAlpha(0.5f);
                            break;
                        }
                        scheduleViewHolder.cv.setBackgroundColor(Color.WHITE);
                        scheduleViewHolder.cv.setAlpha(1f);
                        //Если проверка на фильтрацию пройдена, показать пару
                        if (models.get(i).getPairs().get(0).getSubgroup() != 0)
                            scheduleViewHolder.pairName.setText(models.get(i).getPairs().get(0).getName() + ", п/г " + models.get(i).getPairs().get(0).getSubgroup());
                        else
                            scheduleViewHolder.pairName.setText(models.get(i).getPairs().get(0).getName());
                        scheduleViewHolder.pairName.setVisibility(View.VISIBLE);
                        scheduleViewHolder.pairName.setBackgroundColor(Color.WHITE);
                        scheduleViewHolder.pairName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (models.get(scheduleViewHolder.getAdapterPosition()) != null)
                                    fillDetailsLayout(models.get(scheduleViewHolder.getAdapterPosition()), 0);
                            }
                        });
                        scheduleViewHolder.cv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View v) {
                                MainActivity.setPosition(models.get(scheduleViewHolder.getAdapterPosition()).getDate(),
                                        models.get(scheduleViewHolder.getAdapterPosition()).getN());
                                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                                ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
                                return false;
                            }
                        });
                        if (models.get(i).getPairs().size() > 1) { //Есть пара-дубль TODO сделать универсальную систему на любое число пар
                            if (models.get(i).getPairs().get(0).getSubgroup() != 0)
                                scheduleViewHolder.dublPairName.setText(models.get(i).getPairs().get(1).getName() + ", п/г " + models.get(i).getPairs().get(1).getSubgroup());
                            else
                                scheduleViewHolder.dublPairName.setText(models.get(i).getPairs().get(1).getName());
                            scheduleViewHolder.dublPairName.setVisibility(View.VISIBLE);
                            scheduleViewHolder.dublPairName.setBackgroundColor(Color.WHITE);
                            scheduleViewHolder.dublPairName.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (models.get(scheduleViewHolder.getAdapterPosition()) != null)
                                        fillDetailsLayout(models.get(scheduleViewHolder.getAdapterPosition()), 1);
                                }
                            });
                        } else {
                            scheduleViewHolder.dublPairName.setVisibility(View.GONE);
                        }
                        break;
                    case MONTH:
                        scheduleViewHolder.pairName.setText(month(models.get(i).getDate()));
                        scheduleViewHolder.pairName.setVisibility(View.VISIBLE);
                        scheduleViewHolder.pairName.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                        break;

                }
        }

        private boolean checkedPairFilters(ScheduleModel model) {
            type:
            {
                if (chosenType != null) //Первый вызов до формирования chosenType
                    for (int j = 0; j < chosenType.size(); j++) { //Выделен ли такой тип
                        if (chosenType.valueAt(j) && model.getPairs().get(0).getType().equals(filterType.get(chosenType.keyAt(j)))) {//Если имеется такая запись в выделенных
                            int a = 3;
                            break type;
                        }
                    }
                return false;
            }
            name:
            {
                if (chosenName != null)
                    for (int j = 0; j < chosenName.size(); j++) { //Выделено ли такое имя
                        if (chosenName.valueAt(j) && model.getPairs().get(0).getName().equals(filterName.get(chosenName.keyAt(j)))) {//Если имеется такая запись в выделенных
                            int a = 3;
                            break name;
                        }
                    }
                return false;
            }
            return true;
        }

        private String month(String date) {
            int d = Integer.parseInt(date);
            switch (d) {
                case 0:
                    return "Январь";
                case 1:
                    return "Февраль";
                case 2:
                    return "Март";
                case 3:
                    return "Апрель";
                case 4:
                    return "Май";
                case 5:
                    return "Июнь";
                case 6:
                    return "Июль";
                case 7:
                    return "Август";
                case 8:
                    return "Сентябрь";
                case 9:
                    return "Октябрь";
                case 10:
                    return "Ноябрь";
                case 11:
                    return "Декабрь";
            }
            return null;
        }
    }

    private void fillDetailsLayout(final ScheduleModel model, final int num) {
        if (model.getPairs().size() > 1) {
            next.setVisibility(View.VISIBLE);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fillDetailsLayout(model, (num+1)%model.getPairs().size());
                }
            });
        } else
            next.setVisibility(View.GONE);
        number.setText(model.getN());
        time.setText(model.getDate() + ", " + model.getStartTime() + "-" + model.getEndTime());
        if (model.getPairs().get(num).getSubgroup() != 0)
            name.setText(model.getPairs().get(num).getName() + ", п/г " + model.getPairs().get(num).getSubgroup());
        else
            name.setText(model.getPairs().get(num).getName());
        teacher.setText(model.getPairs().get(num).getTeacher());
        auditory.setText(model.getPairs().get(num).getClassroom());
        type.setText(model.getPairs().get(num).getType());
        //По нажатию переход на списочный вид
        detailsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.setPosition(model.getDate(), model.getN());
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new DailyScheduleFragment()).commit();
            }
        });
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }



}
