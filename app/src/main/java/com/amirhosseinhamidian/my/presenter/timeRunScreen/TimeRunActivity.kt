package com.amirhosseinhamidian.my.presenter.timeRunScreen

import android.annotation.SuppressLint
import android.content.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.service.TimerService
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.isServiceRunningInForeground
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_time_run.*
import kotlinx.android.synthetic.main.activity_time_run.view.*
import kotlinx.android.synthetic.main.bottom_sheet_desc_task.*
import kotlinx.android.synthetic.main.bottom_sheet_desc_task.view.*
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TimeRunActivity : AppCompatActivity() {
    private val viewModel: TimeRunViewModel by  viewModels()
    private lateinit var task: Task
    private var timerSec: Int = 0
    private var isBound = MutableLiveData(false)
    private val intentToService by lazy {
        Intent(this,TimerService::class.java)
    }
    private lateinit var timerService: TimerService
    private val receiver: TimerStatusReceiver by lazy {
        TimerStatusReceiver()
    }


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_time_run)
        if (!intent.hasExtra("task")) {
            finish()
            return
        }
        task = intent.getSerializableExtra("task") as Task
        tvTaskTitle.text = task.title
        tvTaskCategory.text = task.category
        tvTimeSpent.text = String.format("%02d",(task.elapsedTime / (60 * 60)) % 24) +
                " : " + String.format("%02d",(task.elapsedTime / 60) % 60) +
                " : " + String.format("%02d",task.elapsedTime % 60)

        isBound.postValue(
            isServiceRunningInForeground(TimerService::class.java)
        )

        isBound.observe(this) { isActive ->
            lifecycleScope.launch {
                updateUI(isActive)
            }
        }

        btStart.setOnClickListener {
            if (isBound.value!!)
                stopTimerService()
            else
                startTimerService()
        }

        llSave.setOnClickListener {
            if (isBound.value!!) stopTimerService()
            task.elapsedTime += timerSec
            viewModel.updateTime(task)
            finish()
        }

        if (task.description!!.isEmpty()) {
            ivDesc.visibility = View.GONE
        }

        ivDesc.setOnClickListener {
            showDescription()
        }
    }

    private fun showDescription() {
        val bottomSheet = BottomSheetDialog(this, R.style.TutorialBottomSheetDialog)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_desc_task,null)
        view.tvDesc.text = task.description
        view.ivClose.setOnClickListener {
            bottomSheet.dismiss()
        }
        bottomSheet.setCancelable(true);
        bottomSheet.setContentView(view);
        bottomSheet.setCanceledOnTouchOutside(true);
        bottomSheet.getWindow()!!.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this,R.color.transparent)));
        bottomSheet.show();
    }

    override fun onResume() {
        super.onResume()
        isBound.postValue(isServiceRunningInForeground(TimerService::class.java))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver, IntentFilter(Constants.ACTION_TIME_KEY))
    }

    private fun startTimerService() {
        startService(intentToService)
        bindService(intentToService, mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun stopTimerService() {
        if (isBound.value!!) {
            unbindService(mServiceConnection)
            isBound.postValue(false)
        }
        stopService(intentToService)
    }

    private fun updateUI(isStart: Boolean) {
        if (isStart) {
            // when the activity going to be Destroyed, the service will be Unbind from activity,
            // But is still running in foreground. So when you start the app again, you should
            // bind the activity to service again.
            bindService(intentToService, mServiceConnection , Context.BIND_AUTO_CREATE)

            btStart.setTextColor(ContextCompat.getColor(this, R.color.secondary))
            btStart.setBackgroundResource(R.drawable.bg_button_stroke_orange)
            btStart.text = "STOP"
        } else {
            btStart.setTextColor(ContextCompat.getColor(this, R.color.white))
            btStart.setBackgroundResource(R.drawable.bg_solid_orange)
            btStart.text = "START"
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder = service as TimerService.TimerBinder
            timerService = myBinder.service
            isBound.postValue(true)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }

    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onStop() {
        super.onStop()
        if (isBound.value!!) {
            unbindService(mServiceConnection)
            isBound.postValue(false)
        }
    }

    inner class TimerStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_TIME_KEY -> {
                    if (intent.hasExtra(Constants.ACTION_TIME_VALUE)) {
                        val intentExtra = intent.getStringExtra(Constants.ACTION_TIME_VALUE)
                        timerSec = intent.getIntExtra(Constants.ACTION_TIME_VALUE,0)
                        if (intentExtra == Constants.ACTION_TIMER_STOP) {
                            llSave.performClick()
                        } else {
                            tvTimer.text = intent.getStringExtra(Constants.ACTION_TIME_TEXT_VALUE)
                        }
                    }
                }
            }
        }
    }
}