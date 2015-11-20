package savindev.myuniversity.schedule;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import savindev.myuniversity.serverTasks.Schedule;

/**
 * Created by ilyas on 18.11.2015.
 */
public class DateUtil {

    public DateUtil() {
    }

    public static String dateFormatIncrease(ArrayList<Schedule> schedule,int index,String previousValue){
        String dt = previousValue;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        if(index>0){
            try {
                c.setTime(sdf.parse(dt));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            c.add(Calendar.DATE, schedule.get(index).SCHEDULE_INTERVAL);  // number of days to add
            dt = sdf.format(c.getTime());  // dt is now the new date
            return  dt;}

        else
            return dt;
    }

    public static String formatStandart(String inputDate){
        String outputDate = "";

        SimpleDateFormat fromUser = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat standartFormat = new SimpleDateFormat("yyyyMMdd");

        try {

            outputDate = standartFormat.format(fromUser.parse(inputDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  outputDate;
    }
}
