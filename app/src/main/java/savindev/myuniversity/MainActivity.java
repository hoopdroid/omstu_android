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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondarySwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.db.DBRequest;
import savindev.myuniversity.schedule.DailyScheduleFragment;
import savindev.myuniversity.settings.SettingsFragment;
import savindev.myuniversity.welcomescreen.FirstStartActivity;
import savindev.myuniversity.welcomescreen.NotInternetFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static Toolbar toolbar;
    public static Fab fab;
    TextView noteadd;
    String username;
    String email;
    MaterialSheetFab materialSheetFab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        if (settings.getBoolean("isFirstStart", true)) {
            if (isNetworkConnected(getApplication())) {
                Intent intent = new Intent(getApplicationContext(), FirstStartActivity.class);
                this.finish();
                startActivity(intent);
            } else { //Если интернета нет - предложить запуститься еще раз
                setContentView(R.layout.activity_main);
                FragmentTransaction fragmentTransaction=getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_main, new NotInternetFragment()).commit();
            }
        } else {
            setContentView(R.layout.activity_main);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getUserSettings();
            initDrawer();
            DBHelper dbHelper = DBHelper.getInstance(this);
            addfragment(R.string.drawer_schedule, new DailyScheduleFragment());
            fab = (Fab)findViewById(R.id.fab);
            fab.hide();
            View sheetView = findViewById(R.id.fab_sheet);
            View overlay = findViewById(R.id.overlay);
            int sheetColor = getResources().getColor(R.color.md_white_1000);
            int fabColor = getResources().getColor(R.color.accent);

            // Initialize material sheet FAB
                    noteadd = (TextView)findViewById(R.id.fab_sheet_item_note);
                    noteadd.setOnClickListener(this);
                    materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                    sheetColor, fabColor);

        }
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
        email = settings.getString("email", "no email");
    }

    void initDrawer() {

        PrimaryDrawerItem itemSchedule = new PrimaryDrawerItem().withName(R.string.drawer_schedule).withIcon(R.drawable.ic_schedule).withSelectedIcon(R.drawable.ic_schedule_select);
        PrimaryDrawerItem itemNavigation = new PrimaryDrawerItem().withName(R.string.drawer_navigator).withIcon(R.drawable.ic_navigation).withSelectedIcon(R.drawable.ic_navigation_select);
        PrimaryDrawerItem itemNotes = new PrimaryDrawerItem().withName(R.string.drawer_notes).withIcon(R.drawable.ic_notes).withSelectedIcon(R.drawable.ic_notes_select);
        PrimaryDrawerItem itemNews = new PrimaryDrawerItem().withName(R.string.drawer_news).withIcon(R.drawable.ic_news).withSelectedIcon(R.drawable.ic_news_select);
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

                       Intent i = new Intent(getApplicationContext(),ProfileActivity.class);
                        startActivity(i);

                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
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
                                addfragment(R.string.drawer_settings, new SettingsFragment());
                                break;
                        }
                        return false;
                    }
                })
                .build();
    }

    void addfragment(int title, Fragment fragment) {
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
        if (v==noteadd){
            Intent i = new Intent(getApplicationContext(),AttachActivity.class);
            i.putExtra("TypeAttach","Note");
            startActivity(i);
            materialSheetFab.hideSheet();
        }
    }
}
