package com.zander.we.brainBenchmark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;

import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;


import android.view.animation.DecelerateInterpolator;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public abstract class MajorRunningActivity extends Activity {

    int CONSTANT_SCORE_STEP = 0;
    int CONSTANT_GIVEN_SECONDS = 0;
    int CONSTANT_ITEMS_COUNT = 0;
    int CONSTANT_ANIMATION_DURATION = 0;

    int mCurScore = 0;
    Timer mTimer;
    ActivityState mState;
    int mDisplaySeconds = 0;
    int mTotalJudgeTimes = 0;

    float mScaledDensity = 0;
    float mDensity = 0;

    RelativeLayout[] mTestItems ;
    TextView[] mTestTxtViews ;
    ImageView[] mTestImgViews;

    float[] mTestItem_Y;

    float[] mTestItemHeight;

    Hashtable<Integer, String> mTestPairs =null;

    int mInstructionResId = 0;
    Random mRandom;
    boolean mMenuIsHide = false;

    SubActivity mActualActivity = SubActivity.SUB_ACTIVITY_UNASSIGNED;

    private GameMode mGameMode = GameMode.GAME_MODE_NO_MISTAKES;

    //Generate a resource Id programmatically, such imgview0_y,...
    protected int getResIdWithPattern( String type, String beforeNum, int num, String afterNum)
    {
        return getResources().getIdentifier(beforeNum + num + afterNum, type, getPackageName());
    }
    //abstract interfaces


    //To specify test views and ...
    abstract protected void subclass_initOnCreate();
    abstract protected void subclass_set_mTestPairs();

    abstract protected String   subclass_get_TextOfAnItem(int itemIndex);
    abstract protected Integer subclass_get_ValueOfAnItem(int itemIndex);

    abstract protected void   subclass_set_TextOfAnItem (int itemIndex,  String text);
    abstract protected void   subclass_set_ValueOfAnItem(int itemIndex, Integer value);

    abstract String subclass_getKeyForSavingHighestScore();

    //get current animators, the object should use mTestTextViews and mTestViews
    abstract protected Collection<Animator> subclass_get_CurrentAnimators();

    public void MyTest(View view)
    {

    }

    //This method should be overrode
    protected void  subclass_restore_TopItem()
    {
        RelativeLayout topItem = mTestItems[0];
        topItem.setY(mTestItem_Y[0]);

        ViewGroup.LayoutParams vlp = topItem.getLayoutParams();
        vlp.height = (int)mTestItemHeight[0];
        topItem.setLayoutParams(vlp);

        topItem.setVisibility(View.VISIBLE);
    }

    public void popupErrorOrRightText(Boolean isCorrect)
    {

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final RelativeLayout parent = (RelativeLayout)findViewById(R.id.frame_when_running);

        final View fl =  inflater.inflate(R.layout.error_or_right_info_layout, null);


        parent.addView(fl);

        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)fl.getLayoutParams();


        //lp.setMargins((int)(240*mDensity),(int)(256*mDensity),0,0);
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

        fl.setLayoutParams(lp);

        fl.setY(180 * mDensity);

        TextView tv = (TextView)fl.findViewById(R.id.error_or_right_text);
        if(isCorrect)
        {
            fl.setX(24 * mDensity);
            tv.setTextColor(getResources().getColor(R.color.right_text));
            tv.setText(getString(R.string.str_right));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP,32);
        }
        else
        {
            fl.setX(240 * mDensity);
            tv.setTextColor(getResources().getColor(R.color.error_text));
            tv.setText(getString( R.string.str_error));
        }
        fl.setVisibility(View.VISIBLE);
        ViewPropertyAnimator vpAnm = fl.animate();


        vpAnm.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                fl.setVisibility(View.INVISIBLE);
                parent.removeView(fl);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        float endY = 80*mDensity;

        vpAnm.setDuration(1500).alpha(0.05f).y(endY).scaleX(1.5f);

    }

    public void onModeChoose(View view)
    {
        int view_id = view.getId();
        ImageView arrowView = (ImageView)findViewById(R.id.imgview_choice_arrow);

        float no_mistake_y = findViewById(R.id.txtview_no_mistakes).getY();
        float fixed_duration_y = findViewById(R.id.minute_mode_layout).getY();


        ViewPropertyAnimator vpAnm =  arrowView.animate();
        vpAnm.setDuration(300);


        if(view_id == R.id.txtview_no_mistakes ) {
            vpAnm.y(no_mistake_y);
            mGameMode = GameMode.GAME_MODE_NO_MISTAKES;
        }
        else if( view_id == R.id.spinner_minute
                    || view_id == R.id.txtview_fixed_duration )

        {
            vpAnm.y(fixed_duration_y);
            mGameMode = GameMode.GAME_MODE_IN_MINUTES;
        }

    }

    //To specify test views' positions and other properties, should be overrode
    protected void subclass_initBeforeRun()
    {
        RelativeLayout.LayoutParams rlp;
        RelativeLayout rLayout;

        //Adjust view's positions
        for(int i = 0; i < CONSTANT_ITEMS_COUNT; i++)
        {
            rLayout = mTestItems[i];

            rlp = (RelativeLayout.LayoutParams)rLayout.getLayoutParams();
            rlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            rlp.height = (int)mTestItemHeight[i];
            //rlp.gravity = Gravity.CENTER_HORIZONTAL;
            rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);

            rLayout.setLayoutParams(rlp);
            rLayout.setY(mTestItem_Y[i]);
            rLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            //rLayout.setBackgroundColor(Color.WHITE);
        }
    }

    //This Method can be overrode
    protected void subclass_setTestItemPositionAndLayoutValues()
    {
        Resources res = getResources();

        for(int i = 0 ; i < CONSTANT_ITEMS_COUNT; i++) {
            mTestItem_Y[i] = res.getDimension(getResIdWithPattern("dimen","item",i, "_y"));
            mTestItemHeight[i] = res.getDimension(getResIdWithPattern("dimen","item",i, "_height"));
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("## debug ##", "getClass().getSimpleName() = " + getClass().getSimpleName());
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_major_running);

        //UI part
        Spinner spinner = (Spinner) findViewById(R.id.spinner_minute);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.minutes_array, R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        final float spinTxtSize = ((TextView)findViewById(R.id.txtview_fixed_duration)).getTextSize();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //((TextView) parentView.getChildAt(0)).setTextColor(Color.WHITE);
                //((TextView) parentView.getChildAt(0)).setTextSize(TypedValue.COMPLEX_UNIT_PX, spinTxtSize);
                CONSTANT_GIVEN_SECONDS = (position+1)*60;

                Log.i("[zander]","position = "+position+",id = "+id);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }

        });

        spinner.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    onModeChoose(v);
                }
                return false;
            }
        });

        BrnBnchMrkHelper.setSystemFontNumber(1, (TextView) findViewById(R.id.highest_score), 28);
        BrnBnchMrkHelper.setSystemFontNumber(1, (TextView) findViewById(R.id.score_text), 24);
        BrnBnchMrkHelper.setSystemFontNumber(1, (TextView) findViewById(R.id.time_text), 24);

        BrnBnchMrkHelper.setSystemFontNumber(2, (TextView) findViewById(R.id.info_when_paused), 24);

        Resources res = getResources();
        CONSTANT_SCORE_STEP = res.getInteger(R.integer.score_step);
        CONSTANT_GIVEN_SECONDS = res.getInteger(R.integer.given_seconds);

        CONSTANT_ITEMS_COUNT = res.getInteger(R.integer.items_count);
        CONSTANT_ANIMATION_DURATION = res.getInteger(R.integer.animation_duration);

        mTestTxtViews = new TextView[CONSTANT_ITEMS_COUNT];
        mTestImgViews = new ImageView[CONSTANT_ITEMS_COUNT];
        mTestItems = new RelativeLayout[CONSTANT_ITEMS_COUNT];

        mTestItem_Y = new float[CONSTANT_ITEMS_COUNT];
        //mTestItemWidth = new float[CONSTANT_ITEMS_COUNT];
        mTestItemHeight = new float[CONSTANT_ITEMS_COUNT];

        mScaledDensity = getResources().getDisplayMetrics().scaledDensity;
        mDensity = getResources().getDisplayMetrics().density;
        Log.d("info", "System ScaledDensity = "+mScaledDensity +", Density = " + mDensity);

        mCurScore = 0;

        mRandom = new Random(System.currentTimeMillis());

        RelativeLayout fL = (RelativeLayout)findViewById(R.id.frame_when_running);

        for(int i=0; i < CONSTANT_ITEMS_COUNT;i++) {
            mTestItems[i] = new RelativeLayout(getApplicationContext());
            fL.addView(mTestItems[i]);

            mTestImgViews[i] = new ImageView(getApplicationContext());
            mTestImgViews[i].setId(getResIdWithPattern("id", "imgview_id_", i, ""));
            mTestItems[i].addView(mTestImgViews[i]);

            mTestTxtViews[i] = new TextView(getApplicationContext());
            mTestItems[i].addView(mTestTxtViews[i]);
        }

        subclass_set_mTestPairs();

        subclass_setTestItemPositionAndLayoutValues();

        subclass_initOnCreate();

        ChangeState(ActivityAction.ACTIVITY_ACTION_INIT);

        //Set Game Mode UI
        mGameMode = getGameMode();
        if(mGameMode == GameMode.GAME_MODE_IN_MINUTES)
        {

            ImageView arrowView = (ImageView)findViewById(R.id.imgview_choice_arrow);
            LinearLayout.LayoutParams lp = ( LinearLayout.LayoutParams)arrowView.getLayoutParams();
            lp.gravity = Gravity.BOTTOM;

            arrowView.setLayoutParams(lp);
        }
      }

    @Override
    protected void onResume(){
        super.onResume();
        if(mState == ActivityState.ACTIVITY_STATE_PAUSED) {
            ChangeState(ActivityAction.ACTIVITY_ACTION_RESUME);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.i("[zander]","onPause is called in MajorRunning Activity");
        saveGameMode(mGameMode);

        if(mState == ActivityState.ACTIVITY_STATE_RUNNING) {
            ChangeState(ActivityAction.ACTIVITY_ACTION_PAUSE);
        }
    }

    @Override
       public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.major_running, menu);

        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(!mMenuIsHide);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            //toShowAbout();
            return false;
        }
        else if (id == R.id.action_share)
        {
            toShare();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private Integer getRandomTestPairValue()
    {
       return (Integer)mTestPairs.keySet().toArray()[mRandom.nextInt(mTestPairs.size())];
     }

    private String getRandomTestPairText()
    {
        return (String)mTestPairs.values().toArray()[mRandom.nextInt(mTestPairs.size())];
    }

    private boolean isTestedPairMatch()
    {
        String text = subclass_get_TextOfAnItem(CONSTANT_ITEMS_COUNT - 1);
        Integer value = subclass_get_ValueOfAnItem(CONSTANT_ITEMS_COUNT - 1);

        return mTestPairs.get(value).equals(text);
    }


    private void fillTestItemsRandomly()
    {
        for( int i = 0; i < CONSTANT_ITEMS_COUNT; i++)
        {
            subclass_set_TextOfAnItem(i, getRandomTestPairText());
            subclass_set_ValueOfAnItem(i, getRandomTestPairValue());
        }
    }

    //move test items
    private void moveTestItems() {

        //make the two buttons unClickable
        findViewById(R.id.yes_button).setClickable(false);
        findViewById(R.id.no_button).setClickable(false);


        Collection<Animator> animatorCollection = subclass_get_CurrentAnimators();

        AnimatorSet animatorSet = new AnimatorSet();
        final long duration = (long) CONSTANT_ANIMATION_DURATION;


        //hide the item at the bottom
        mTestItems[CONSTANT_ITEMS_COUNT-1].setVisibility(View.INVISIBLE);

        Log.d("## debug ##", "animatorCollection.size() = " + animatorCollection.size());

        animatorSet.setInterpolator(new DecelerateInterpolator());
        animatorSet.setDuration(duration);
        animatorSet.playTogether(animatorCollection);

         animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                //make mColorTextViews' item points to new  TextView
                TextView tempTextView = mTestTxtViews[CONSTANT_ITEMS_COUNT-1];
                ImageView tempImgView = mTestImgViews[CONSTANT_ITEMS_COUNT-1];
                RelativeLayout tempItem = mTestItems[CONSTANT_ITEMS_COUNT-1];

                for (int i = CONSTANT_ITEMS_COUNT-1; i >=1 ; i--)
                {
                    mTestTxtViews[i] =mTestTxtViews[i-1];
                    mTestImgViews[i] = mTestImgViews[i-1];
                    mTestItems[i] = mTestItems[i-1];
                }

                mTestTxtViews[0] = tempTextView;
                mTestImgViews[0] = tempImgView;
                mTestItems[0] = tempItem;

                //To increase the matching rate, you need to do some intervention
                if(mRandom.nextInt(30) % 4 == 0)
                {
                    Integer value = getRandomTestPairValue();
                    subclass_set_ValueOfAnItem(0, value);
                    subclass_set_TextOfAnItem(0, mTestPairs.get(value));
                }
                else
                {
                    subclass_set_TextOfAnItem(0, getRandomTestPairText());
                    subclass_set_ValueOfAnItem(0, getRandomTestPairValue());
                }

                //move the one at the bottom to the top, so that it doesn't need to be recreated frequently
                subclass_restore_TopItem();

                //make the two buttons unClickable
                findViewById(R.id.yes_button).setClickable(true);
                findViewById(R.id.no_button).setClickable(true);
            }
        });

        //Move other text views
        animatorSet.start();
    }


    private void refreshScore()
    {
        TextView scoreView = (TextView)findViewById(R.id.score_text);
        scoreView.setText(getResources().getString(R.string.score) + String.valueOf(mCurScore));
    }

    private void refreshTime()
    {
        final Activity curActivity = this;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Resources res = getResources();
                TextView timeView = (TextView) findViewById(R.id.time_text);
                timeView.setText(String.format("%02d : %02d", mDisplaySeconds / 60, mDisplaySeconds % 60));
                if (mDisplaySeconds == 0 && mGameMode == GameMode.GAME_MODE_IN_MINUTES) {
                    //Stop the running
                    ChangeState(ActivityAction.ACTIVITY_ACTION_PAUSE);
                    BrnBnchMrkHelper.showPopup(curActivity, PopupCondition.POPUP_CONDITION_TIME_IS_UP);
                }
            }
        });

    }

    private  void onJudge(boolean isYes, View view)
    {
        Boolean isMatched = isTestedPairMatch();

        mTotalJudgeTimes += 1;

        if( (isMatched && isYes) || (!isMatched && !isYes))
        {
            //when the judgement is right
            mCurScore += CONSTANT_SCORE_STEP;
            moveTestItems();
            refreshScore();
            if(mGameMode == GameMode.GAME_MODE_IN_MINUTES)
            {
                popupErrorOrRightText(true);
            }
        }
        else
        {
            //when the judgement is wrong
            if(mGameMode == GameMode.GAME_MODE_NO_MISTAKES) {
                //view.setBackgroundResource(R.drawable.button2_bg_pressed);

                //Stop the running
                ChangeState(ActivityAction.ACTIVITY_ACTION_PAUSE);

                BrnBnchMrkHelper.showPopup(this, PopupCondition.POPUP_CONDITION_WRONG);
            }
            else
            {
                //Minutes mode
                moveTestItems();

                //do something to report error
                popupErrorOrRightText(false);
            }
        }
    }

    public void onYes(View view)
    {
        onJudge(true, view);
    }

    public void onNo(View view)
    {
        onJudge(false, view);
    }



    public void showPausedUI(String btn_txt, String info_txt)
    {

        ((TextView)findViewById(R.id.info_when_paused)).setText(info_txt);
        String highestRecord = String.format(getString(R.string.str_highest_score), getHighestScore());
        ((TextView)findViewById(R.id.highest_score)).setText(highestRecord);
        ((Button)findViewById(R.id.color_start_button)).setText(btn_txt);

        showActionBarItems();

        findViewById(R.id.frame_when_running).setVisibility(View.INVISIBLE);
        findViewById(R.id.frame_when_paused).setVisibility(View.VISIBLE);

    }

    private void saveGameMode(GameMode mode)
    {
        if(mode == getGameMode())
        {
            return;
        }

        SharedPreferences settings = getApplicationContext().getSharedPreferences(getString(R.string.application_data), 0);
        SharedPreferences.Editor editor = settings.edit();


        editor.putInt(getString(R.string.game_mode), mode.getValue());
        editor.apply();
    }

    private GameMode getGameMode()
    {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(getString(R.string.application_data), 0);
        int value = settings.getInt(getString(R.string.game_mode), GameMode.GAME_MODE_NO_MISTAKES.getValue());
        return GameMode.getEnumByInt(value);
    }

    private void saveHighestScore(int score)
    {
        if( score < getHighestScore() )
        {
            return;
        }

        SharedPreferences settings = getApplicationContext().getSharedPreferences(getString(R.string.application_data), 0);
        SharedPreferences.Editor editor = settings.edit();

        String key = subclass_getKeyForSavingHighestScore();
        if(! key.isEmpty())
        {
            editor.putInt(key, mCurScore);
            editor.apply();
        }
        else {
            Log.e("## ERROR ##", "getKeyForSavingHighestScore() return empty in saveHighestScore()");
        }
    }

    private int getHighestScore()
    {
        SharedPreferences settings = getApplicationContext().getSharedPreferences(getString(R.string.application_data), 0);

        String key = subclass_getKeyForSavingHighestScore();
        if( !key.isEmpty() )
        {
            return settings.getInt(key, 0);
        }

        Log.e("## ERROR ##", "getKeyForSavingHighestScore() return empty in getHighestScore");
        return 0;
    }

    private String getPerformanceResult()
    {
        int secs = 0;

        if( 0 == mTotalJudgeTimes) {
            return getString(R.string.info_do_nothing);
        }

        if (mGameMode == GameMode.GAME_MODE_IN_MINUTES) {
            secs = CONSTANT_GIVEN_SECONDS - mDisplaySeconds;
            return String.format( getString(R.string.info_minutes_mode_finished),
                    mCurScore,
                    (float)secs/mTotalJudgeTimes,
                    mCurScore*100/mTotalJudgeTimes);
        }
        else
        {
            secs = mDisplaySeconds;
            return String.format( getString(R.string.info_no_mistake_mode_finished),
                    mCurScore,
                    secs,
                    (float)secs/mCurScore);
        }


    }

    private String getPerformanceComment()
    {
        /*
        Resources res = getResources();
        int secs =CONSTANT_GIVEN_SECONDS - mLeftSeconds;

        if ( mCurScore >= res.getInteger(R.integer.score_level_1) )
        {
            return res.getString(R.string.performance_1);
        }
        else if ( mCurScore >= res.getInteger(R.integer.score_level_2) )
        {
            return res.getString(R.string.performance_2);
        }
        else if ( mCurScore >= res.getInteger(R.integer.score_level_3) )
        {
            return res.getString(R.string.performance_3);
        }
        else if ( mCurScore >= res.getInteger(R.integer.score_level_4) )
        {
            if ((float)mCurScore/secs >= 1.3f)
            {
                return res.getString(R.string.performance_5) ;
            }
            return res.getString(R.string.performance_4);
        }

        return res.getString(R.string.performance_6);*/
        return "";

    }

    private void showActionBarItems()
    {
        mMenuIsHide = false;
        invalidateOptionsMenu();
    }

    private void hideActionBarItems()
    {
        mMenuIsHide = true;
        invalidateOptionsMenu();
    }

    public void ChangeState(ActivityAction action)
    {
        Resources res = getResources();
        switch(action) {
            case ACTIVITY_ACTION_INIT:
                //String instruction = res.getString(mInstructionResId)+"\n\n" + res.getString(R.string.instruction_part_2);
                String instruction = res.getString(mInstructionResId);
                showPausedUI(res.getString(R.string.str_start), instruction);

                mState = ActivityState.ACTIVITY_STATE_INITIAL;
                break;

            case ACTIVITY_ACTION_RUN:

                subclass_initBeforeRun();
                mCurScore = 0;
                mTotalJudgeTimes = 0;

                if (mGameMode == GameMode.GAME_MODE_IN_MINUTES) {
                    mDisplaySeconds = CONSTANT_GIVEN_SECONDS;
                } else {
                    mDisplaySeconds = 0;
                }

                fillTestItemsRandomly();
                hideActionBarItems();

                findViewById(R.id.frame_when_paused).setVisibility(View.INVISIBLE);
                findViewById(R.id.frame_when_running).setVisibility(View.VISIBLE);

                for (int btnId : new int[]{R.id.yes_button, R.id.no_button}) {
                    findViewById(btnId).setClickable(true);
                }

                mState = ActivityState.ACTIVITY_STATE_RUNNING;

                TextView arrow_view;

                //adjust right arrow > and < 's position
                for (int arrowId : new int[]{R.id.right_arrow, R.id.left_arrow}) {
                    arrow_view = (TextView) findViewById(arrowId);
                    arrow_view.setY(mTestItem_Y[CONSTANT_ITEMS_COUNT - 1]);
                    arrow_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.arrow_text_size));
                    arrow_view.setHeight((int) mTestItemHeight[CONSTANT_ITEMS_COUNT - 1]);
                    arrow_view.setGravity(Gravity.CENTER_VERTICAL);
                    //arrow_view.setBackgroundColor(Color.BLACK);
                }

                refreshScore();
                if (null == mTimer) {
                    mTimer = new Timer(true);
                }

                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if(mState == ActivityState.ACTIVITY_STATE_PAUSED)
                        {
                            return;
                        }

                        if (mGameMode == GameMode.GAME_MODE_IN_MINUTES)
                        {
                            mDisplaySeconds -= 1;
                        } else {
                            mDisplaySeconds += 1;
                        }
                        refreshTime();
                    }
                }, 0, 1000);


                break;

            case ACTIVITY_ACTION_PAUSE:
                mState = ActivityState.ACTIVITY_STATE_PAUSED;
                break;

            case ACTIVITY_ACTION_RESUME:
                mState = ActivityState.ACTIVITY_STATE_RUNNING;
                break;

            case ACTIVITY_ACTION_TERMINATE:

                if(mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;
                }

                String finishedInfo = getPerformanceResult();// + "\n"+getPerformanceComment();

                saveHighestScore(mCurScore);

                showPausedUI(res.getString(R.string.str_restart),finishedInfo);

                mState = ActivityState.ACTIVITY_STATE_FINISHED;
                break;
            default:
                break;
        }
    }

    public void onRun(View view)
    {
        ChangeState(ActivityAction.ACTIVITY_ACTION_RUN);
    }


    public boolean onKeyDown(int keyCode,/*@NonNull*/ KeyEvent event) {
        //only if it's in running state, make it to return stopped state
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mState == ActivityState.ACTIVITY_STATE_RUNNING) {
                ChangeState(ActivityAction.ACTIVITY_ACTION_PAUSE);
                BrnBnchMrkHelper.showPopup(this, PopupCondition.POPUP_CONDITION_IF_EXIT);
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /***
     * convert layout to bitmap
     * @param view: flameLayout/linearLayout...
     * @param width: bitmap's height
     * @param height : bitmap's width
     * @return bitmap
     */
    private static Bitmap viewToBitmap(View view, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }



    private Uri getPNGUri(Bitmap bitmapImage){
        String fileName = getString(R.string.share_image_file);

        FileOutputStream outputStream;

        try {

            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            bitmapImage.compress(Bitmap.CompressFormat.PNG,80,outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File pngFile = new File(getFilesDir(), fileName);
        String urlStr ="";
        try {
            urlStr = MediaStore.Images.Media.insertImage(getContentResolver(), pngFile.getAbsolutePath(), "myscore", null);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.parse(urlStr);
        /*
        File pngFile = new File(getFilesDir(), fileName);
        return FileProvider.getUriForFile(getApplicationContext(),getString(R.string.provider_authority), pngFile);
        */
        //return "content://"+getString(R.string.provider_authority)+"/"+ fileName;
    }

    public void toShare()
    {
        //update the frame_for_share
        String text;
        if (mState == ActivityState.ACTIVITY_STATE_INITIAL)
        {
            text = getString(R.string.info_share_1);
        }
        else
        {
            /*text =  String.format(getString(R.string.info_share_2),
                    mCurScore,
                    getTitle().toString());*/
            text = getTitle().toString() + "\n" +((TextView)findViewById(R.id.info_when_paused)).getText().toString();
        }

        TextView shareInfoView = (TextView)findViewById(R.id.share_info_text);
        shareInfoView.setText(text);

        //Generate the image
        View viewToShare = findViewById(R.id.frame_for_share);

        viewToShare.invalidate();

        Bitmap b = MajorRunningActivity.viewToBitmap(viewToShare, viewToShare.getWidth(),viewToShare.getHeight());
        if( b == null) {
            Log.d("info", "bitmap is null!");
            return;
        }

        //String url= MediaStore.Images.Media.insertImage(getContentResolver(),b,"title1",null);

        Intent shareIntent = new Intent();
        //String pngUri = getPNGUri(b);
        Uri pngUri = getPNGUri(b);
        Log.d("## BrainBenchMark ##", "pngUri =" + pngUri.toString());
        shareIntent.setAction(Intent.ACTION_SEND);

        if( pngUri !=null && !pngUri.toString().isEmpty())
        {
            shareIntent.putExtra(Intent.EXTRA_STREAM, pngUri);
            shareIntent.setType("image/png");
        }
        else
        {
            shareIntent.putExtra(Intent.EXTRA_TEXT,  text);
            shareIntent.setType("text/plain");
        }

        shareIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Verify that the intent will resolve to an activity
       if (shareIntent.resolveActivity(getPackageManager()) != null) {
         startActivity(Intent.createChooser(shareIntent, "Share scores to.."));
        }

    }


}

