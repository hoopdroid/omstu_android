package savindev.myuniversity.schedule;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.settings.Colores;

public class DailyScheduleFragment extends AbstractSchedule {
    /**
     * Класс, отображающий расписание на определенный срок в виде списка предметов с параметрами
     */
    private ArrayList<String> values;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = preInitializeData(inflater, container);

        if (view == null) {//Если данные существуют:
            view = inflater.inflate(R.layout.fragment_daily_schedule, container, false);
            MainActivity.fab.hide();
            mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
            scheduleList = (RecyclerView) view.findViewById(R.id.schedule);
            llm = new LinearLayoutManager(getActivity());
            adapter = new ScheduleAdapter(new ArrayList<ScheduleModel>());
            isLinear = true;
            scheduleList.setLayoutManager(llm);

            colores = Colores.valueOf(getActivity().getSharedPreferences(
                    "settings", Context.MODE_PRIVATE).getString("daily", "DAILY_NOT_ALLOCATED"));
            switch (colores) {
                case DAILY_BY_TYPE:
                    values = new ArrayList<>(DBHelper.getInstance(getActivity()).getSchedulesHelper().getGroupLessonsTypes(getActivity(), currentID, isGroup));
            }

            postInitializeData();

            if (MainActivity.getPositionDate() != null) {
                String date = MainActivity.getPositionDate();
                try { //Костыль, чтобы сначала получились и обработались данные, а потом пустило к позиции
                    lmt.onPostExecute(lmt.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
                GregorianCalendar c = new GregorianCalendar();
                c.set(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(4, 6)),
                        Integer.parseInt(date.substring(6)));
                int p = getPostition(c);
                if (p != 0)
                    scheduleList.scrollToPosition(p - 1);
                MainActivity.clearPositions();
            }
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            inflater.inflate(R.menu.daily_schedule, menu);}
        else
            inflater.inflate(R.menu.daily_schedule_old_api, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FragmentTransaction ft;
        switch (item.getItemId()) {
            case R.id.transition:
                //Переход на календарный вид
                getActivity().getSharedPreferences("settings", 0).edit().putString("lastOpenSchedule", "grid").apply();
                MainActivity.setOpen(currentID, isGroup, currentGroup); //Запись по id. потом по нему открывать расписание
                ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_main, new CalendarScheduleFragment()).commit();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    public class ScheduleAdapter extends savindev.myuniversity.schedule.ScheduleAdapter {

        ScheduleAdapter(List<ScheduleModel> models) {
            super(getActivity(), models);
        }

        @Override
        public ScheduleViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.schedule_line_test, viewGroup, false);
            return new ScheduleViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ScheduleViewHolder scheduleViewHolder, final int i) {


            scheduleViewHolder.pairNumber.setText(models.get(i).getN());
            if (!isGroup)
                scheduleViewHolder.pairHandler.setImageDrawable(getResources().getDrawable(R.drawable.ic_account_multiple));
            else
                scheduleViewHolder.pairHandler.setImageDrawable(getResources().getDrawable(R.drawable.ic_account));
            scheduleViewHolder.pairTime.setText(models.get(i).getStartTime() + "-\n" + models.get(i).getEndTime());
            scheduleViewHolder.pairDayWeek.setText(DateUtil.getDayWeek(DateUtil.formatDate(models.get(i).getDate())));
            scheduleViewHolder.pairName.setText(models.get(i).getPairs().get(0).getName());
            if (models.get(i).getPairs().get(0).getSubgroup() != 0)
                scheduleViewHolder.pairName.setText(scheduleViewHolder.pairName.getText() + ", подгруппа " + models.get(i).getPairs().get(0).getSubgroup());
            if (models.get(i).getPairs().get(0).isCancelled())
                scheduleViewHolder.pairName.setText(scheduleViewHolder.pairName.getText() + " (отм.)");
            scheduleViewHolder.pairTeacher.setText(models.get(i).getPairs().get(0).getTeacher());
            scheduleViewHolder.pairAuditory.setText(models.get(i).getPairs().get(0).getClassroom());
            scheduleViewHolder.pairType.setText(models.get(i).getPairs().get(0).getType());
            scheduleViewHolder.pairDate.setText(DateUtil.formatDate(models.get(i).getDate()));

            if (models.get(i).getPairs().get(0).getTeacher().equals("")) {
                scheduleViewHolder.teacherLayout.setVisibility(View.GONE);
            } else
                scheduleViewHolder.teacherLayout.setVisibility(View.VISIBLE);
            if (i == 0 || !models.get(i).getDate().equals(models.get(i - 1).getDate())) {
                scheduleViewHolder.pairDate.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_dark));
                scheduleViewHolder.pairDateLayout.setVisibility(View.VISIBLE);
            } else {
                scheduleViewHolder.pairDateLayout.setVisibility(View.GONE);
            }

            scheduleViewHolder.cv.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    Intent intent = new Intent(context, PairInfoActivity.class);
                    intent.putExtra("schedulemodel",models.get(i));
                    intent.putExtra("pairtime", scheduleViewHolder.pairDate.getText().toString().substring(0, 5) + " "
                            + scheduleViewHolder.pairDayWeek.getText().toString() + " " + scheduleViewHolder.pairTime.getText().toString().substring(0, 5));

                    startActivity(intent);

                }
            });

            if (color == null)
                color = getActivity().getResources().getIntArray(R.array.pairs_colors_light);
            switch (colores) {
                case DAILY_BY_TYPE:
                    scheduleViewHolder.scheduleLineContainer.setBackgroundColor(color[values.indexOf(models.get(i).getPairs().get(0).getType())]);
                   // scheduleViewHolder.pairTime.setBackgroundColor(color[values.indexOf(models.get(i).getPairs().get(0).getType())]);
                    break;
            }

            //Work with Notes
            if (!models.get(i).getNotes().isEmpty() && models.get(i).getNotes().get(0).getIsDone() == 0) {
                scheduleViewHolder.noteLayout.setVisibility(View.VISIBLE);
                scheduleViewHolder.pairNote.setText(models.get(i).getNotes().get(0).getName());
                switch (models.get(i).getNotes().get(0).getPriority()) {
                    case HIGH:
                        //  scheduleViewHolder.noteLayout.setBackgroundColor(getResources().getColor(R.color.md_red_400));
                        scheduleViewHolder.pairNoteImage.setImageResource(R.drawable.ic_attach_note_red);
                        scheduleViewHolder.pairNote.setTextColor(getResources().getColor(R.color.md_red_800));
                        break;
                    case MEDIUM:
                        //  scheduleViewHolder.noteLayout.setBackgroundColor(getResources().getColor(R.color.md_orange_400));
                        scheduleViewHolder.pairNoteImage.setImageResource(R.drawable.ic_attach_note_orange);
                        scheduleViewHolder.pairNote.setTextColor(getResources().getColor(R.color.md_orange_800));
                        break;
                    case LOW:
                        // scheduleViewHolder.noteLayout.setBackgroundColor(getResources().getColor(R.color.md_yellow_400));
                        scheduleViewHolder.pairNoteImage.setImageResource(R.drawable.ic_attach_note_yellow);
                        scheduleViewHolder.pairNote.setTextColor(getResources().getColor(R.color.md_yellow_800));
                        break;
                }
            } else
                scheduleViewHolder.noteLayout.setVisibility(View.GONE);
        }
    }


}