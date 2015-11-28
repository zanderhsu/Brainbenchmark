package com.zander.we.brainBenchmark;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.zander.we.brainBenchmark.PopupCondition;
import com.zander.we.brainBenchmark.util.SystemUiHider;
import java.util.Timer;



/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class StartupActivity extends Activity {

    private Handler mHandler = new Handler();
    private Timer mTimer = new Timer(true);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startup);

        TextView menuTxtView = (TextView)findViewById(R.id.menu_txtview);

        BrnBnchMrkHelper.setSystemFontNumber(3,menuTxtView, 60);
        if(loadingActivity.sCurrentInstance != null)
        {
            loadingActivity.sCurrentInstance.finish();
        }
        else
        {
            Log.i("[BM]", "loadingActivity.sCurrentInstance is null");
        }
    }


    @Override
    protected void onStart()
    {
        super.onStart();
        showViewsByAnimation();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        showViewsByAnimation();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.major_running, menu);
        menu.findItem(R.id.action_share).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {

            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    public void gotoColorJudging(View view)
    {
        Intent intent = new Intent(this, ColorActivity.class);
        startActivity(intent);
    }

    public void gotoShapeJudging(View view)
    {
        Intent intent = new Intent(this, ShapeActivity.class);
        startActivity(intent);
    }

    public void gotoFacesJudging(View view)
    {
        Intent intent = new Intent(this, FacesActivity.class);
        startActivity(intent);
    }

    public void showAbout(View view)
    {
        BrnBnchMrkHelper.showPopup(this, PopupCondition.POPUP_CONDITION_ABOUT);
    }

    public boolean onKeyDown(int keyCode,/*@NonNull*/ KeyEvent event) {
        //only if it's in running state, make it to return stopped state
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }

        return super.onKeyDown(keyCode, event);
    }

    private void showViewsByAnimation()
    {
        int[] view_ids = {
              /*  R.id.on_colors_btn,
                R.id.on_shapes_btn,
                R.id.on_faces_btn,
                R.id.menu_txtview*/
                R.id.ll_startup
        };

        BrnBnchMrkHelper.showViewsByAnimation(this, view_ids);

    }

    public void myTest(View view)
    {
        showViewsByAnimation();
    }
}
