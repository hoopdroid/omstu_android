package savindev.myuniversity.welcomescreen;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import savindev.myuniversity.MainActivity;
import savindev.myuniversity.R;
import savindev.myuniversity.db.DBHelper;
import savindev.myuniversity.serverTasks.GetInitializationInfoTask;

public class FirstStartActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar toolbar;
    private Button btnSignin;
    private Button btnSkip;
    AuthorizationFragment authorizationFragment;
    LinearLayout buttons;
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!DBHelper.isInitializationInfoThere(this)) {
            GetInitializationInfoTask giit = new GetInitializationInfoTask(getApplicationContext(), null);
            giit.execute();
        }

        setContentView(R.layout.activity_first_start);
        buttons = (LinearLayout)findViewById(R.id.buttonsLayout);
        icon = (ImageView)findViewById(R.id.icon);
        buttons.animate();

        btnSignin = (Button)findViewById(R.id.btnSignin);
        btnSkip = (Button)findViewById(R.id.btnSkip);

        btnSignin.setOnClickListener(this);
        btnSkip.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {
        if(v==btnSignin){

            SlideToDown();
            ResizeIcon();
            authorizationFragment = new AuthorizationFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_in_right);
            transaction.replace(R.id.login_fragment, authorizationFragment);
            transaction.addToBackStack(null);
            transaction.commit();}

        if(v==btnSkip){

            SharedPreferences settings = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            settings.edit().putBoolean("isFirstStart", false);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isFirstStart", false);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }


    public void ResizeIcon() {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(icon,
                PropertyValuesHolder.ofFloat("scaleX", 0.5f),
                PropertyValuesHolder.ofFloat("scaleY", 0.5f));
        scaleDown.setDuration(500);
        scaleDown.start();
    }

    public void SlideToDown() {

        Animation slide = null;
        slide = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 5.2f);

        slide.setDuration(500);
        slide.setFillAfter(true);
        slide.setFillEnabled(true);
        buttons.startAnimation(slide);

        slide.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                buttons.setVisibility(View.GONE);

            }

        });
    }}