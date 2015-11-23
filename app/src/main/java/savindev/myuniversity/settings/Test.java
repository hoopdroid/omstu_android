package savindev.myuniversity.settings;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import savindev.myuniversity.R;

/**
 * Created by Katena on 23.11.2015.
 */
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
}
