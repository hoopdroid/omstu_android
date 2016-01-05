package savindev.myuniversity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.notes.NotesFragment;
import savindev.myuniversity.welcomescreen.FirstStartActivity;

public class PairInfoActivity extends AppCompatActivity implements View.OnClickListener {

    String pairName;
    String pairInfo;
    int scheduleId;
    String date;
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView title,subtitle;
    TextView noteadd;
    MaterialSheetFab materialSheetFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pair_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Bundle b = getIntent().getExtras();
        if(b!=null){
        pairName =   b.getString("pairname");
        pairInfo = b.getString("pairtime");
        scheduleId = b.getInt("scheduleId");
        date = b.getString("date");}


        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        title = (TextView) findViewById(R.id.title);
        subtitle = (TextView) findViewById(R.id.subtitle);

        collapsingToolbarLayout.setTitle("");
        title.setText(pairName);
        subtitle.setText(pairInfo);
        toolbar.setTitle("");
        addfragment(new NotesFragment());
        initSheetFab();

    }

    void addfragment(Fragment fragment) {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction;
        fragmentTransaction = fragmentManager
                .beginTransaction();
        fragmentTransaction.replace(R.id.pairNotesList, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (materialSheetFab.isSheetVisible()) {
            materialSheetFab.hideSheet();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {

        if (v==noteadd){
            Intent i = new Intent(getApplicationContext(),AttachActivity.class);
            i.putExtra("TypeAttach","Note");
            i.putExtra("scheduleId",scheduleId);
            i.putExtra("date",date);
            i.putExtra("time",pairInfo);

            startActivity(i);finish();
            materialSheetFab.hideSheet();
        }
    }



    private void initSheetFab(){
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        final Fab fab = (Fab)findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(R.color.md_white_1000);
        int fabColor = getResources().getColor(R.color.accent);

        // Initialize material sheet FAB
        noteadd = (TextView)findViewById(R.id.fab_sheet_item_note);
        noteadd.setOnClickListener(this);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {

            public void onSheetHidden() {
                // Called when the material sheet's "hide" animation ends.
                fab.show();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    fab.hide();
                    isShow = true;
                } else if(isShow) {
                    fab.show();
                    isShow = false;
                }
            }
        });
    }
}
