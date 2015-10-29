package savindev.myuniversity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.OnButtonClickListener;
import com.dexafree.materialList.card.provider.WelcomeCardProvider;
import com.dexafree.materialList.view.MaterialListView;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    String username;
    String email;

   static PrimaryDrawerItem itemSchedule ;
   static PrimaryDrawerItem itemNavigation;
   static PrimaryDrawerItem itemNotes ;
   static PrimaryDrawerItem itemEducation ;
   static PrimaryDrawerItem itemNews ;
   static SecondaryDrawerItem itemSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getUserSettings();
        initDrawer();


        final MaterialListView mListView = (MaterialListView) findViewById(R.id.material_listview);


         Card welcome_card = new Card.Builder(this)
                .setTag("WELCOME_CARD")
                .setDismissible()
                .withProvider(WelcomeCardProvider.class)
                .setTitle("Добро пожаловать в систему!")
                .setTitleColor(Color.WHITE)
                .setDescription("Проведите пальцем справа-налево")
                .setDescriptionColor(Color.WHITE)
                .setSubtitle("Здравствуйте!")
                .setSubtitleColor(Color.WHITE)
                .setBackgroundColor(getResources().getColor(R.color.accent))
                .setButtonText("Okay!")
                .setOnButtonPressedListener(new OnButtonClickListener() {
                    @Override
                    public void onButtonClicked(final View view, final Card card) {
                        Toast.makeText(getApplicationContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                        mListView.animate();
                        mListView.clearAll();
                    }
                })
                .endConfig()
                .build();


        mListView.add(welcome_card);

    }


   private void getUserSettings(){
       SharedPreferences settings = getSharedPreferences("UserInfo", 0);
       email = settings.getString("Email","no email");
    }

    void initDrawer(){

         itemSchedule = new PrimaryDrawerItem().withName(R.string.drawer_schedule).withIcon(R.drawable.ic_schedule).withSelectedIcon(R.drawable.ic_schedule_select);
         itemNavigation = new PrimaryDrawerItem().withName(R.string.drawer_navigator).withIcon(R.drawable.ic_navigation).withSelectedIcon(R.drawable.ic_navigation_select);
         itemNotes = new PrimaryDrawerItem().withName(R.string.drawer_notes).withIcon(R.drawable.ic_notes).withSelectedIcon(R.drawable.ic_notes_select);
         itemNews = new PrimaryDrawerItem().withName(R.string.drawer_news).withIcon(R.drawable.ic_news).withBadge("12").withSelectedIcon(R.drawable.ic_news_select);
         itemEducation = new PrimaryDrawerItem().withName(R.string.drawer_education).withIcon(R.drawable.ic_school).withSelectedIcon(R.drawable.ic_school_select);
         itemSettings = new SecondaryDrawerItem().withName(R.string.drawer_settings).withIcon(R.drawable.ic_settings).withSelectedIcon(R.drawable.ic_settings_select);

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.background)
                .addProfiles(
                        new ProfileDrawerItem().withName(username).withEmail(email).withIcon(getResources().getDrawable(R.drawable.ic_account_circle_white_48dp))
                )
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        return false;
                    }
                })
                .build();
//create the drawer and remember the `Drawer` result object
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
                                Toast.makeText(getApplicationContext(),"SCHEDULE", Toast.LENGTH_SHORT).show();
                                break;
                            case 2:
                                Toast.makeText(getApplicationContext(),"NAVIGATION", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(getApplicationContext(),"NOTES", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(getApplicationContext(),"NEWS", Toast.LENGTH_SHORT).show();
                                break;
                            case 5:
                                Toast.makeText(getApplicationContext(),"EDUCATION", Toast.LENGTH_SHORT).show();
                                break;
                            case 6:
                                Toast.makeText(getApplicationContext(),"SETTINGS", Toast.LENGTH_SHORT).show();
                                break;

                        }
                        return false;
                    }
                })
                .build();
    }
}
