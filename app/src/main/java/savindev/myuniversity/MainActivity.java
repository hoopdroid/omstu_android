package savindev.myuniversity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.notes.AttachActivity;
import savindev.myuniversity.notes.NotesFragment;
import savindev.myuniversity.performance.PerformanceFragment;
import savindev.myuniversity.schedule.DailyScheduleFragment;
import savindev.myuniversity.settings.GroupsActivity;
import savindev.myuniversity.settings.SettingsFragment;
import savindev.myuniversity.welcomescreen.FirstStartActivity;
import savindev.myuniversity.welcomescreen.NotInternetFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static MainActivity mainActivity ;
    public static Toolbar toolbar;
    public static Fab fab;
    private TextView noteAdd;
    private TextView homeworkAdd;
    private TextView reminderAdd;
    private String username;
    private String email;
    private MaterialSheetFab materialSheetFab;
    static Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity=this;
        SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if (settings.getBoolean("isFirstStart", true)) {
            if (isNetworkConnected(getApplication())) {
                Intent intent = new Intent(getApplicationContext(), FirstStartActivity.class);
                this.finish();
                startActivity(intent);
            } else {
                setContentView(R.layout.activity_main);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_main, new NotInternetFragment()).commit();}

            } else {

            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("");
            getUserSettings();
            initDrawer();
            addfragment(R.string.drawer_schedule, new DailyScheduleFragment());
            fab = (Fab) findViewById(R.id.fab);
            fab.hide();
            View sheetView = findViewById(R.id.fab_sheet);
            View overlay = findViewById(R.id.overlay);
            int sheetColor = getResources().getColor(R.color.md_white_1000);
            int fabColor = getResources().getColor(R.color.accent);

            noteAdd = (TextView) findViewById(R.id.fab_sheet_item_note);
            homeworkAdd = (TextView) findViewById(R.id.fab_sheet_item_homework);
            reminderAdd = (TextView) findViewById(R.id.fab_sheet_item_reminder);
            noteAdd.setOnClickListener(this);
            homeworkAdd.setOnClickListener(this);
            reminderAdd.setOnClickListener(this);
            materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                    sheetColor, fabColor);
            DBHelper dbHelper = new DBHelper(this);

            Log.d("SEMESTER NUMBER",Integer.toString(dbHelper.getSemestersHelper().getNumSemesterFromDate("20150910")));

        }
    }

    public static Drawer getDrawer() {
        return result;
    }

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    private void getUserSettings() {
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        username = settings.getString("UserFirstName", "") + " " + settings.getString("UserLastName", "");
        email = settings.getString("email", "");
    }

    private void initDrawer() {

        PrimaryDrawerItem itemSchedule = new PrimaryDrawerItem().withName(R.string.drawer_schedule).withIcon(R.drawable.ic_calendar_clock).withSelectedIcon(R.drawable.ic_schedule_select);
        PrimaryDrawerItem itemNavigation = new PrimaryDrawerItem().withName(R.string.drawer_navigator).withIcon(R.drawable.ic_map_marker).withSelectedIcon(R.drawable.ic_navigation_select);
        PrimaryDrawerItem itemNotes = new PrimaryDrawerItem().withName(R.string.drawer_notes).withIcon(R.drawable.ic_note).withSelectedIcon(R.drawable.ic_notes_select);
        PrimaryDrawerItem itemNews = new PrimaryDrawerItem().withName(R.string.drawer_news).withIcon(R.drawable.ic_library_books).withSelectedIcon(R.drawable.ic_news_select);
        PrimaryDrawerItem itemEducation = new PrimaryDrawerItem().withName(R.string.drawer_education).withIcon(R.drawable.ic_school).withSelectedIcon(R.drawable.ic_school_select);
        PrimaryDrawerItem itemPerformance = new PrimaryDrawerItem().withName(R.string.drawer_performance).withIcon(R.drawable.ic_chart_line).withSelectedIcon(R.drawable.ic_chart_line_select);
        SecondaryDrawerItem itemSettings = new SecondaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings_box).withSelectedIcon(R.drawable.ic_settings_select);

        AccountHeader headerResult;

        if(username.equals("")||email.equals("")){
                    headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.drawer_header)
                    .build();
        }

        else {
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.drawer_header)
                    .addProfiles(
                            new ProfileDrawerItem().withName(username).withEmail(email).withIcon(getResources().getDrawable(R.drawable.ic_account_circle_white_48dp))
                    )
                    .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                        @Override
                        public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {

                            Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(i);

                            return false;
                        }
                    })
                    .withSelectionListEnabledForSingleProfile(false)
                    .build();
        }

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .addDrawerItems(
                        itemSchedule,
                        itemNavigation,
                        itemNotes,
                        itemNews,
                        itemEducation,
                        itemPerformance,
                        new DividerDrawerItem(),
                        itemSettings
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {

                            case 1:
                                addfragment(R.string.drawer_schedule, new DailyScheduleFragment());
                                break;
                            case 2:
                                addfragment(R.string.drawer_navigator, new WelcomeFragment());
                                break;
                            case 3:
                                addfragment(R.string.drawer_notes, new NotesFragment());
                                break;
                            case 4:
                                addfragment(R.string.drawer_news, new NewsFragment());
                                break;
                            case 5:
                                addfragment(R.string.drawer_education, new WelcomeFragment());
                                break;
                            case 6:
                                addfragment(R.string.drawer_performance, new PerformanceFragment());
                                break;
                            case 8:
                                addfragment(R.string.drawer_settings, new SettingsFragment());
                                break;
                        }
                        return false;
                    }
                })
                .build();
    }

   public void addfragment(int title, Fragment fragment) {
        toolbar.setTitle(title);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.content_main, fragment);
        fragmentTransaction.commit();
    }

    //Проверка на подключение к интернету
    public static boolean isNetworkConnected(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null;
    }

    private static String mFragment;

    public static String getFragment() {
        return mFragment;
    }

    public static void setFragment(String fragment) {
        mFragment = fragment;
    }

    private static View view;

    public static View getView() {
        return view;
    }

    public static void setView(View v) {
        view = v;
    }

    private static int openGroup;
    private static boolean openIsGroup;
    private static String openGroupName;
    private static String positionDate;
    private static String positionN;

    public static int getOpenGroup() {
        return openGroup;
    }

    public static boolean isOpenIsGroup() {
        return openIsGroup;
    }

    public static String getOpenGroupName() {
        return openGroupName;
    }

    public static String getPositionDate() {
        return positionDate;
    }

    public static String getPositionN() {
        return positionN;
    }

    public static void clearPositions() {
        positionDate = null;
        positionN = null;
    }

    public static void setOpen(int openGroup, boolean openIsGroup, String openGroupName) {
        MainActivity.openGroup = openGroup;
        MainActivity.openIsGroup = openIsGroup;
        MainActivity.openGroupName = openGroupName;
    }

    public static void setPosition(String positionDate, String positionN) {
        MainActivity.positionDate = positionDate;
        MainActivity.positionN = positionN;
    }


    @Override
    public void onClick(View v) {
        if (v == noteAdd) {
            Intent i = new Intent(getApplicationContext(), AttachActivity.class);
            i.putExtra("TypeAttach", "Note");
            startActivity(i);
            materialSheetFab.hideSheet();
        }
        if( v == homeworkAdd || v == reminderAdd){
            showDevSnackBar(v);
        }
    }

    public void refreshActivity(){

        this.finish();
    }

    private void showDevSnackBar(View v){
        Snackbar snackbar = Snackbar
                .make(v,"К сожалению, пока можно добавить только заметку", Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
