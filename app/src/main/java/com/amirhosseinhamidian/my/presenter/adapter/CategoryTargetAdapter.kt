package com.amirhosseinhamidian.my.presenter.adapter

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.ShapeDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.CategoryTarget


class CategoryTargetAdapter(private val mList: ArrayList<CategoryTarget>): RecyclerView.Adapter<CategoryTargetAdapter.ViewHolder>() {

    fun add(listData: List<CategoryTarget>) {
        mList.clear()
        mList.addAll(listData)
        notifyDataSetChanged()
    }

    fun addToFirst(categoryTarget: CategoryTarget) {
        mList.add(0,categoryTarget)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_target_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryTarget = mList[position]
        holder.tvCategory.text = categoryTarget.category.name
        holder.tvTimeTarget.text = "${categoryTarget.hourTarget}h : ${categoryTarget.minuteTarget}m"
        val background: Drawable = holder.vColor.background
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            background.colorFilter = BlendModeColorFilter(categoryTarget.category.color!!, BlendMode.SRC_ATOP)
        } else {
            holder.vColor.background.setColorFilter(categoryTarget.category.color!!, PorterDuff.Mode.SRC_ATOP)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvTimeTarget: TextView = itemView.findViewById(R.id.tvTimeTarget)
        val vColor: View = itemView.findViewById(R.id.vColor)
    }
}