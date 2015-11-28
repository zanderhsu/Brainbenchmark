package com.zander.we.brainbenchmark;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;


public class ColorActivity extends MajorRunningActivity {

    float[] mTxtSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected String   subclass_get_TextOfAnItem(int itemIndex)
    {
        return mTestTxtViews[itemIndex].getText().toString();
    }

    protected Integer subclass_get_ValueOfAnItem(int itemIndex)
    {
        return mTestTxtViews[itemIndex].getCurrentTextColor();
    }

    protected void   subclass_set_TextOfAnItem (int itemIndex,  String text)
    {
        //adding \u2060 is just a workaround for a well-known bug on TextView of 4.0.3,
        mTestTxtViews[itemIndex].setText(text+"\u2060");
    }

    protected void   subclass_set_ValueOfAnItem(int itemIndex, Integer value)
    {
        mTestTxtViews[itemIndex].setTextColor(value);
    }

    //To set test views and textViews size and position and etc
    protected void subclass_initOnCreate()
    {
        Resources res = getResources();

        mActualActivity = SubActivity.SUB_ACTIVITY_COLOR;

        mTxtSize = new float[CONSTANT_ITEMS_COUNT];
        //the unit for textSize will be sp, but getDimension()'s unit is px, so it needs to converted to sp from px
        for(int i = 0; i < CONSTANT_ITEMS_COUNT ; i++)
        {
            mTxtSize[i] = res.getDimension(getResIdWithPattern("dimen","txtview", i, "_textsize")) / mScaledDensity;
        }

        mInstructionResId = R.string.color_judgement_instruction;
    }

    protected void subclass_set_mTestPairs()
    {
        Resources res = getResources();

        mTestPairs = new Hashtable<Integer, String>();

        mTestPairs.put( Color.BLACK,    res.getString(R.string.color_text_black));
        mTestPairs.put( Color.BLUE ,    res.getString(R.string.color_text_blue));
        mTestPairs.put( Color.GREEN,  res.getString(R.string.color_text_green));
        mTestPairs.put( Color.RED,    res.getString(R.string.color_text_red));
      //  mTestPairs.put( Color.MAGENTA,    res.getString(R.string.color_text_purple));

        mTestPairs.put( res.getColor(R.color.purple_1), res.getString(R.string.color_text_purple));
        mTestPairs.put( res.getColor(R.color.purple_2), res.getString(R.string.color_text_purple));
    //    mTestPairs.put( res.getColor(R.color.blue_1), res.getString(R.string.color_text_blue));
        mTestPairs.put( res.getColor(R.color.red_1), res.getString(R.string.color_text_red));
        mTestPairs.put( res.getColor(R.color.green_1), res.getString(R.string.color_text_green));

    }

    //To set test views and textViews' positions
    protected void subclass_initBeforeRun()
    {
        super.subclass_initBeforeRun();

        ViewGroup.LayoutParams vlp;

        for(int i = 0; i < CONSTANT_ITEMS_COUNT; i++)
        {
            mTestItems[i].removeView(mTestImgViews[i]);
            mTestImgViews[i] = null;

            TextView txtView = mTestTxtViews[i];

            vlp = txtView.getLayoutParams();
            vlp.height  = ViewGroup.LayoutParams.MATCH_PARENT;
            vlp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            txtView.setLayoutParams(vlp);
            txtView.setGravity(Gravity.CENTER);
            txtView.setTextSize(mTxtSize[i]);
            txtView.setTypeface(null, Typeface.BOLD);
            txtView.setShadowLayer(8f, 4f, 4f, Color.DKGRAY);
            //txtView.setBackgroundColor(Color.RED);
        }
    }


    //get current animators
    protected Collection<Animator> subclass_get_CurrentAnimators()
    {
        ArrayList<Animator> amArray = new ArrayList<Animator>();

        PropertyValuesHolder y_ph;
        PropertyValuesHolder size_ph;

        //To animate textViews
        for (int i = 0; i < CONSTANT_ITEMS_COUNT - 1; i++) {

            y_ph = PropertyValuesHolder.ofFloat("y", mTestItem_Y[i], mTestItem_Y[i+1]);
            amArray.add(ObjectAnimator.ofPropertyValuesHolder(mTestItems[i], y_ph));

            size_ph = PropertyValuesHolder.ofFloat("textSize", mTxtSize[i], mTxtSize[i+1]);
            amArray.add(ObjectAnimator.ofPropertyValuesHolder(mTestTxtViews[i], size_ph));

            /**
             * Because ObjectAnimator require a property accessor, but height for RelativeLayout
             * has no such thing like a setHeight(), therefore it needs a value Animator
             */

            final RelativeLayout rL = mTestItems[i];
            ValueAnimator vAm = ValueAnimator.ofInt((int) mTestItemHeight[i], (int) mTestItemHeight[i + 1]);
            vAm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = rL.getLayoutParams();
                    layoutParams.height = val;
                    rL.setLayoutParams(layoutParams);

                }
            });
            amArray.add(vAm);
        }

        //return Arrays.copyOf(amArray.toArray(),amArray.size(),Animator[].class);
        return amArray;
    }

    @Override
    protected void  subclass_restore_TopItem()
    {
        mTestTxtViews[0].setTextSize(mTxtSize[0]);
        super.subclass_restore_TopItem();
    }

    String subclass_getKeyForSavingHighestScore()
    {
        return getString(R.string.color_judge_highest_score);
    }
}
