package savindev.myuniversity.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import savindev.myuniversity.R;

public class VitalizationFragment extends Fragment {

    private SharedPreferences sPref;
    private CheckBox vertical, horisontal;
    private RadioGroup calendar, daily;
    private RadioButton calendarByType, calendarNotAllocated, dailyByType, dailyNotAllocated;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);  //Создание основных элементов вместе с фрагментом
        View view = inflater.inflate(R.layout.vitalization, container, false);
        vertical = (CheckBox) view.findViewById(R.id.full_vertical);
        horisontal = (CheckBox) view.findViewById(R.id.full_horisontal);
        calendar = (RadioGroup) view.findViewById(R.id.radioGroupCalendar);
        daily = (RadioGroup) view.findViewById(R.id.RadioGroupDaily);
        calendarByType = (RadioButton) view.findViewById(R.id.calendar_by_type);
        calendarNotAllocated = (RadioButton) view.findViewById(R.id.calendar_not_allocated);
        dailyByType = (RadioButton) view.findViewById(R.id.daily_by_type);
        dailyNotAllocated = (RadioButton) view.findViewById(R.id.daily_not_allocated);

        sPref = getActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        vertical.setChecked(sPref.getBoolean("full_vertical", false));
        horisontal.setChecked(sPref.getBoolean("full_horisontal", false));
        switch (sPref.getString("calendar", "CALENDAR_NOT_ALLOCATED")) {
            case "CALENDAR_BY_TYPE":
                calendarByType.setChecked(true);
                break;
            case "CALENDAR_NOT_ALLOCATED":
                calendarNotAllocated.setChecked(true);
                break;
            default:
                calendarNotAllocated.setChecked(true);
                break;
        }
        switch (sPref.getString("daily", "DAILY_NOT_ALLOCATED")) {
            case "DAILY_BY_TYPE":
                dailyByType.setChecked(true);
                break;
            case "DAILY_NOT_ALLOCATED":
                dailyNotAllocated.setChecked(true);
                break;
            default:
                dailyNotAllocated.setChecked(true);
                break;
        }
        return view;
    }


    @Override
    public void onDestroy() {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("full_vertical", vertical.isChecked());
        ed.putBoolean("full_horisontal", horisontal.isChecked());
        switch (calendar.getCheckedRadioButtonId()) {
//            case R.id.radio0:
//                ed.putString("calendar", "radio0"); break;
            case R.id.calendar_by_type:
                ed.putString("calendar", "CALENDAR_BY_TYPE");
                break;
//            case R.id.radio2:
//                ed.putString("calendar", "radio2"); break;
            case R.id.calendar_not_allocated:
                ed.putString("calendar", "CALENDAR_NOT_ALLOCATED");
                break;
        }
        switch (daily.getCheckedRadioButtonId()) {
//            case R.id.RadioButton01:
//                ed.putString("daily", "RadioButton01"); break;
            case R.id.daily_by_type:
                ed.putString("daily", "DAILY_BY_TYPE");
                break;
//            case R.id.RadioButton03:
//                ed.putString("daily", "RadioButton03"); break;
            case R.id.daily_not_allocated:
                ed.putString("daily", "DAILY_NOT_ALLOCATED");
                break;
        }
        ed.apply();
        super.onDestroy();
    }
}
