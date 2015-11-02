package savindev.myuniversity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import savindev.myuniversity.db.DBHelper;

public class SettingsActivity extends AppCompatActivity {
//Testing sqlite getters

    ListView groupsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        groupsList = (ListView)findViewById(R.id.groupsList);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        SQLiteDatabase sqliteDatabase;   DBHelper helper = new DBHelper(this);

        sqliteDatabase = helper.getWritableDatabase();



        ArrayList groups = new ArrayList();
        Cursor cursor = sqliteDatabase.rawQuery("SELECT teacher_lastname FROM Teachers",null);
        if (cursor .moveToFirst()) {

            while (cursor.isAfterLast() == false) {
                String name = cursor.getString(cursor.getColumnIndex("teacher_lastname"));
                groups.add(name);
                cursor.moveToNext();
            }
        }



        // используем адаптер данных
        ArrayAdapter arrayAdapter = new ArrayAdapter(this,	android.R.layout.simple_list_item_1, groups);

        groupsList.setAdapter(arrayAdapter);
    }
}
