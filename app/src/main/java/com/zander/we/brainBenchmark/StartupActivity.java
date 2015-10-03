package com.zander.we.brainBenchmark;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.zander.we.brainBenchmark.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class StartupActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_startup);

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
            showAbout();
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

    private void showAbout()
    {

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }

        ft.addToBackStack(null);

        DialogFragment dialog = new popupDialogFragment();
        Bundle args = new Bundle();

        args.putInt(popupDialogFragment.CONSTANT_SHOW_WHAT, PopupCondition.POPUP_CONDITION_ABOUT.getValue());
        dialog.setArguments(args);

        dialog.show(ft, "About");

        getFragmentManager().executePendingTransactions();
        if(dialog.getDialog() != null)
        {
            dialog.getDialog().setCanceledOnTouchOutside(false);
        }
    }
}
