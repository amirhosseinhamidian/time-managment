package com.amirhosseinhamidian.my.presenter.mainScreen

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.presenter.adapter.TaskListAdapter
import com.amirhosseinhamidian.my.presenter.addEditScreen.AddEditActivity
import com.amirhosseinhamidian.my.utils.CustomDialog
import com.amirhosseinhamidian.my.utils.SwipeRecyclerviewItemCallback
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter
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
        viewModel.getAllTask().observe(this) {
            adapter.add(it)
        }
        viewModel.getTodayElapsedTime().observe(this) {
            adapter.addTodayElapsedTime(it)
        }
    }

    private fun setupRecyclerview() {
        adapter = TaskListAdapter(this , arrayListOf(),true)
        recyclerview.layoutManager = LinearLayoutManager(this)
        recyclerview.adapter = adapter
        val swipeHandler = object : SwipeRecyclerviewItemCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {
                    ItemTouchHelper.RIGHT -> {
                        val adapter = recyclerview.adapter as TaskListAdapter
                        val intent = Intent(this@MainActivity , AddEditActivity::class.java)
                        intent.putExtra("mode",AddEditActivity.MODE_EDIT)
                        intent.putExtra("task",adapter.getItemPosition(viewHolder.adapterPosition))
                        startActivity(intent)
                    }

                    ItemTouchHelper.LEFT -> {
                        val customDialog = CustomDialog(this@MainActivity)
                        customDialog.setCancelable(false)
                        customDialog.setTitle("Delete")
                        customDialog.setMessage("Are you sure to delete this task? \n It's clear forever")
                        customDialog.setPositiveButton("Yes") {
                            val adapter = recyclerview.adapter as TaskListAdapter
                            val taskToClear = adapter.removeAt(viewHolder.adapterPosition)
                            customDialog.dismiss()
                            viewModel.deleteTask(taskToClear)
                        }
                        customDialog.setNegativeButton("Cancel") {
                            val adapter = recyclerview.adapter as TaskListAdapter
                            adapter.notifyDataSetChanged()
                            customDialog.dismiss()
                        }
                        customDialog.show()
                    }
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }
}