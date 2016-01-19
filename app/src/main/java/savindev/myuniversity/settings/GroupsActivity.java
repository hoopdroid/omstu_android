package savindev.myuniversity.settings;

import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import savindev.myuniversity.R;


public class GroupsActivity extends AppCompatActivity {

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editor.putBoolean("isFirstSetting", false);
        editor.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        editor = settings.edit();

        setContentView(R.layout.container);
        Bundle titleBundle = getIntent().getExtras();
        String toolbarTitle = "Расписания";
        if (titleBundle != null)
            toolbarTitle = titleBundle.getString("Title", "Отображаемые расписания");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();


            }
        });
        getSupportActionBar().setTitle(toolbarTitle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, new GroupsFragment());
        fragmentTransaction.commit();


    }
}
