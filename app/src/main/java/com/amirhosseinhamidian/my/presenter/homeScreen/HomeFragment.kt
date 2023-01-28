package com.amirhosseinhamidian.my.presenter.homeScreen

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
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
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var adapter: TaskListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerview()
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
        adapter = TaskListAdapter(requireContext() , arrayListOf(),true)
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        recyclerview.adapter = adapter
        val swipeHandler = object : SwipeRecyclerviewItemCallback(requireContext()) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when(direction) {
                    ItemTouchHelper.RIGHT -> {
                        val adapter = recyclerview.adapter as TaskListAdapter
                        val intent = Intent(activity , AddEditActivity::class.java)
                        intent.putExtra("mode",AddEditActivity.MODE_EDIT)
                        intent.putExtra("task",adapter.getItemPosition(viewHolder.adapterPosition))
                        startActivity(intent)
                    }

                    ItemTouchHelper.LEFT -> {
                        val customDialog = CustomDialog(requireContext())
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
        swipeHandler.setDisablePositions(listOf(0))
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }
}