package com.amirhosseinhamidian.my.presenter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.Task

class CategoryListAdapter(private val context: Context ,private val mList: ArrayList<Category>): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    fun add(listData: List<Category>) {
        mList.clear()
        mList.addAll(listData)
        notifyDataSetChanged()
    }

    fun add(category: Category) {
        mList.add(category)
        notifyItemInserted(mList.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = mList[position]
        holder.tvCategory.text = category.name
        holder.clHolder.setOnClickListener {
            resetSelectedCategory()
            category.isSelected = true
            notifyDataSetChanged()
        }
        if (category.isSelected) {
            holder.clHolder.setBackgroundResource(R.drawable.bg_button_stroke_orange)
            holder.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.secondary))
        } else{
            holder.clHolder.setBackgroundResource(R.drawable.bg_stroke_white)
            holder.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.white))
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    private fun resetSelectedCategory() {
        mList.forEach {
            it.isSelected = false
        }
    }

    fun getCategorySelected(): Category {
        mList.forEach {
            if (it.isSelected)
                return it
        }
        return Category(name = "")
    }

    fun selectCategory(category: String) {
        for (item in mList) {
            if (category == item.name) {
                item.isSelected = true
            }
        }
        notifyDataSetChanged()
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCategory : TextView = itemView.findViewById(R.id.tvCategory)
        val clHolder : ConstraintLayout = itemView.findViewById(R.id.clHolder)
    }
}