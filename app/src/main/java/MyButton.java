package com.zander.we.brainbenchmark;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

/**
 * Created by zxu on 20/11/2015.
 */
public class MyButton extends Button {

    public MyButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyButton(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean value = super.onTouchEvent(event);
        int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN )
        {
            int h_offset = getResources().getDimensionPixelSize(R.dimen.button_shadow_h_offset);
            int v_offset = getResources().getDimensionPixelSize(R.dimen.button_shadow_v_offset);
            setPadding(h_offset,v_offset,0,0);
        }
        else if (action == MotionEvent.ACTION_UP   )
        {
            setPadding(0,0,0,0);
        }

        return value;
    }
}
