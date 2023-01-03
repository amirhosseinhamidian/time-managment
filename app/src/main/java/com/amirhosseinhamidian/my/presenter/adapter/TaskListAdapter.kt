package com.amirhosseinhamidian.my.presenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.presenter.timeRunScreen.TimeRunActivity
import com.amirhosseinhamidian.my.utils.Constants


class TaskListAdapter(private val context: Context , private val mList: ArrayList<Task>) : RecyclerView.Adapter<TaskListAdapter.ViewHolder>() {


    fun add(dataList: List<Task>) {
        mList.clear()
        mList.addAll(dataList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = mList[position]

        holder.tvTaskTitle.text = task.title
        holder.tvTaskCategory.text = task.category
        holder.tvTaskTimeDone.text = calculateTimeInHourMinuteFormat(task.elapsedTime)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, TimeRunActivity::class.java)
            intent.putExtra("task", task)
            context.startActivity(intent)
        }
        if(task.taskStatus == Constants.STATUS_RUNNING) {
            holder.clHolder.setBackgroundResource(R.drawable.bg_solid_green)
        } else {
            holder.clHolder.setBackgroundResource(R.drawable.edit_text_box)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun calculateTimeInHourMinuteFormat(timeInSec: Int): String {
        val hour = timeInSec/3600
        val minute = timeInSec/60%60
        return "${hour}h : ${minute}m"
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTaskTitle : TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskCategory : TextView = itemView.findViewById(R.id.tvTaskCategory)
        val tvTaskTimeDone : TextView = itemView.findViewById(R.id.tvTaskTimeDone)
        val clHolder : ConstraintLayout = itemView.findViewById(R.id.clHolder)
    }
}