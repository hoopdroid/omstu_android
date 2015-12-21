package savindev.myuniversity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.welcomescreen.FirstStartActivity;

public class ProfileActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportActionBar().setTitle("Мой профиль");

        SharedPreferences settings = getSharedPreferences("UserInfo", 0);

        String username = settings.getString("UserFirstName","")+ " "+settings.getString("UserMiddleName","") +" " +settings.getString("UserLastName","");

        toolbar.setTitle(username);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
                builder.setTitle("Сменить пользователя?")
                        .setMessage("Ваши данные могут быть утеряны")

                        .setCancelable(false)
                        .setPositiveButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(ProfileActivity.this, FirstStartActivity.class);
                                        deleteUserPreferences();
                                        dbHelper.getUsedSchedulesHelper().deleteMainSchedule(getApplicationContext());
                                        startActivity(i);
                                        finish();
                                    }
                                }).setNegativeButton("Нет",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }
    private void deleteUserPreferences(){

        SharedPreferences.Editor editor = getSharedPreferences("UserInfo", Context.MODE_PRIVATE).edit();
        editor.remove("UserLastName");
        editor.remove("UserFirstName");
        editor.remove("UserMiddleName");
        editor.remove("UserGroup");
        editor.remove("email");
        editor.remove("password");
        editor.remove("UserId").commit();
    }
}
