package com.amirhosseinhamidian.my.presenter.timeRunScreen

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.service.TimerService
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.CustomDialog
import com.amirhosseinhamidian.my.utils.isServiceRunningInForeground
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_time_run.*
import kotlinx.android.synthetic.main.activity_time_run.ivBack
import kotlinx.android.synthetic.main.activity_time_run.tvTaskCategory
import kotlinx.android.synthetic.main.activity_time_run.tvTaskTitle
import kotlinx.android.synthetic.main.bottom_sheet_desc_task.view.*
import kotlinx.coroutines.launch
import java.util.Date


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
    private var status = -1
    var timeSpent = 0L


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
            if (isActive) {
                viewModel.getTaskRunningId().observe(this) { taskActiveId ->
                    lifecycleScope.launch {
                        if (taskActiveId == task.id) {
                            status = Constants.STATUS_TASK_ACTIVE
                            updateUI()
                        }else {
                            status = Constants.STATUS_ANOTHER_TASK_ACTIVE
                            updateUI()
                        }
                    }
                }
            } else {
                lifecycleScope.launch {
                    status = Constants.STATUS_ANY_TASK_ACTIVE
                    updateUI()
                }
            }
        }

        btStart.setOnClickListener {
            val isActive = isBound.value!!
            viewModel.updateTaskStatus(!isActive, task.id!!)
            if (isActive) {
                stopTimerService()
                timeSpent = timerSec.toLong()
            } else {
                startTimerService()
            }
        }

        llSave.setOnClickListener {
            if (isBound.value!!) stopTimerService()
            task.elapsedTime += timerSec
            task.taskStatus = Constants.STATUS_STOPPED
            viewModel.updateTime(task)
            viewModel.checkDailyDetailIsExist(taskId = task.id!!)
                .observe(this) { timeToday ->
                    if (timeToday == null) {
                        viewModel.insertDetail(details = DailyDetails(
                            taskId = task.id!!,
                            date = viewModel.getCurrentDate(),
                            time = timerSec
                        ))
                    } else {
                        viewModel.updateDetail(taskId = task.id!!, time = timeToday + timerSec)
                    }
            }
            finish()
        }

        ivBack.setOnClickListener { onBackPressed() }

        if (task.description!!.isEmpty()) {
            ivDesc.visibility = View.GONE
        }

        ivDesc.setOnClickListener {
            showDescription()
        }

        ivReset.setOnClickListener {
            val customDialog = CustomDialog(this)
            customDialog.setTitle("Warning")
            customDialog.setMessage("Are you sure to reset your time?")
            customDialog.setCancelable(false)
            customDialog.setPositiveButton("Yes") {
                stopTimerService()
                timerSec = 0
                timeSpent = 0L
                tvTimer.text = "00:00:00"
                customDialog.dismiss()
            }
            customDialog.setNegativeButton("close") {
                customDialog.dismiss()
            }
            customDialog.show()
        }
    }

    private fun showDescription() {
        val bottomSheet = BottomSheetDialog(this, R.style.DialogStyle)
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

    private fun updateUI() {
        when(status) {
            Constants.STATUS_TASK_ACTIVE -> {
                // when the activity going to be Destroyed, the service will be Unbind from activity,
                // But is still running in foreground. So when you start the app again, you should
                // bind the activity to service again.
                bindService(intentToService, mServiceConnection , Context.BIND_AUTO_CREATE)

                btStart.setTextColor(ContextCompat.getColor(this, R.color.secondary))
                btStart.setBackgroundResource(R.drawable.bg_button_stroke_orange)
                btStart.text = "STOP"
            }

            Constants.STATUS_ANOTHER_TASK_ACTIVE -> {
                btStart.setTextColor(ContextCompat.getColor(this, R.color.white))
                btStart.setBackgroundResource(R.drawable.bg_solid_gray_dark)
                btStart.text = "Running Another ..."
                btStart.isEnabled = false
                llSave.visibility = View.GONE
                tvTimer.alpha = 0.5f
            }

            Constants.STATUS_ANY_TASK_ACTIVE -> {
                btStart.setTextColor(ContextCompat.getColor(this, R.color.white))
                btStart.setBackgroundResource(R.drawable.bg_solid_orange)
                btStart.text = "START"
            }
        }
    }

    private val mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val myBinder = service as TimerService.TimerBinder
            timerService = myBinder.service
            timerService.timeSpent = timeSpent * 1000L
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
            try {
                unbindService(mServiceConnection)
            }catch (e: java.lang.Exception) {
            }
            isBound.postValue(false)
        }
    }

    override fun onBackPressed() {
        if (status == Constants.STATUS_ANY_TASK_ACTIVE && timerSec != 0 ) {
            showSaveAlertDialog()
        } else {
            super.onBackPressed()
        }
    }

    private fun showSaveAlertDialog() {
        val dialog = Dialog(this, R.style.DialogStyle)
        val view = LayoutInflater.from(this)
            .inflate(R.layout.alert_dialog_save_task_time_elapsed, null,false)
        dialog.setCancelable(false)
        val tvSave = view.findViewById<TextView>(R.id.tvSave)
        val tvNotSave = view.findViewById<TextView>(R.id.tvNotSave)
        tvSave.setOnClickListener {
            llSave.performClick()
            super.onBackPressed()
        }
        tvNotSave.setOnClickListener {
            super.onBackPressed()
        }
        dialog.setContentView(view)
        dialog.show()
    }

    inner class TimerStatusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Constants.ACTION_TIME_KEY -> {
                    if (status == Constants.STATUS_ANOTHER_TASK_ACTIVE) return
                    if (intent.hasExtra(Constants.ACTION_TIME_VALUE)) {
                        val intentExtra = intent.getStringExtra(Constants.ACTION_TIME_VALUE)
                        timerSec = intent.getIntExtra(Constants.ACTION_TIME_VALUE,0)
                        if (intentExtra == Constants.ACTION_TIMER_STOP) {

                        } else {
                            tvTimer.text = intent.getStringExtra(Constants.ACTION_TIME_TEXT_VALUE)
                        }
                    }
                }
            }
        }
    }
}