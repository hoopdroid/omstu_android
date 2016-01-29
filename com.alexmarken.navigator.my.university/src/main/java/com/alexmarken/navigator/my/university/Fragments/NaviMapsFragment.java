package com.alexmarken.navigator.my.university.Fragments;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexmarken.navigator.my.university.NavigatorLibrary;
import com.alexmarken.navigator.my.university.engine.Navigator;
import com.alexmarken.navigator.my.university.engine.navigator.naviAuditor;
import com.alexmarken.navigator.my.university.engine.navigator.naviCorp;
import com.alexmarken.navigator.my.university.util.DrawView;
import com.alexmarken.navigator.my.university.R;

public class NaviMapsFragment extends Fragment
        implements View.OnTouchListener, DrawView.onMapsEvents
{
  private static final String FRAGMENT_TITLE = "Карта";
  private static int loadCorp = -1;
  private static int loadStage = -1;
  private static String loadAud = "";

  private String AuditoryDown = "";
  public View rootView;
  private AppCompatActivity mainActivity;
  private NumberPicker npStage;
  private ImageButton ibZoomPlus;
  private ImageButton ibZoomMinus;
  private ScrollView scrollviewinfo;
  private Button btnNaviDialogFrom;
  private Button btnNaviDialogTo;
  private Button btnLocationOk;
  private Button btnLocationCancel;
  private AutoCompleteTextView tvAuditoryA;
  private AutoCompleteTextView tvAuditoryB;
  LinearLayout llfilterAuditory;
  LinearLayout llfilterSteps;
  LinearLayout llfilterWC;
  private DrawView dView = null;

  private boolean panelNaviOpened = false;
  private boolean panelNaviAnimate = false;

  private boolean panelTopOpened = false;
  private boolean panelTopAnimate = false;

  private boolean panelOpened = true;
  private boolean panelAnimate = false;

  private boolean showSteps = false;
  private boolean showAuditory = true;
  private boolean showWC = false;

  public static NaviMapsFragment newInstance() {
    return new NaviMapsFragment();
  }

  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
  {
    this.rootView = inflater.inflate(R.layout.fragment_navigator_maps, container, false);

    this.mainActivity = NavigatorLibrary.mainActivity;

    this.scrollviewinfo = ((ScrollView)this.rootView.findViewById(R.id.scrollviewinfo));

    this.npStage = ((NumberPicker)this.rootView.findViewById(R.id.npStage));
    this.npStage.setMaxValue(4);
    this.npStage.setMinValue(0);
    this.npStage.setWrapSelectorWheel(false);

    EditText numberPickerChild = (EditText)this.npStage.getChildAt(0);
    numberPickerChild.setFocusable(false);
    numberPickerChild.setInputType(0);

    this.ibZoomPlus = ((ImageButton)this.rootView.findViewById(R.id.ibZoomPlus));
    this.ibZoomPlus.setOnTouchListener(this);

    this.ibZoomMinus = ((ImageButton)this.rootView.findViewById(R.id.ibZoomMinus));
    this.ibZoomMinus.setOnTouchListener(this);

    if (this.dView != null) {
      this.dView = null;
    }

    RelativeLayout mapslayout = (RelativeLayout)this.rootView.findViewById(R.id.mapslayout);
    this.dView = new DrawView(this.rootView.getContext(), mapslayout, this.mainActivity.getPackageName());
    this.dView.isCampus = true;
    DrawView.onMapsEvent = this;

    mapslayout.addView(this.dView);

    this.tvAuditoryA = ((AutoCompleteTextView)this.rootView.findViewById(R.id.tvAuditoryA));
    this.tvAuditoryB = ((AutoCompleteTextView)this.rootView.findViewById(R.id.tvAuditoryB));

    this.btnLocationOk = ((Button)this.rootView.findViewById(R.id.btnLocationOk));
    this.btnLocationCancel = ((Button)this.rootView.findViewById(R.id.btnLocationCancel));

    this.btnNaviDialogFrom = ((Button)this.rootView.findViewById(R.id.btnNaviDialogFrom));
    this.btnNaviDialogFrom.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v) {
        RelativeLayout lrNavigatePanel = (RelativeLayout)NaviMapsFragment.this.rootView.findViewById(R.id.lrNavigatePanel);

        if (!NaviMapsFragment.this.panelNaviOpened) {
          NaviMapsFragment.this.startAniNaviPanel(lrNavigatePanel, 500L);
        }
        NaviMapsFragment.this.tvAuditoryA.setText(NaviMapsFragment.this.AuditoryDown);
        NaviMapsFragment.this.emulationOnTouch(NaviMapsFragment.this.tvAuditoryB);
      }
    });

    this.btnNaviDialogTo = ((Button)this.rootView.findViewById(R.id.btnNaviDialogTo));
    this.btnNaviDialogTo.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v) {
        RelativeLayout lrNavigatePanel = (RelativeLayout)NaviMapsFragment.this.rootView.findViewById(R.id.lrNavigatePanel);

        if (!NaviMapsFragment.this.panelNaviOpened) {
          NaviMapsFragment.this.startAniNaviPanel(lrNavigatePanel, 500L);
        }
        NaviMapsFragment.this.tvAuditoryB.setText(NaviMapsFragment.this.AuditoryDown);
        NaviMapsFragment.this.emulationOnTouch(NaviMapsFragment.this.tvAuditoryA);
      }
    });
    RelativeLayout rlBottom = (RelativeLayout)this.rootView.findViewById(R.id.rlBottom);
    rlBottom.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == 1) {
          NaviMapsFragment.this.startAniDescrPanel(v, 500L);
        }
        return true;
      }
    });
    Button btnFilter = (Button)this.rootView.findViewById(R.id.btnFIlter);
    btnFilter.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View v) {
        LinearLayout llTop = (LinearLayout)NaviMapsFragment.this.rootView.findViewById(R.id.llTop);
        NaviMapsFragment.this.startAniTopPanel(llTop, 500L);
      }
    });
    this.dView.loadImage(loadCorp, loadStage);

    if (loadCorp != -1) {
      this.dView.isSearch = true;
      this.dView.activeAuditory = loadAud;

      this.dView.loadImage(loadCorp, loadStage);

      this.dView.isAuditor = false;
      this.dView.isCampus = false;
    }

    final int activeColor = Color.parseColor("#66BB6A");
    final int disactiveColor = Color.parseColor("#FFFFFF");

    this.llfilterAuditory = ((LinearLayout)this.rootView.findViewById(R.id.llfilterAuditory));
    this.llfilterAuditory.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View v, MotionEvent event) {
        TextView tvfilterAuditory = (TextView)NaviMapsFragment.this.rootView.findViewById(R.id.tvfilterAuditory);

        if (event.getAction() == 1) {
          NaviMapsFragment.this.dView.setFilterAuditory(!NaviMapsFragment.this.dView.isAuditor);
        } else if (event.getAction() == 6661024) {
          NaviMapsFragment.this.showAuditory = NaviMapsFragment.this.dView.isAuditor;

          int iColor = NaviMapsFragment.this.showAuditory ? activeColor : disactiveColor;

          tvfilterAuditory.setTextColor(iColor);
        } else if (event.getAction() == 0) {
          tvfilterAuditory.setTextColor(Color.parseColor("#4CAF50"));
        }

        return true;
      }
    });
    this.llfilterSteps = ((LinearLayout)this.rootView.findViewById(R.id.llfilterSteps));
    this.llfilterSteps.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View v, MotionEvent event) {
        TextView tvfilterSteps = (TextView)NaviMapsFragment.this.rootView.findViewById(R.id.tvfilterSteps);

        if (event.getAction() == 1) {
          NaviMapsFragment.this.dView.setFilterSteps(!NaviMapsFragment.this.dView.isSteps);
        } else if (event.getAction() == 6661024) {
          NaviMapsFragment.this.showSteps = (!NaviMapsFragment.this.showSteps);
          NaviMapsFragment.this.dView.isSteps = NaviMapsFragment.this.showSteps;
          NaviMapsFragment.this.dView.Update();

          int iColor = NaviMapsFragment.this.showSteps ? activeColor : disactiveColor;

          tvfilterSteps.setTextColor(iColor);
        } else if (event.getAction() == 0) {
          tvfilterSteps.setTextColor(Color.parseColor("#4CAF50"));
        }

        return true;
      }
    });
    this.llfilterWC = ((LinearLayout)this.rootView.findViewById(R.id.llfilterWC));
    this.llfilterWC.setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View v, MotionEvent event) {
        TextView tvfilterWC = (TextView)NaviMapsFragment.this.rootView.findViewById(R.id.tvfilterWC);

        if (event.getAction() == 1) {
          NaviMapsFragment.this.dView.setFilterWC(!NaviMapsFragment.this.dView.isWC);
        }
        if (event.getAction() == 6661024) {
          NaviMapsFragment.this.showWC = (!NaviMapsFragment.this.showWC);

          int iColor = NaviMapsFragment.this.showWC ? activeColor : disactiveColor;

          tvfilterWC.setTextColor(iColor);
        } else if (event.getAction() == 0) {
          tvfilterWC.setTextColor(Color.parseColor("#4CAF50"));
        }

        return true;
      }
    });
    return this.rootView;
  }

  public boolean onTouch(View v, MotionEvent event)
  {
    ImageButton btn = (ImageButton)v;

    if (event.getAction() == 1) {
      if (v.getId() == this.ibZoomPlus.getId()) {
        btn.setBackgroundResource(R.drawable.button_zoom_plus);
        this.dView.changeScaleFactor(0.2F);
      }
      else if (v.getId() == this.ibZoomMinus.getId()) {
        btn.setBackgroundResource(R.drawable.button_zoom_minus);
        this.dView.changeScaleFactor(-0.2F);
      }
    }
    else if (event.getAction() == 0) {
      if (v.getId() == this.ibZoomPlus.getId()) {
        btn.setBackgroundResource(R.drawable.button_zoom_plus_down);
      }
      else if (v.getId() == this.ibZoomMinus.getId()) {
        btn.setBackgroundResource(R.drawable.button_zoom_minus_down);
      }
    }

    return false;
  }

  public void onAttach(Activity activity)
  {
    super.onAttach(activity);
    NavigatorLibrary.naviMain.postOnAttach("Карта");
  }

  void startAniDescrPanel(View v, final long duration) {
    int extHeight = (int)this.scrollviewinfo.getY();
    ObjectAnimator objectAnimator = null;

    if (!this.panelOpened) {
      objectAnimator = ObjectAnimator.ofFloat(v, "y", new float[] { v.getY() - v.getHeight() + extHeight });
      this.panelOpened = (!this.panelOpened);
    }
    else {
      objectAnimator = ObjectAnimator.ofFloat(v, "y", new float[] { v.getY() + v.getHeight() - extHeight });
      this.panelOpened = (!this.panelOpened);
    }

    if (!this.panelAnimate) {
      objectAnimator.addListener(new Animator.AnimatorListener()
      {
        public void onAnimationStart(Animator animation) {
          NaviMapsFragment.this.panelAnimate = true;

          Button btnArrow = (Button)NaviMapsFragment.this.rootView.findViewById(R.id.btnArrow);

          int rotation = NaviMapsFragment.this.panelOpened ? 0 : 180;

          btnArrow.animate().rotation(rotation).setDuration(duration).start();
        }

        public void onAnimationEnd(Animator animation)
        {
          NaviMapsFragment.this.panelAnimate = false;
        }

        public void onAnimationCancel(Animator animation)
        {
        }

        public void onAnimationRepeat(Animator animation)
        {
        }
      });
      objectAnimator.setDuration(duration);

      if (!this.panelAnimate)
        objectAnimator.start();
    }
  }

  void startAniTopPanel(final View v, long duration) {
    final float value = this.panelTopOpened ? 0.0F : 1.0F;

    if (!this.panelTopAnimate)
      v.animate().alpha(value).setDuration(duration).setListener(new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
          panelTopAnimate = true;

          if (value == 1.0F)
            v.setVisibility(View.VISIBLE);
        }

        public void onAnimationEnd(Animator animation) {
          panelTopOpened = (!NaviMapsFragment.this.panelTopOpened);
          panelTopAnimate = false;

          if (value == 0.0F)
            v.setVisibility(View.INVISIBLE);
        }

        public void onAnimationCancel(Animator animation) {
          panelTopAnimate = false;
        }

        public void onAnimationRepeat(Animator animation) {
        }
      }).start();
  }

  void startAniNaviPanel(final View v, long duration)
  {
    final float value = this.panelNaviOpened ? 0.0F : 1.0F;

    if (!this.panelNaviAnimate)
      v.animate().alpha(value).setDuration(duration).setListener(new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
          panelNaviAnimate = true;

          if (value == 1.0F)
            v.setVisibility(View.VISIBLE);
        }

        public void onAnimationEnd(Animator animation) {
          panelNaviOpened = (!panelNaviOpened);
          panelNaviAnimate = false;

          if (value == 0.0F)
            v.setVisibility(View.INVISIBLE);
        }

        public void onAnimationCancel(Animator animation) {
          panelNaviAnimate = false;
        }

        public void onAnimationRepeat(Animator animation) {
        }
      }).start();
  }

  public void onChangeFiter(int typeFilter, boolean flag)
  {
    View v = null;

    if (typeFilter == 0)
      v = this.llfilterAuditory;
    else if (typeFilter == 1)
      v = this.llfilterSteps;
    else if (typeFilter == 2) {
      v = this.llfilterWC;
    }
    if (v != null)
      emulationOnTouch(v);
  }

  public void onChangeMap(boolean isCampus, final int corp, final int stage)
  {
    dView.isCampus = isCampus;
    dView.isAuditor = (!dView.isCampus);

    RelativeLayout viewlist = (RelativeLayout)this.rootView.findViewById(R.id.rlStagelist);
    RelativeLayout rlBottom = (RelativeLayout)this.rootView.findViewById(R.id.rlBottom);

    if (isCampus) {
      viewlist.setVisibility(View.INVISIBLE);
      rlBottom.setVisibility(View.INVISIBLE);
      this.btnNaviDialogFrom.setVisibility(View.INVISIBLE);
      this.btnNaviDialogTo.setVisibility(View.INVISIBLE);
      this.dView.steps = null;
    }
    else {
      viewlist.setVisibility(View.VISIBLE);
      rlBottom.setVisibility(View.VISIBLE);

      Navigator navi = NavigatorLibrary.engine.getUniversityById("omgups").getNavigator();
      this.dView.steps = navi.getGraps().getSteps(navi.getCampus().getMap(corp, stage).name);
      this.dView.Update();

      btnLocationCancel.setOnClickListener(new OnClickListener() {
        public void onClick(View paramAnonymousView) {
          RelativeLayout localRelativeLayout = (RelativeLayout) NaviMapsFragment.this.rootView.findViewById(R.id.lrNavigatePanel);
          startAniNaviPanel(localRelativeLayout, 500L);
        }
      });

      btnLocationOk.setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {
          String str1 = tvAuditoryA.getText().toString();
          String str2 = tvAuditoryB.getText().toString();

          if ((str1.length() > 0) && (str2.length() > 0)) {
            Navigator localNavigator = NavigatorLibrary.engine.getUniversityById("omgups").getNavigator();

            int i = localNavigator.getCampus().getCorpus(corp).searchAuditory(str1);
            int j = localNavigator.getCampus().getCorpus(corp).searchAuditory(str2);

            if ((i != -1) && (j != -1)) {
              int k = ((naviAuditor) localNavigator.getCampus().getCorpus(corp).getAuditors().get(i)).getStage();
              int m = ((naviAuditor) localNavigator.getCampus().getCorpus(corp).getAuditors().get(j)).getStage();

              String str3 = localNavigator.getCampus().getMap(corp, k).name;
              String str4 = localNavigator.getCampus().getMap(corp, m).name;

              int n = localNavigator.getGraps().getIdOnAuditory(str3, str1);
              int i1 = localNavigator.getGraps().getIdOnAuditory(str4, str2);

              if ((n != -1) && (i1 != -1)) {
                NaviMapsFragment.this.dView.points = localNavigator.getGraps().StartScan(n, i1, -1, n, ":" + String.valueOf(n) + ":");
                NaviMapsFragment.this.dView.Update();
                if (NaviMapsFragment.this.dView.points != null) {
                  Toast.makeText(NaviMapsFragment.this.rootView.getContext(), "Маршрут проложен", Toast.LENGTH_SHORT).show();
                  dView.navi_descr = localNavigator.getGraps().analise(NaviMapsFragment.this.dView.points);
                  onClickLine(corp, stage, dView.navi_descr);
                } else {
                  String msg = "Начальная";

                  if (i != -1)
                    msg = "Конечная";
                  Toast.makeText(rootView.getContext(), msg + " аудитория не найдена!", Toast.LENGTH_SHORT).show();
                }
              }
            } else
              Toast.makeText(rootView.getContext(), "Введите начальную и конечную аудитории!", Toast.LENGTH_SHORT).show();
          } else
            Toast.makeText(rootView.getContext(), "Введите начальную и конечную аудитории!", Toast.LENGTH_SHORT).show();
        }
      });


      final TextView title = (TextView)rlBottom.findViewById(R.id.textViewMapDescr);
      title.setText("Корпус: " + corp + "\nЭтаж: " + stage);

      this.npStage.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
          NaviMapsFragment.this.btnNaviDialogFrom.setVisibility(View.INVISIBLE);
          NaviMapsFragment.this.btnNaviDialogTo.setVisibility(View.INVISIBLE);
          title.setText("Корпус: " + corp + "\nЭтаж: " + newVal);
          NaviMapsFragment.this.dView.loadImage(corp, newVal);
        }
      });
      naviCorp corpus = NavigatorLibrary.engine.getUniversityById("omgups").getNavigator().getCampus().getCorpus(corp);
      int max_stage = corpus.getStages();

      this.npStage.setWrapSelectorWheel(false);

      if (corpus.isGround()) {
        max_stage--;
        this.npStage.setMinValue(0);
      } else {
        this.npStage.setMinValue(1);
      }
      this.npStage.setWrapSelectorWheel(false);

      this.npStage.setMaxValue(max_stage);
      this.npStage.setValue(stage);
    }
  }

  public void onClickAuditor(int corp, int stage, naviAuditor data)
  {
    RelativeLayout rlBottom = (RelativeLayout)this.rootView.findViewById(R.id.rlBottom);

    TextView title = (TextView)rlBottom.findViewById(R.id.textViewMapDescr);
    title.setText("Аудитория: " + corp + "-" + data.getAuditor() + "\n" + data.getName());

    this.btnNaviDialogFrom.setVisibility(View.VISIBLE);
    this.btnNaviDialogTo.setVisibility(View.VISIBLE);

    this.AuditoryDown = data.getAuditor();

    if (!this.panelOpened)
      startAniDescrPanel(rlBottom, 1000L);
  }

  public void onClickLine(int corp, int stage, String text)
  {
    this.btnNaviDialogFrom.setVisibility(View.INVISIBLE);
    this.btnNaviDialogTo.setVisibility(View.INVISIBLE);

    RelativeLayout rlBottom = (RelativeLayout)this.rootView.findViewById(R.id.rlBottom);
    TextView title = (TextView)rlBottom.findViewById(R.id.textViewMapDescr);
    title.setText(text);

    if (!this.panelOpened)
      startAniDescrPanel(rlBottom, 500L);
  }

  public static void loadmap(int corp, int stage, String auditory) {
    loadAud = auditory;
    loadCorp = corp;
    loadStage = stage;
  }

  private void emulationOnTouch(View v) {
    long downTime = SystemClock.uptimeMillis();
    long eventTime = SystemClock.uptimeMillis() + 100L;
    float x = 0.0F;
    float y = 0.0F;

    int metaState = 0;
    MotionEvent motionEvent = MotionEvent.obtain(downTime, eventTime, 6661024, x, y, metaState);

    v.dispatchTouchEvent(motionEvent);
  }
}