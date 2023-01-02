package com.amirhosseinhamidian.my.presenter.mainScreen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.presenter.adapter.TaskListAdapter
import com.amirhosseinhamidian.my.presenter.addEditScreen.AddEditActivity
import com.amirhosseinhamidian.my.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_time_run.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter
//    private val receiver: TimerStatusReceiver by lazy {
//        TimerStatusReceiver()
//    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupRecyclerview()
        fab.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAllTask().observe(this, Observer {
            adapter.add(it)
        })
//        LocalBroadcastManager.getInstance(this)
//            .registerReceiver(receiver, IntentFilter(Constants.ACTION_TIME_KEY))
    }

    private fun setupRecyclerview() {
        adapter = TaskListAdapter(this , arrayListOf())
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
    }

    override fun onPause() {
        super.onPause()
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

//    inner class TimerStatusReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            when (intent.action) {
//                Constants.ACTION_TIME_KEY -> {
//                    if (intent.hasExtra(Constants.ACTION_TIME_VALUE)) {
//                        val intentExtra = intent.getStringExtra(Constants.ACTION_TIME_VALUE)
//                        if (intentExtra == Constants.ACTION_TIMER_STOP) {
//                            adapter.stopActiveTask()
//                        } else {
//
//                        }
//                    }
//                }
//            }
//        }
//    }
}