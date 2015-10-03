package com.zander.we.brainBenchmark;import android.app.Activity;import android.app.AlertDialog;import android.app.Dialog;import android.app.DialogFragment;import android.content.DialogInterface;import android.os.Bundle;import android.util.Log;import java.util.Hashtable;public class popupDialogFragment extends DialogFragment {    final static String CONSTANT_SHOW_WHAT = "SHOW_WHAT";    Hashtable<Integer,Integer> mShowWhatVsMsgId = new Hashtable<Integer,Integer>();    @Override    public Dialog onCreateDialog(Bundle savedInstanceState) {        mShowWhatVsMsgId.put(PopupCondition.POPUP_CONDITION_WRONG.getValue(), R.string.info_fail);        mShowWhatVsMsgId.put(PopupCondition.POPUP_CONDITION_ABOUT.getValue(), R.string.info_about);        mShowWhatVsMsgId.put(PopupCondition.POPUP_CONDITION_TIME_IS_UP.getValue(), R.string.info_time_is_up);        // Use the Builder class for convenient dialog construction        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),AlertDialog.THEME_HOLO_DARK);        final int showWhat = getArguments().getInt(CONSTANT_SHOW_WHAT);        final int msgId = mShowWhatVsMsgId.get(showWhat);        builder.setMessage(msgId).setPositiveButton(R.string.str_close, new DialogInterface.OnClickListener() {            @Override            public void onClick(DialogInterface dialog, int id) {                if (showWhat == PopupCondition.POPUP_CONDITION_WRONG.getValue()                 || showWhat == PopupCondition.POPUP_CONDITION_TIME_IS_UP.getValue())                {                    Activity curActivity = getActivity();                    String className =curActivity.getClass().getSimpleName();                    if(!className.equals("StartupActivity")) {                        ((MajorRunningActivity) curActivity).ChangeState(ActivityAction.ACTIVITY_ACTION_TERMINATE);                    }                }            }        });        // Create the AlertDialog object and return it        return builder.create();    }    @Override    public void onDismiss (DialogInterface dialog)    {        super.onDismiss(dialog);        Log.d("info", "dialog's onMiss()");    }}