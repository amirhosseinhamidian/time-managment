package com.amirhosseinhamidian.my.presenter.timeRunScreen

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_time_run.*
import java.util.*
import kotlin.concurrent.timerTask

@AndroidEntryPoint
class TimeRunActivity : AppCompatActivity() {
    private val viewModel: TimeRunViewModel by  viewModels()
    private lateinit var task: Task
    private lateinit var timer: Timer
    private var isTimerOn: Boolean = false
    private var timerSec: Int = 0
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

        btStart.setOnClickListener {
            if (!isTimerOn) {
                isTimerOn = true
                btStart.setTextColor(ContextCompat.getColor(this, R.color.teal_200))
                btStart.setBackgroundResource(R.drawable.bg_button_stroke_orange)
                btStart.text = "Puase"

                timer = Timer()
                timer.schedule(timerTask {
                    timerSec++
                    runOnUiThread{
                        tvHour.text = String.format("%02d",(timerSec / (60 * 60)) % 24)
                        tvMinute.text = String.format("%02d",(timerSec / 60) % 60)
                        tvSecond.text = String.format("%02d",timerSec % 60)
                    }
                },1000, 1000)

            } else{
                isTimerOn = false
                btStart.setTextColor(ContextCompat.getColor(this, R.color.white))
                btStart.setBackgroundResource(R.drawable.bg_solid_orange)
                btStart.text = "Start"

                timer.cancel()
                timer.purge()
            }
        }

        llSave.setOnClickListener {
            if (isTimerOn) btStart.performClick()
            task.elapsedTime += timerSec
            viewModel.updateTime(task)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            timer.cancel()
            timer.purge()
        }catch (e: java.lang.Exception){

        }
    }
}