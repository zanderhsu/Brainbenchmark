package com.zander.we.brainBenchmark;

enum PopupCondition{
    POPUP_CONDITION_WRONG(0),
    POPUP_CONDITION_TIME_IS_UP(1),
    POPUP_CONDITION_ABOUT(2);

    private int mValue;

    PopupCondition(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }
}

enum ActivityState {
    ACTIVITY_STATE_INITIAL,
    ACTIVITY_STATE_RUNNING,
    ACTIVITY_STATE_PAUSED,
    ACTIVITY_STATE_FINISHED
}

enum ActivityAction{
    ACTIVITY_ACTION_INIT, //When 1st time it entered
    ACTIVITY_ACTION_RUN,
    ACTIVITY_ACTION_PAUSE,
    ACTIVITY_ACTION_TERMINATE //When the given time (60s) expires
}

enum SubActivity{
    SUB_ACTIVITY_UNASSIGNED,
    SUB_ACTIVITY_COLOR,
    SUB_ACTIVITY_SHAPE,
    SUB_ACTIVITY_FACES,
}