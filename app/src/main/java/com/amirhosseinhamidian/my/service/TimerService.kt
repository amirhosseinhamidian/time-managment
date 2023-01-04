package com.amirhosseinhamidian.my.service

import android.annotation.SuppressLint
import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.presenter.timeRunScreen.TimeRunActivity
import com.amirhosseinhamidian.my.utils.Constants
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class TimerService : Service() {

    private val mBinder: IBinder = TimerBinder()
    private lateinit var chronometer: Chronometer
    private var isBound = false
    private val job = Job()
    var timeFlow: MutableStateFlow<String> = MutableStateFlow("")
    private lateinit var notificationBuilder: Notification.Builder
    var timeSpent = 0L

    private var notificationReceiver: BroadcastReceiver =
        object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.extras?.getString(Constants.ACTION_NOTIFICATION_NAME)) {
                    Constants.ACTION_STOP -> {
                        stopForegroundService()
                    }
                }
            }
        }

    override fun onBind(p0: Intent?): IBinder {
        isBound = true
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        startForegroundService()

        isBound = true
        chronometer = Chronometer(this)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        CoroutineScope(job).launch(Dispatchers.IO) {

            // while isBound is true, means the service has a work to do because it is used in
            // While loop, So the service does not stopped and you should break from While loop.
            // Otherwise it recreates the service and notification again (after clicking on stop action)

            while (isBound) {
                if (!isBound) break
                timeFlow.value = getTimestamp().also { time ->
                    updateNotification(time)
                }
                delay(1000)
            }
        }
        return START_NOT_STICKY
    }

    private fun updateNotification(time: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = notificationBuilder
            .setContentText(time)
            .build()

        notificationManager.notify(Constants.NOTIFICATION_ID, notification)
    }

    private fun getTimestamp(): String {
        val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base + timeSpent

        val hours = (elapsedMillis / 3600000).toInt()
        val minutes = (elapsedMillis - hours * 3600000).toInt() / 60000
        val seconds = (elapsedMillis - hours * 3600000 - minutes * 60000).toInt() / 1000

        val time = getString(R.string.time, hours, minutes, seconds)

        if(elapsedMillis > 1000L)
            sendBroadcastEvent(Constants.ACTION_TIME_KEY, time, (elapsedMillis/1000).toInt())

        return time
    }

    override fun onDestroy() {
        super.onDestroy()
        isBound = false
        timeSpent = SystemClock.elapsedRealtime() - chronometer.base
        chronometer.stop()
        unregisterReceiver(notificationReceiver)
    }

    private fun stopForegroundService() {
        val elapsedMillis = SystemClock.elapsedRealtime() - chronometer.base
        sendBroadcastEvent(Constants.ACTION_TIME_KEY, Constants.ACTION_TIMER_STOP,((elapsedMillis/1000).toInt()))
        isBound = false
        chronometer.stop()

        // Stop foreground service and remove the notification
        stopForeground(true)

        // Stop the foreground service.
        stopSelf()

    }

    private fun sendBroadcastEvent(action: String, timeText: String, time: Int) {
        val timerIntent = Intent(action).apply {
            putExtra(Constants.ACTION_TIME_TEXT_VALUE, timeText)
            putExtra(Constants.ACTION_TIME_VALUE , time)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(timerIntent)
    }

    private fun startForegroundService() {
        val notification: Notification = getNotification()

        registerReceiver(notificationReceiver, IntentFilter(Constants.ACTION_NOTIFICATION_KEY))

        // Notification ID cannot be 0.
        startForeground(Constants.NOTIFICATION_ID, notification)
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun getNotification(): Notification {
        val intentMain = Intent(this, TimeRunActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntentMain = PendingIntent.getActivity(
            this,0, intentMain,PendingIntent.FLAG_IMMUTABLE)

        val intentStop = Intent(this, NotificationBroadcastReceiver::class.java).apply {
            action = Constants.ACTION_STOP
        }
        val pendingIntentStop = PendingIntent.getBroadcast(
            this,0, intentStop, PendingIntent.FLAG_IMMUTABLE
        )

        val channelId =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createNotificationChannel("my_service", "My Background Service")
            } else {
                // If earlier version channel ID is not used
                Constants.CHANNEL_ID
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = Notification.Builder(this, channelId)
                .setContentTitle("Task Timer")
                .setContentTitle("0")
                .setSmallIcon(R.drawable.ic_timer)
                .setContentIntent(pendingIntentMain)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setColor(getColor(R.color.background))
                .setColorized(true)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setTicker(getText(R.string.time))
                .setStyle(Notification.DecoratedCustomViewStyle())
                .addAction(
                    Notification.Action.Builder(
                        Icon.createWithResource(this, R.drawable.ic_close),
                        getString(R.string.btn_stop),
                        pendingIntentStop
                    ).build()
                )
        }
        return notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    inner class TimerBinder : Binder() {
        val service: TimerService
            get() = this@TimerService
    }
}