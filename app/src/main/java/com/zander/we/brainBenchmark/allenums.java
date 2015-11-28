package com.zander.we.brainBenchmark;

enum PopupCondition{
    POPUP_CONDITION_WRONG(0),
    POPUP_CONDITION_TIME_IS_UP(1),
    POPUP_CONDITION_ABOUT(2),
    POPUP_CONDITION_IF_EXIT(3);

    private int mValue;

    PopupCondition(int value)
    {
        mValue = value;
    }

    public static PopupCondition fromInteger(int x) {

        return PopupCondition.values()[x];

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
    ACTIVITY_ACTION_RESUME,
    ACTIVITY_ACTION_TERMINATE
}

enum SubActivity{
    SUB_ACTIVITY_UNASSIGNED,
    SUB_ACTIVITY_COLOR,
    SUB_ACTIVITY_SHAPE,
    SUB_ACTIVITY_FACES,
}

enum GameMode{
    GAME_MODE_NO_MISTAKES(1),
    GAME_MODE_IN_MINUTES(2);

    private final int value;
    GameMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GameMode getEnumByInt(int value)
    {
        switch(value)
        {
            case 2:
                return GAME_MODE_IN_MINUTES;

            default:
                return GAME_MODE_NO_MISTAKES;

        }
    }
}