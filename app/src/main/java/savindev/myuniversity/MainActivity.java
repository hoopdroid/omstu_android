package savindev.myuniversity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.util.ArrayList;

import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.schedule.DailyScheduleFragment;
import savindev.myuniversity.schedule.GroupsModel;
import savindev.myuniversity.settings.SettingsFragment;
import savindev.myuniversity.welcomescreen.FirstStartActivity;

public class MainActivity extends AppCompatActivity {

    static Toolbar toolbar;
    String username;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        ArrayList<GroupsModel> list = DBHelper.UsedSchedulesHelper.getGroupsModelList(getApplicationContext());

        if(settings.getBoolean("isFirstStart",true)) {

            Intent intent = new Intent(getApplicationContext(), FirstStartActivity.class);
            startActivity(intent);
            finish();

        } else {

            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            getUserSettings();
            initDrawer();

        }



    }


   private void getUserSettings(){

       SharedPreferences settings = getSharedPreferences("UserInfo", 0);

       username = settings.getString("UserFirstName","")+  " " +settings.getString("UserLastName","")+" "+getUserGroup(settings.getInt("UserGroup",0),getApplicationContext());

       email = settings.getString("email","no email");
    }

    void initDrawer(){

        PrimaryDrawerItem itemSchedule = new PrimaryDrawerItem().withName(R.string.drawer_schedule).withIcon(R.drawable.ic_schedule).withSelectedIcon(R.drawable.ic_schedule_select);
        PrimaryDrawerItem itemNavigation = new PrimaryDrawerItem().withName(R.string.drawer_navigator).withIcon(R.drawable.ic_navigation).withSelectedIcon(R.drawable.ic_navigation_select);
        PrimaryDrawerItem itemNotes = new PrimaryDrawerItem().withName(R.string.drawer_notes).withIcon(R.drawable.ic_notes).withSelectedIcon(R.drawable.ic_notes_select);
        PrimaryDrawerItem itemNews = new PrimaryDrawerItem().withName(R.string.drawer_news).withIcon(R.drawable.ic_news).withBadge("12").withSelectedIcon(R.drawable.ic_news_select);
        PrimaryDrawerItem itemEducation = new PrimaryDrawerItem().withName(R.string.drawer_education).withIcon(R.drawable.ic_school).withSelectedIcon(R.drawable.ic_school_select);
        SecondaryDrawerItem itemSettings = new SecondaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings).withSelectedIcon(R.drawable.ic_settings_select);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.drawer_header)
                .addProfiles(
                        new ProfileDrawerItem().withName(username).withEmail(email).withIcon(getResources().getDrawable(R.drawable.ic_account_circle_white_48dp))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                        addfragment(R.string.profile_text,new ProfileFragment());

                        return false;
                    }
                })
                .build();

        Drawer result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemSchedule,

                        itemNavigation,

                        itemNotes,

                        itemNews,

                        itemEducation,

                        itemSettings,
                        new SecondaryDrawerItem()


                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position) {
                            case 1:

                                addfragment(R.string.drawer_schedule,new DailyScheduleFragment());

                                break;
                            case 2:
                                addfragment(R.string.drawer_navigator,new WelcomeFragment());
                                break;
                            case 3:
                                addfragment(R.string.drawer_notes,new WelcomeFragment());
                                break;
                            case 4:
                                addfragment(R.string.drawer_news,new NewsFragment());
                                break;
                            case 5:
                                addfragment(R.string.drawer_education,new WelcomeFragment());
                                break;
                            case 6:
                                addfragment(R.string.drawer_settings,new SettingsFragment());
                                break;

                        }
                        return false;
                    }
                })
                .build();
    }

    void addfragment(int title,Fragment fragment){

        toolbar.setTitle(title);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction=fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.content_main,fragment);
        fragmentTransaction.commit();
        Toast.makeText(getApplicationContext(), title, Toast.LENGTH_SHORT).show();

    }

    //Проверка на подключение к интернету
    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }


// [CR] Нельзя так делать. ты работаешь с базой данных вне класса базы данных. утащи это дело в DBHelper
    public static String getUserGroup(int id,Context context){
        String groupName= "";

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = dbHelper.getReadableDatabase();
        String find = "SELECT * FROM  "+ DBHelper.GroupsHelper.TABLE_NAME + " WHERE "+ DBHelper.GroupsHelper.COL_ID_GROUP +" = " +id ;
        Cursor cursor = sqLiteDatabase.rawQuery(find,null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {

            groupName=cursor.getString(cursor.getColumnIndex(DBHelper.GroupsHelper.COL_GROUP_NAME));
            cursor.moveToNext();

        }
        cursor.close();
        return groupName;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE); //Удалить активную группу
        settings.edit().remove("openGroup");
        settings.edit().remove("openIsGroup");
    }
}
