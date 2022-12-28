package com.amirhosseinhamidian.my.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amirhosseinhamidian.my.utils.Constants

class NotificationBroadcastReceiver : BroadcastReceiver(){
    override fun onReceive(contex: Context, intent: Intent) {
        contex.sendBroadcast(
            Intent(Constants.ACTION_NOTIFICATION_KEY).apply {
                putExtra(Constants.ACTION_NOTIFICATION_NAME, intent.action)
            }
        )
    }

}