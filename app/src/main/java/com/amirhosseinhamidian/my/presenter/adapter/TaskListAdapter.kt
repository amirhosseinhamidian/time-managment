package com.amirhosseinhamidian.my.presenter.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.presenter.timeRunScreen.TimeRunActivity
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.Date.calculateTimeInHourMinuteFormat
import java.text.SimpleDateFormat
import java.util.*


class TaskListAdapter(private val context: Context , private val mList: ArrayList<Task>, private val showHeader: Boolean)
    : RecyclerView.Adapter<ViewHolder>() {

    var elapsedTime: Int = 0

    fun add(dataList: List<Task>) {
        mList.clear()
        if(showHeader) {
            mList.add(Task.hiddenTask())
        }
        mList.addAll(dataList)
        notifyDataSetChanged()
    }

    fun addTodayElapsedTime(time: Int) {
        elapsedTime = time
        notifyItemChanged(0)
    }

    fun removeAt(position: Int): Task {
        val taskToClear = mList[position]
        mList.removeAt(position)
        notifyItemRemoved(position)
        return taskToClear
    }

    fun getItemPosition(position: Int): Task {
        return mList[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.main_header_item,parent,false)
            return ViewHolderHeader(view)
        }
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = mList[position]
        when(holder.itemViewType) {
            VIEW_TYPE_HEADER -> {
                val viewHolder = holder as ViewHolderHeader
                val c: Date = Calendar.getInstance().time
                val df = SimpleDateFormat("dd MMM yyyy , EE", Locale.getDefault())
                viewHolder.tvDate.text = df.format(c)
                viewHolder.tvElapsedTime.text = calculateTimeInHourMinuteFormat(elapsedTime)
            }
            VIEW_TYPE_TASKS -> {
                val viewHolder = holder as ViewHolder
                viewHolder.tvTaskTitle.text = task.title
                viewHolder.tvTaskCategory.text = task.category
                viewHolder.tvTaskTimeDone.text = calculateTimeInHourMinuteFormat(task.elapsedTime)
                viewHolder.itemView.setOnClickListener {
                    val intent = Intent(context, TimeRunActivity::class.java)
                    intent.putExtra("task", task)
                    context.startActivity(intent)
                }
                if(task.taskStatus == Constants.STATUS_RUNNING) {
                    viewHolder.clHolder.setBackgroundResource(R.drawable.bg_solid_green)
                } else {
                    viewHolder.clHolder.setBackgroundResource(R.drawable.edit_text_box)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


    override fun getItemViewType(position: Int): Int {
        if (showHeader && position == 0) return VIEW_TYPE_HEADER
        return VIEW_TYPE_TASKS
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTaskTitle : TextView = itemView.findViewById(R.id.tvTaskTitle)
        val tvTaskCategory : TextView = itemView.findViewById(R.id.tvTaskCategory)
        val tvTaskTimeDone : TextView = itemView.findViewById(R.id.tvTaskTimeDone)
        val clHolder : ConstraintLayout = itemView.findViewById(R.id.clHolder)
    }

    class ViewHolderHeader(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvElapsedTime : TextView = itemView.findViewById(R.id.tvElapsedTime)
        val tvDate : TextView = itemView.findViewById(R.id.tvDate)
    }

    companion object {
        const val VIEW_TYPE_HEADER = 1
        const val VIEW_TYPE_TASKS = 2
    }
}