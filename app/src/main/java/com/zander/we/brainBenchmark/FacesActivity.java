package com.zander.we.brainbenchmark;

import android.content.res.Resources;
import android.os.Bundle;

import java.util.Hashtable;


public class FacesActivity extends ShapeActivity {




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected  void subclass_initOnCreate()
    {
        super.subclass_initOnCreate();
        mActualActivity = SubActivity.SUB_ACTIVITY_FACES;
        mInstructionResId = R.string.face_judgement_instruction;
    }

    @Override
    protected void subclass_set_mTestPairs()
    {
        Resources res = getResources();

        mTestPairs = new Hashtable<Integer, String>();

        mTestPairs.put( R.drawable.face_abbott_1,    res.getString(R.string.face_abbott));
        mTestPairs.put( R.drawable.face_obama_1,    res.getString(R.string.face_obama));
        mTestPairs.put( R.drawable.face_kim_jongun_1,    res.getString(R.string.face_Jongun));
        mTestPairs.put( R.drawable.face_abbott_2,    res.getString(R.string.face_abbott));
        mTestPairs.put( R.drawable.face_kim_jongun_2,    res.getString(R.string.face_Jongun));
        mTestPairs.put( R.drawable.face_obama_2,    res.getString(R.string.face_obama));
        mTestPairs.put( R.drawable.face_putin_1,    res.getString(R.string.face_putin));

        mTestPairs.put( R.drawable.face_abbott_3,    res.getString(R.string.face_abbott));
        mTestPairs.put( R.drawable.face_kim_jongun_3,    res.getString(R.string.face_Jongun));
        mTestPairs.put( R.drawable.face_obama_3,    res.getString(R.string.face_obama));
        mTestPairs.put( R.drawable.face_putin_2,    res.getString(R.string.face_putin));
        mTestPairs.put( R.drawable.face_obama_4,    res.getString(R.string.face_obama));

        mTestPairs.put( R.drawable.face_abbott_4,    res.getString(R.string.face_abbott));
        mTestPairs.put( R.drawable.face_putin_3,    res.getString(R.string.face_putin));
        mTestPairs.put( R.drawable.face_obama_5,    res.getString(R.string.face_obama));

    }

    @Override
    String subclass_getKeyForSavingHighestScore()
    {
            return getString(R.string.face_judge_highest_score);
    }

}
