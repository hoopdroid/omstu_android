package savindev.myuniversity.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import savindev.myuniversity.db.DBHelper;

/**
 * Реализует подгрузку данных при достижении конца списка в расписании
 */

public class LoadMoreTask extends AsyncTask<Integer, Void, TreeMap<GregorianCalendar, Integer>> {
    private GregorianCalendar calendar;
    private int oldMonth;
    private int currentID;
    private boolean isGroup;
    private Context context;
    private ScheduleAdapter adapter;
    private RecyclerView scheduleList;
    private boolean isLinear;
    private ArrayList<ScheduleModel> data;
    private boolean isFinished = false;

    public LoadMoreTask(Context context, GregorianCalendar calendar, int currentID, boolean isGroup,
                        ScheduleAdapter adapter, RecyclerView scheduleList, boolean isLinear) {
        this.context = context;
        this.calendar = calendar;
        this.currentID = currentID;
        this.isGroup = isGroup;
        this.adapter = adapter;
        this.scheduleList = scheduleList;
        this.isLinear = isLinear;
    }

    @Override
    protected TreeMap<GregorianCalendar, Integer> doInBackground(Integer... params) {
        int oldDataSize = adapter.getItemCount();
        data = new ArrayList<>(); //Лист, используемый для адаптера
        TreeMap<GregorianCalendar, Integer> positions = new TreeMap<>(); //Возвращает позиции для элементов
        for (int i = 0; i < params[0]; i++) { //Добавить число записей, равное params[0]
            oldMonth = calendar.get(Calendar.MONTH); //Предыдущее состояние календаря для определения месяца
            calendar.add(Calendar.DAY_OF_MONTH, 1); //Каждый раз работа со следующим днем
            int dayCount = new DBHelper(context).getUniversityInfoHelper().getDaysInWeek();
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY || //Воскресенье, =1, точно нет пар
                    calendar.get(Calendar.DAY_OF_WEEK) > dayCount + 1) //Суббота =7, если 6-дневка выходного быть не должно
                continue;
            ArrayList<ScheduleModel> daySchedule;
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            String month = String.valueOf((calendar.get(Calendar.MONTH)) + 1);
            if (day.length() < 2)
                day = "0" + day;
            if (month.length() < 2)
                month = "0" + month;
            daySchedule = DBHelper.SchedulesHelper.getSchedules(context, "" + calendar.get(Calendar.YEAR) + month + day,
                    currentID, isGroup);  //Получение расписания на день
            if (daySchedule == null)
                return positions;   //null - семестр уже закончился

            Collections.sort(daySchedule, new Comparator<ScheduleModel>() { //Сортировка сначала по отмененным, потом по началу пары
                @Override
                public int compare(ScheduleModel lhs, ScheduleModel rhs) {
                    if (lhs.getStartTime().equals(rhs.getStartTime()))
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            return Boolean.compare(lhs.getPairs().get(0).isCancelled(), rhs.getPairs().get(0).isCancelled());
                        }
                    return lhs.getStartTime().compareTo(rhs.getStartTime());
                }
            });
            if (!isGroup) //Сортировать только для преподавателей
                daySchedule = groupPacking(daySchedule);
            if (!isLinear) {//Переформировывать только для сетки
                toGridView(daySchedule);
            }
            positions.put((GregorianCalendar) calendar.clone(), data.size() + 1 + oldDataSize);
            data.addAll(daySchedule);
        }
        return positions;
    }

    private ArrayList<ScheduleModel> groupPacking(ArrayList<ScheduleModel> pairs) { //Решение проблемы со множеством групп в одно время у преподавателей. считается, что у него не может быть несколько пар в одно время - get(0)
        ArrayList<ScheduleModel> schedule = new ArrayList<>();
        ScheduleModel pair;
        int skip = 1;
        for (int i = 0; i < pairs.size(); ) {
            pair = pairs.get(i);
            while (i + skip < pairs.size() && pairs.get(i).getN().equals(pairs.get(i + skip).getN()) &&
                    pairs.get(i).getPairs().get(0).getName().equals(pairs.get(i + skip).getPairs().get(0).getName()) &&
                    pairs.get(i).getPairs().get(0).isCancelled() == (pairs.get(i + skip).getPairs().get(0).isCancelled())) {
                if (pairs.get(i + skip) == null) {
                    break;
                }
                pair.getPairs().get(0).setTeacher(pair.getPairs().get(0).getTeacher() + ", " + pairs.get(i + skip).getPairs().get(0).getTeacher());
                skip++;
            }
            schedule.add(pair);

            i += skip;
            skip = 1;
        }
        return schedule;
    }

    private void toGridView(ArrayList<ScheduleModel> pairs) { //переформирование для сетки
        //Добавить окна
        int i = 0;
        int pairCount = DBHelper.getInstance(context).getPairsHelper().getPairsInDay(context);
        String pairNum = "";
        for (; i < pairs.size(); i++) { //окна до последней пары
            if (pairs.get(i).getN().equals(pairNum)) {//Номер пары совпадает с предыдущим, пару продублировать в предыдущую
                pairs.get(i-1).addListItem(pairs.get(i).getPairs());
                pairs.remove(i);
                i--;
                continue;
            }
            if (Integer.parseInt(pairs.get(i).getN()) != (i + 1)) {//Если номер пары не совпадает с позицией - заткнуть дырку пустотой
                pairs.add(i, null);
                continue;
            }
            pairNum = pairs.get(i).getN();
        }
        for (; i < pairCount; i++) //окна от последней пары и до конца дня
            pairs.add(i, null);

        //Добавить дату
        pairs.add(0, new ScheduleModel(CellType.DAY, day()));
        pairs.add(new ScheduleModel(CellType.DATE, calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1)));

        //Добавить месяц
        if (oldMonth != calendar.get(Calendar.MONTH)) //смена месяца
            pairs.add(0, new ScheduleModel(CellType.MONTH, calendar.get(Calendar.MONTH) + ""));
    }

    private String day() { //По календарю отдает день недели
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                return "ВС";
            case Calendar.MONDAY:
                return "ПН";
            case Calendar.TUESDAY:
                return "ВТ";
            case Calendar.WEDNESDAY:
                return "СР";
            case Calendar.THURSDAY:
                return "ЧТ";
            case Calendar.FRIDAY:
                return "ПТ";
            case Calendar.SATURDAY:
                return "СБ";
        }
        return null;
    }

    @Override
    protected void onPostExecute(TreeMap<GregorianCalendar, Integer> positions) {
        if (isFinished) //Если уже заходили, не выполнять повторно
            return; //Костыль, нужен для пролистывания к позиции
        isFinished = true;
        if (data == null || data.isEmpty()) {
            //Toast.makeText(context, "Данные закончились", Toast.LENGTH_SHORT).show();
            return;
        }
        //Обновить адаптер и вернуть на последнюю просмотренную позицию
        adapter.add(data);
        adapter.notifyDataSetChanged();

        if (isLinear) {
            LinearLayoutManager lm = (LinearLayoutManager) scheduleList.getLayoutManager();
            int index = lm.findFirstVisibleItemPosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            lm.scrollToPositionWithOffset(index, top);
        } else {
            GridLayoutManager lm = (GridLayoutManager) scheduleList.getLayoutManager();
            int index = lm.findFirstVisibleItemPosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            lm.scrollToPositionWithOffset(index, top);
        }
        context.sendBroadcast(new Intent("FINISH_LMT")); //Отправить запрос на обновление
    }
}
