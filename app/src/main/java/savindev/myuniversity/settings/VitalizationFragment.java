package savindev.myuniversity.settings;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        switch (sPref.getString("calendar", "")) {
            case "calendarByType":
                calendarByType.setChecked(true);
                break;
            case "calendarNotAllocated":
                calendarNotAllocated.setChecked(true);
                break;
            default:
                calendarNotAllocated.setChecked(true);
                break;
        }
        switch (sPref.getString("daily", "")) {
            case "dailybyType":
                dailyByType.setChecked(true);
                break;
            case "dailyNotAllocated":
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
                ed.putString("calendar", "calendarByType");
                break;
//            case R.id.radio2:
//                ed.putString("calendar", "radio2"); break;
            case R.id.calendar_not_allocated:
                ed.putString("calendar", "calendarNotAllocated");
                break;
        }
        switch (daily.getCheckedRadioButtonId()) {
//            case R.id.RadioButton01:
//                ed.putString("daily", "RadioButton01"); break;
            case R.id.daily_by_type:
                ed.putString("daily", "dailyByType");
                break;
//            case R.id.RadioButton03:
//                ed.putString("daily", "RadioButton03"); break;
            case R.id.daily_not_allocated:
                ed.putString("daily", "dailyNotAllocated");
                break;
        }
        ed.apply();
        super.onDestroy();
    }
}
