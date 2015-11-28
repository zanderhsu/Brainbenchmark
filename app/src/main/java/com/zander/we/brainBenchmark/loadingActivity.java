package com.zander.we.brainBenchmark;


import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class loadingActivity extends Activity {

    static loadingActivity sCurrentInstance = null;


    private TextView mContentView;

    private ProgressBar mProgress;
    private int mProgressStatus = 0;

    private Handler mHandler = new Handler();



    public void onOKbtn(View view)
    {
        Intent intent = new Intent(this, StartupActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading);

        sCurrentInstance = this;


        BrnBnchMrkHelper.setSystemFontNumber(3,(TextView)findViewById(R.id.txtview_game_title),60);

        //mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = (TextView)findViewById(R.id.fullscreen_content);
        BrnBnchMrkHelper.setSystemFontNumber(2,mContentView,32);

        showViewsByAnimation();
        // Set up the user interaction to manually show or hide the system UI.
       /* mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        //findViewById(R.id.dummy_button).setOnTouchListener(mDelayHideTouchListener);


        /*
        mProgress = (ProgressBar)findViewById(R.id.loading_prgbar);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mProgressStatus = 0;
                while( mProgressStatus < 100 )
                {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            mProgress.setProgress(mProgressStatus);
                        }
                    });


                    mProgressStatus++;
                    try {
                        Thread.sleep(10);

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.i("[zander]", "e:" + e.toString());
                        // handle the exception...
                        // For example consider calling Thread.currentThread().interrupt(); here.
                    }

                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(mCtx, StartupActivity.class);
                        startActivity(intent);
                    }
                });
            }
        }).start();
        */
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        //delayedHide(100);
    }



    private void showViewsByAnimation()
    {
        int[] view_ids = {
                //R.id.got_it_btn,
                //R.id.fullscreen_content,
                R.id.ll_loading
        };

        BrnBnchMrkHelper.showViewsByAnimation(this, view_ids);

    }
}
