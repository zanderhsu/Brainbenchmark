package com.zander.we.brainBenchmark;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


/**
 * Created by zxu on 19/11/2015.
 */
public class BrnBnchMrkHelper {
    private static void setCustomFont(TextView txtView, String fontName, int size_in_dp)
    {
        ContextWrapper context = (ContextWrapper)txtView.getContext();

        Typeface tf = Typeface.createFromAsset(context.getAssets(),
                "fonts/" + fontName);

        txtView.setTypeface(tf);
        txtView.setTextSize(TypedValue.COMPLEX_UNIT_DIP,size_in_dp);

    }

    public static void setSystemFontNumber(int fontNumber, TextView txtView,int size_in_dp)
    {

        ContextWrapper context = (ContextWrapper)txtView.getContext();
        int fontID = context.getResources().getIdentifier("system_font_name" + fontNumber, "string", context.getPackageName());
        if(fontID != 0) {
            setCustomFont(txtView, context.getString(fontID), size_in_dp);
        }
        else
        {
            Log.i("[ERROR]","fond ID is zero in setSystemFontNumber()");
        }
    }


    public static void showViewsByAnimation(ContextWrapper context,int[] view_ids)
    {

        Animation startupAnimation = AnimationUtils.loadAnimation(context, R.anim.startup_btn_anim);
        Activity activity = null;

        if(context instanceof Activity){
            activity = (Activity)context;
        }

        for( int view_id: view_ids) {
            View view = activity.findViewById(view_id);
            view.startAnimation(startupAnimation);
        }

    }

    public static void showPopup(Activity curActivity, PopupCondition condition)
    {


        FragmentTransaction ft = curActivity.getFragmentManager().beginTransaction();
        Fragment prev = curActivity.getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        DialogFragment dialog = new popupDialogFragment();
        Bundle args = new Bundle();

        args.putInt(popupDialogFragment.CONSTANT_SHOW_WHAT, condition.getValue());
        dialog.setArguments(args);

        dialog.show(ft, "dialog");
        curActivity.getFragmentManager().executePendingTransactions();
        if(dialog.getDialog() != null)
        {
            dialog.getDialog().setCanceledOnTouchOutside(false);
        }
        else
        {
            Log.e("### ERROR ###", "getDialog() still returns NULL");
        }

    }
}
