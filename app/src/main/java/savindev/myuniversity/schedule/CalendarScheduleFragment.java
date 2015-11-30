package savindev.myuniversity.schedule;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.R;

/**
 * Класс, отображающий расписание на определенный срок в виде сетки с предметами. Имеет различные методы фильтрации предметов
 * и возможность отображения подробностей для любого из них
 */

public class CalendarScheduleFragment extends AbstractSchedule {

    private LinearLayout filtersLayout, detailsLayout;
    private int monthCount;
    private int pairCount;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = preInitializeData(inflater, container);

        if (view == null) {//Если данные существуют:
            monthCount = 0;
            SharedPreferences userInfo = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            view = inflater.inflate(R.layout.fragment_calendar_schedule, container, false);
            //        View header = LayoutInflater.from(getActivity()).inflate(R.layout.one_calendar_pair, scheduleList, false);
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            isLinear = false;

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

            scheduleList = (RecyclerView) view.findViewById(R.id.calendarSchedule);
            //TODO получить из базы число пар за день  int pairCount = DBHelper.getPairCount(context);
            pairCount = 5;
            //*2 + 2: *2 - каждая пара занимает двойной размер, +2 - дополнительные поля нормального размера на дату и день недели
            glm = new GridLayoutManager(getActivity(), pairCount * 2 + 2); //2 поля на дату и день недели
            scheduleList.setLayoutManager(glm);
            adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
            glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() { //Установка длины ячейки
                @Override
                public int getSpanSize(int position) {
//                    if (adapter.isHeader(position)) {
//                        monthCount = monthCount + 1;
//                        return glm.getSpanCount(); //новый месяц, ячейка с месяцем на всю строку
//                    } else
//                    if ((position - monthCount) % (pairCount + 2) == 0 || (position - monthCount) % (pairCount + 2) == pairCount + 1)
//                        return 1;
//                    else
//                    return 2; //Ячейки с парами занимают в 2 раза больше места ячеек с датами
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
        }
        return view;
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
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public class ScheduleAdapter extends savindev.myuniversity.schedule.ScheduleAdapter {
        public static final int VIEW_DATE = 1;
        public static final int VIEW_DAY = 2;
        public static final int VIEW_MONTH = 3;
        public static final int VIEW_PAIR = 4;

        //        private static final int ITEM_VIEW_TYPE_HEADER = 0;
//        private static final int ITEM_VIEW_TYPE_ITEM = 1;
//        private final View header;
        ScheduleAdapter(List<ScheduleModel> models) {
            //            if (header == null) {
//                throw new IllegalArgumentException("header may not be null");
//            }
//            this.header = header;
            super(getActivity(), models);
        }

//        public boolean isHeader(int position) {
//            return (position == 0 || !models.get(position).getDate().substring(0, 1).equals(models.get(position - 1).getDate().substring(0, 1)));
//        }

        @Override
        public int getItemViewType(int position) {
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
        public ScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//            if (viewType == ITEM_VIEW_TYPE_HEADER) {
//                return new ScheduleViewHolder(header);
//            }
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_calendar_pair, viewGroup, false);
            return new ScheduleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ScheduleViewHolder scheduleViewHolder, int i) {
//            if (isHeader(i)) {
//                scheduleViewHolder.pairName.setText(models.get(i).getDate());
//                return;
//            }
//            if ((i - monthCount) % (pairCount + 2) == 0) { //Если 1 - это день недели
//                scheduleViewHolder.pairName.setText(models.get(i).getDate().substring(0, 1));
//            } else if ((i - monthCount) % (pairCount + 2) == pairCount + 1) { //Если последний - дата
//                scheduleViewHolder.pairName.setText(models.get(i).getDate().substring(1));
//            } else {// Иначе это пара
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
                        scheduleViewHolder.pairName.setText(models.get(i).getName());
                        scheduleViewHolder.pairName.setVisibility(View.VISIBLE);
                        scheduleViewHolder.pairName.setBackgroundColor(Color.WHITE);
                        break;
                    case MONTH:
                        scheduleViewHolder.pairName.setText(month(models.get(i).getDate()));
                        scheduleViewHolder.pairName.setVisibility(View.VISIBLE);
                        scheduleViewHolder.pairName.setBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                        break;
                }

//            }

        }

        private String month(String date) {
            int d =  Integer.parseInt(date);
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

//        @Override
//        public int getItemViewType(int position) {
//            return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
//        }

    }


}
