package com.amirhosseinhamidian.my.presenter.adapter

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.domain.model.TaskStatisticWeekly
import com.amirhosseinhamidian.my.utils.Date

class TaskStatisticsAdapter(private val context: Context, private val mList: ArrayList<TaskStatisticWeekly>): RecyclerView.Adapter<TaskStatisticsAdapter.ViewHolder>() {

    fun add(listData: List<TaskStatisticWeekly>) {
        mList.clear()
        mList.addAll(listData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_statistics_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val taskStatisticWeekly = mList[position]
        holder.tvTitleTask.text = taskStatisticWeekly.taskTitle
        holder.tvTitleCategory.text = taskStatisticWeekly.category.name
        val background: Drawable = holder.vColor.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background.colorFilter = BlendModeColorFilter(taskStatisticWeekly.category.color!!, BlendMode.SRC_ATOP)
        } else {
            background.setColorFilter(taskStatisticWeekly.category.color!!, PorterDuff.Mode.SRC_ATOP)
        }
        holder.tvTotalTime.text = Date.calculateTimeInHourMinuteFormat(taskStatisticWeekly.totalTimeWeekly)
        holder.llDetailHolder.visibility = if (taskStatisticWeekly.isExpanded) View.VISIBLE else View.GONE
        holder.vDivider.visibility = if (taskStatisticWeekly.isExpanded) View.VISIBLE else View.GONE
        holder.ivArrow.rotation = if (taskStatisticWeekly.isExpanded) 180f else 0f
        holder.itemView.setOnClickListener {
            if (taskStatisticWeekly.dailyDetailsList.isEmpty()) return@setOnClickListener
            taskStatisticWeekly.isExpanded = !taskStatisticWeekly.isExpanded
            setupDailyDetailLayout(taskStatisticWeekly.dailyDetailsList,holder.llDetailHolder)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun setupDailyDetailLayout(dailyDetailsList : List<DailyDetails>, layoutHolder: LinearLayout) {
        layoutHolder.removeAllViews()
        dailyDetailsList.forEach {
            val view = LayoutInflater.from(context).inflate(R.layout.daily_details_item,null)
            val tvDate = view.findViewById<TextView>(R.id.tvDate)
            val tvTime = view.findViewById<TextView>(R.id.tvTime)
            tvDate.text = it.date
            tvTime.text = Date.calculateTimeInHourMinuteSecondFormat(it.time)
            layoutHolder.addView(view)
        }
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvTitleTask : TextView = itemView.findViewById(R.id.tvTitleTask)
        val tvTitleCategory : TextView = itemView.findViewById(R.id.tvTitleCategory)
        val tvTotalTime : TextView = itemView.findViewById(R.id.tvTotalTime)
        val ivArrow : ImageView = itemView.findViewById(R.id.ivArrow)
        val llDetailHolder : LinearLayout = itemView.findViewById(R.id.llDetailHolder)
        val vColor : View = itemView.findViewById(R.id.vColor)
        val vDivider : View = itemView.findViewById(R.id.vDivider)
    }
}