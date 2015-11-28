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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import java.util.Collection;
import java.util.Hashtable;


public class ShapeActivity extends MajorRunningActivity {


    float[] mTxtSize;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected String   subclass_get_TextOfAnItem(int itemIndex)
    {
        return mTestTxtViews[itemIndex].getText().toString();
    }

    protected void   subclass_set_TextOfAnItem (int itemIndex,  String text)
    {
        //adding \u2060 is just a workaround for a well-known bug on TextView of 4.0.3,
        mTestTxtViews[itemIndex].setText(text+"\u2060");
    }

    protected void   subclass_set_ValueOfAnItem(int itemIndex, Integer value)
    {
        int ImgResId = value;
        ImageView imgView = mTestImgViews[itemIndex];
/*        TextView txtView = mTestTxtViews[itemIndex];

        switch(ImgResId)
        {
            case R.drawable.shape_oval_b:
            case R.drawable.shape_rectangle_b:
                imgView.setScaleType(ImageView.ScaleType.FIT_END);
                break;

            default:
                imgView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                break;
        }
*/
        imgView.setImageResource(ImgResId);
        imgView.setTag(ImgResId);
    }

    protected Integer subclass_get_ValueOfAnItem(int itemIndex)
    {
        return (Integer)mTestImgViews[itemIndex].getTag();
    }

    //To set test views and textViews size and position and etc
    protected void subclass_initOnCreate()
    {
        Resources res = getResources();

        mActualActivity = SubActivity.SUB_ACTIVITY_SHAPE;
        mTxtSize = new float[CONSTANT_ITEMS_COUNT];

        for (int i = 0; i< CONSTANT_ITEMS_COUNT; i++)
        {
            mTxtSize[i] = res.getDimension(getResIdWithPattern("dimen","txtview0",i, "_textsize"))/mScaledDensity;
        }

        mInstructionResId = R.string.shape_judgement_instruction;


    }

    protected void subclass_set_mTestPairs()
    {
        Resources res = getResources();

        mTestPairs = new Hashtable<Integer, String>();

        mTestPairs.put( R.drawable.shape_circle,    res.getString(R.string.shape_circle));
        mTestPairs.put( R.drawable.shape_oval_a,      res.getString(R.string.shape_oval));
        mTestPairs.put( R.drawable.shape_oval_b,      res.getString(R.string.shape_oval));
        mTestPairs.put( R.drawable.shape_rectangle_a,res.getString(R.string.shape_rectangle));
        mTestPairs.put( R.drawable.shape_rectangle_b,res.getString(R.string.shape_rectangle));
        mTestPairs.put( R.drawable.shape_square,    res.getString(R.string.shape_square));
       // mTestPairs.put( R.drawable.shape_triangle,    res.getString(R.string.shape_triangle));
    }

    @Override
    protected void subclass_initBeforeRun()
    {
        super.subclass_initBeforeRun();

        Resources res = getResources();
        ViewGroup.LayoutParams vlp;
        ImageView imgView ;

        for(int i = 0; i < CONSTANT_ITEMS_COUNT; i++)
        {
            imgView = mTestImgViews[i];
            vlp = imgView.getLayoutParams();
            //Make the image view a square
            vlp.height  = ViewGroup.LayoutParams.MATCH_PARENT;
            vlp.width = (int)mTestItemHeight[i];
            imgView.setLayoutParams(vlp);
            imgView.setAdjustViewBounds(true);
            //imgView.setBackgroundColor(Color.BLACK);

            TextView txtView = mTestTxtViews[i];

            vlp = txtView.getLayoutParams();
            vlp.height  = ViewGroup.LayoutParams.MATCH_PARENT;
            vlp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            ((RelativeLayout.LayoutParams) vlp).addRule(RelativeLayout.RIGHT_OF, imgView.getId());
            txtView.setLayoutParams(vlp);

            txtView.setGravity(Gravity.CENTER_VERTICAL);
            txtView.setTextSize(mTxtSize[i]);
            txtView.setTextColor(res.getColor(R.color.info_text));
            txtView.setTypeface(null, Typeface.BOLD);
            txtView.setPadding((int) (mTestItemHeight[i] * 0.1F), 0, 0, 0);
            txtView.setShadowLayer(8f, 4f, 4f, Color.DKGRAY);
            // txtView.setBackgroundColor(Color.RED);
        }
    }

    //get current animators
    protected Collection<Animator> subclass_get_CurrentAnimators()
    {
        ArrayList<Animator> animatorArrayList = new ArrayList<Animator>();

        PropertyValuesHolder y_ph ;
        PropertyValuesHolder size_ph;

        Animator am;
        ValueAnimator vAm;

        //To animate textViews
        for (int i = 0; i < CONSTANT_ITEMS_COUNT - 1; i++)
        {
            size_ph = PropertyValuesHolder.ofFloat("textSize", mTxtSize[i], mTxtSize[i+1]);
            am = ObjectAnimator.ofPropertyValuesHolder(mTestTxtViews[i], size_ph);
            animatorArrayList.add(am);

            y_ph = PropertyValuesHolder.ofFloat("y", mTestItem_Y[i], mTestItem_Y[i+1]);
            am = ObjectAnimator.ofPropertyValuesHolder(mTestItems[i], y_ph);
            animatorArrayList.add(am);

            final RelativeLayout rL = mTestItems[i];
            final ImageView imgView = mTestImgViews[i];

            vAm = ValueAnimator.ofInt((int)mTestItemHeight[i], (int)mTestItemHeight[i+1]);
            vAm.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(){
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator)
                {
                    int val = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = rL.getLayoutParams();
                    layoutParams.height = val;
                    rL.setLayoutParams(layoutParams);

                    layoutParams = imgView.getLayoutParams();
                    layoutParams.width = val;
                    imgView.setLayoutParams(layoutParams);
                }
            });
            animatorArrayList.add(vAm);
        }

        //return Arrays.copyOf(animatorArrayList.toArray(), animatorArrayList.size(), Animator[].class);
        return animatorArrayList;
    }

    @Override
    protected void   subclass_restore_TopItem()
    {
        mTestTxtViews[0].setTextSize(mTxtSize[0]);

        ImageView topImgView = mTestImgViews[0];
        ViewGroup.LayoutParams vlp = topImgView.getLayoutParams();
        vlp.width = (int) mTestItemHeight[0];
        topImgView.setLayoutParams(vlp);

        super.subclass_restore_TopItem();
    }

    String subclass_getKeyForSavingHighestScore()
    {
        return getString(R.string.shape_judge_highest_score);
    }
}
