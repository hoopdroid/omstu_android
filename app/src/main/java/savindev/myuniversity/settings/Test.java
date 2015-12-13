package savindev.myuniversity.settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import savindev.myuniversity.R;
import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.serverTasks.AuthorizationTask;
import savindev.myuniversity.serverTasks.GetScheduleTask;
import savindev.myuniversity.serverTasks.GetUniversityInfoTask;


public class Test extends Activity {
    TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.test);

        t = (TextView)findViewById(R.id.textView);

    }


    public void setmain(View view) {
        SharedPreferences.Editor e = getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
        e.putBoolean("test", false).apply();
        t.setText("main");
    }

    public void settest(View view) {
        SharedPreferences.Editor e = getSharedPreferences("UserInfo",Context.MODE_PRIVATE).edit();
        e.putBoolean("test", true).apply();
        t.setText("test");
    }

    public void bum(View view) {
        Thread auth = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 500; i++) {
                    AuthorizationTask at = new AuthorizationTask(getApplicationContext());
                    String[] params = {"vovik0134", "123"};
                    at.execute(params);
                }
            }
        });
        Thread schedule = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 500; i++) {
                    GroupsModel m = new GroupsModel("aa", i, true, "19700101000000");
                    GetScheduleTask gt = new GetScheduleTask(getApplicationContext());
                    gt.execute(m);
                }
            }
        });
        Thread info = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 500; i++) {
                    GetUniversityInfoTask gt = new GetUniversityInfoTask(getApplicationContext(), null);
                    gt.execute();
                }
            }
        });
        auth.start();
        schedule.start();
        info.start();
        try {
            Thread.currentThread().sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
