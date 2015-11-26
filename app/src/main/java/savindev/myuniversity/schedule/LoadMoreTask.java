package savindev.myuniversity.schedule;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import savindev.myuniversity.db.DBHelper;

//Реализует подгрузку данных при достижении конца списка
public class LoadMoreTask extends AsyncTask<Integer, Void, ArrayList<ScheduleModel>> {
    private GregorianCalendar calendar;
    private int currentID;
    private boolean isGroup;
    private Context context;
    private ScheduleAdapter adapter;
    private RecyclerView scheduleList;
    boolean isLinear;

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
    protected ArrayList<ScheduleModel> doInBackground(Integer... params) {
        ArrayList<ScheduleModel> data = new ArrayList();
        for (int i = 0; i < params[0]; i++) { //Добавить число записей, равное params[0]
            calendar.add(Calendar.DAY_OF_MONTH, 1); //Каждый раз работа со следующим днем
            //TODO подумать, как нужно обрабатывать выходные дни
            ArrayList<ScheduleModel> daySchedule;
            String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
            if (day.length() < 2)
                day = "0" + day;
            daySchedule = DBHelper.SchedulesHelper.getSchedules(context,
                    "" + calendar.get(Calendar.YEAR) + (calendar.get(Calendar.MONTH) + 1) + day,
                    currentID, isGroup);  //Получение расписания на день

            data.addAll(daySchedule);
        }

        return data;
    }

    @Override
    protected void onPostExecute(ArrayList<ScheduleModel> data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(context, "Данные закончились", Toast.LENGTH_SHORT).show();
            return;
        }
        //Обновить адаптер и вернуть на последнюю просмотренную позицию
        adapter.add(data);
        adapter.notifyDataSetChanged();

        if (isLinear) {
            LinearLayoutManager lm = (LinearLayoutManager)scheduleList.getLayoutManager();
            int index = lm.findFirstVisibleItemPosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            lm.scrollToPositionWithOffset(index, top);
        } else {
            GridLayoutManager lm = (GridLayoutManager)scheduleList.getLayoutManager();
            int index = lm.findFirstVisibleItemPosition();
            int top = (scheduleList.getChildAt(0) == null) ? 0 : scheduleList.getChildAt(0).getTop();
            lm.scrollToPositionWithOffset(index, top);
        }
        context.sendBroadcast(new Intent("FINISH_LMT")); //Отправить запрос на обновление
    }
}
