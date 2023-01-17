package com.amirhosseinhamidian.my.utils

object Constants {

    const val CHANNEL_NAME = "CountUpTimerService"
    const val CHANNEL_ID = "0"
    const val NOTIFICATION_ID = 101

    //region Time Service
    const val ACTION_NOTIFICATION_KEY = "ACTION_NOTIFICATION_KEY"
    const val ACTION_NOTIFICATION_NAME = "ACTION_NOTIFICATION_NAME"
    const val ACTION_START = "START"
    const val ACTION_STOP = "STOP"
    const val ACTION_TIME_KEY = "TIME_KEY"
    const val ACTION_TIME_TEXT_VALUE = "00:00:00"
    const val ACTION_TIME_VALUE = "TIME_VALUE"
    const val ACTION_TIMER_STOP = "TIMER_STOP"
    //endregion

    //region DB Status
    const val STATUS_RUNNING = 1
    const val STATUS_STOPPED = 0
    //endregion

    //region run task activity status
    const val STATUS_TASK_ACTIVE = 1
    const val STATUS_ANOTHER_TASK_ACTIVE = 2
    const val STATUS_ANY_TASK_ACTIVE = 3
    //endregion

    //region date status
    const val YESTERDAY_DATE_STATUS = 1
    const val CURRENT_DATE_STATUS = 2
    //endregion

    const val CURRENT_WEEK = 1
    const val NEXT_WEEK = 2

    const val FIRST_DAY_SATURDAY = 1
    const val FIRST_DAY_SUNDAY = 2
    const val FIRST_DAY_MONDAY = 3

}