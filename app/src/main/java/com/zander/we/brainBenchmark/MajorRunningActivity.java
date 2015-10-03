package com.zander.we.brainBenchmark;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Hashtable;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

//import android.support.annotation.NonNull;



public abstract class MajorRunningActivity extends Activity {

    int CONSTANT_SCORE_STEP = 0;
    int CONSTANT_GIVEN_SECONDS = 0;
    int CONSTANT_ITEMS_COUNT = 0;
    int CONSTANT_ANIMATION_DURATION = 0;

    int mCurScore = 0;
    Timer mTimer;
    ActivityState mState;
    int mLeftSeconds = 0;

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
    abstract protected  void   subclass_set_ValueOfAnItem(int itemIndex, Integer value);

    abstract String subclass_getKeyForSavingHighestScore();

    //get current animators, the object should use mTestTextViews and mTestViews
    abstract protected Animator[] subclass_get_CurrentAnimators();

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

    //To specify test views' positions and other properties, should be overrode
    protected void subclass_initBeforeRun()
    {
        ViewGroup.LayoutParams vlp;
        RelativeLayout rLayout;

        //Adjust view's positions
        for(int i = 0; i < CONSTANT_ITEMS_COUNT; i++)
        {
            rLayout = mTestItems[i];

            vlp = rLayout.getLayoutParams();
            vlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            vlp.height = (int)mTestItemHeight[i];
            ((FrameLayout.LayoutParams)vlp).gravity = Gravity.CENTER_HORIZONTAL;
            rLayout.setLayoutParams(vlp);
            rLayout.setY(mTestItem_Y[i]);
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
        mLeftSeconds = CONSTANT_GIVEN_SECONDS;

        mRandom = new Random(System.currentTimeMillis());

        FrameLayout fL = (FrameLayout)findViewById(R.id.frame_when_running);

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
            toShowAbout();
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

        Animator[] animatorArrays = subclass_get_CurrentAnimators();

        AnimatorSet animatorSet = new AnimatorSet();
        final long duration = (long) CONSTANT_ANIMATION_DURATION;

        //make the two buttons unClickable
        findViewById(R.id.yes_button).setClickable(false);
        findViewById(R.id.no_button).setClickable(false);

        //hide the item at the bottom
        mTestItems[CONSTANT_ITEMS_COUNT-1].setVisibility(View.INVISIBLE);

        Log.d("## debug ##", "animatorArrays.length = " + animatorArrays.length);

        for (Animator a : animatorArrays) {
            if(a != null) {
                a.setDuration(duration);
                animatorSet.play(a);
            }
            else {
                Log.d("## debug","a is null !!!");
            }
        }

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
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Resources res = getResources();
                TextView timeView = (TextView) findViewById(R.id.time_text);
                timeView.setText(res.getString(R.string.time_left) + " " + String.format("%02d : %02d", mLeftSeconds / 60, mLeftSeconds % 60));
                if (mLeftSeconds == 0) {
                    showPopup(PopupCondition.POPUP_CONDITION_TIME_IS_UP);
                }
            }
        });

    }

    private  void onJudge(boolean isYes, View view)
    {
        Boolean isMatched = isTestedPairMatch();

        if( (isMatched && isYes) || (!isMatched && !isYes))
        {
            mCurScore += CONSTANT_SCORE_STEP;
            moveTestItems();
            refreshScore();
        }
        else
        {
            view.setBackgroundResource(R.drawable.buttons_focus2);
            showPopup(PopupCondition.POPUP_CONDITION_WRONG);
        }
    }

    public void onYes(View view)
    {
        onJudge(true,view);
    }

    public void onNo(View view)
    {
        onJudge(false,view);
    }

    private void showPopup(PopupCondition condition)
    {
        //Stop the running
        ChangeState(ActivityAction.ACTIVITY_ACTION_PAUSE);

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        DialogFragment dialog = new popupDialogFragment();
        Bundle args = new Bundle();

        args.putInt(popupDialogFragment.CONSTANT_SHOW_WHAT, condition.getValue());
        dialog.setArguments(args);

        dialog.show(ft, "dialog");
        getFragmentManager().executePendingTransactions();
        if(dialog.getDialog() != null)
        {
            dialog.getDialog().setCanceledOnTouchOutside(false);
        }
        else
        {
            Log.e("### ERROR ###", "getDialog() still returns NULL");
        }

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
        int secs =CONSTANT_GIVEN_SECONDS - mLeftSeconds;

        return String.format( getString(R.string.info_finished),
                mCurScore,
                mCurScore,
                secs,
                (float)mCurScore/secs,
                (float)secs/mCurScore);
    }

    private String getPerformanceComment()
    {
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

        return res.getString(R.string.performance_6);

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
        switch(action)
        {
            case ACTIVITY_ACTION_INIT:
                String instruction = res.getString(mInstructionResId)+"\n\n" + res.getString(R.string.instruction_part_2);
                showPausedUI(res.getString(R.string.str_start), instruction);
                mState = ActivityState.ACTIVITY_STATE_INITIAL;
                break;

            case ACTIVITY_ACTION_RUN:

                subclass_initBeforeRun();
                mCurScore = 0;
                mLeftSeconds = CONSTANT_GIVEN_SECONDS;
                fillTestItemsRandomly();
                hideActionBarItems();

                findViewById(R.id.frame_when_paused).setVisibility(View.INVISIBLE);
                findViewById(R.id.frame_when_running).setVisibility(View.VISIBLE);

                for ( int btnId : new int[]{R.id.yes_button, R.id.no_button}) {
                    findViewById(btnId).setClickable(true);
                    findViewById(btnId).setBackgroundResource(R.drawable.button_type_2);
                }

                mState = ActivityState.ACTIVITY_STATE_RUNNING;

                TextView arrow_view;

                //adjust right arrow > and < 's position
                for(int arrowId : new int[]{R.id.right_arrow, R.id.left_arrow})
                {
                    arrow_view = (TextView) findViewById(arrowId);
                    arrow_view.setY(mTestItem_Y[CONSTANT_ITEMS_COUNT - 1]);
                    arrow_view.setTextSize(TypedValue.COMPLEX_UNIT_PX, res.getDimension(R.dimen.arrow_text_size));
                    arrow_view.setHeight((int)mTestItemHeight[CONSTANT_ITEMS_COUNT-1]);
                    arrow_view.setGravity(Gravity.CENTER_VERTICAL);
                    //arrow_view.setBackgroundColor(Color.BLACK);
                }

                refreshScore();
                if ( null == mTimer)
                {
                    mTimer = new Timer(true);
                }

                mTimer.schedule(new TimerTask(){
                    @Override
                    public void run(){
                        mLeftSeconds -=1;
                        refreshTime();
                    }
                },0,1000);


                break;

        case ACTIVITY_ACTION_PAUSE:
            //showPausedUI(res.getString(R.string.str_restart),res.getString(R.string.color_judgement_instruction));
            if(mTimer != null) {
                mTimer.cancel();
                mTimer.purge();
                mTimer = null;
            }
            mState = ActivityState.ACTIVITY_STATE_PAUSED;
            break;

        case ACTIVITY_ACTION_TERMINATE:

                String finishedInfo = getPerformanceResult() + "\n"+getPerformanceComment();

                saveHighestScore(mCurScore);

                showPausedUI(res.getString(R.string.str_restart),finishedInfo);
                if(mTimer != null) {
                    mTimer.cancel();
                    mTimer.purge();
                    mTimer = null;
                }
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
                ChangeState(ActivityAction.ACTIVITY_ACTION_TERMINATE);
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

    public void toShowAbout()
    {
        showPopup(PopupCondition.POPUP_CONDITION_ABOUT);
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

