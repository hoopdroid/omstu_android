package savindev.myuniversity.schedule;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import savindev.myuniversity.R;

/**
 * Адаптер с общими методами для расписания-списка и расписания-сетки
 */

public abstract class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {

    public class ScheduleViewHolder extends RecyclerView.ViewHolder {

        protected TextView pairNumber;
        protected TextView pairTime;
        protected TextView pairName;
        protected TextView pairTeacher;
        protected TextView pairAuditory;
        protected TextView pairType;
        protected TextView pairDate;
        protected TextView pairDayWeek;
        protected ImageView pairHandler;
        protected RelativeLayout pairDateLayout;
        protected RelativeLayout teacherLayout;
        protected RelativeLayout noteLayout;
        protected TextView pairNote;
        protected ImageView pairNoteImage;
        protected CardView cv;

        protected TextView dublPairName;

        ScheduleViewHolder(View itemView) {
            super(itemView);
            pairNumber = (TextView) itemView.findViewById(R.id.pairNumber);
            pairTime = (TextView) itemView.findViewById(R.id.pairTime);
            pairName = (TextView) itemView.findViewById(R.id.pairName);
            pairTeacher = (TextView) itemView.findViewById(R.id.pairTeacher);
            pairAuditory = (TextView) itemView.findViewById(R.id.pairAuditory);
            pairType = (TextView) itemView.findViewById(R.id.pairType);
            pairDate = (TextView) itemView.findViewById(R.id.pairDate);
            pairHandler = (ImageView) itemView.findViewById(R.id.teacherIcon);
            pairDateLayout = (RelativeLayout) itemView.findViewById(R.id.pairDateLayout);
            teacherLayout = (RelativeLayout) itemView.findViewById(R.id.teacherLayout);
            pairDayWeek = (TextView) itemView.findViewById(R.id.pairDayWeek);
            cv = (CardView) itemView.findViewById(R.id.cv);
            pairNote = (TextView) itemView.findViewById(R.id.pairNote);
            pairNoteImage = (ImageView) itemView.findViewById(R.id.pairNoteImage);
            noteLayout = (RelativeLayout)itemView.findViewById(R.id.noteLayout);
            dublPairName = (TextView) itemView.findViewById(R.id.dublPairName);

        }
    }

    List<ScheduleModel> models;
    Context context;

    ScheduleAdapter(Context context, List<ScheduleModel> models) {
        this.models = models;
        this.context = context;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public void add(ArrayList<ScheduleModel> data) {
        this.models.addAll(data);
    }

    public void deleteData() {
        models.clear();
    }
}
