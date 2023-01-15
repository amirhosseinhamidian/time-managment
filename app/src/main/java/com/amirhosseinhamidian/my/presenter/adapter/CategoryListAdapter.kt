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
import com.amirhosseinhamidian.my.domain.model.CategoryTarget

class CategoryListAdapter(private val context: Context ,private val mList: ArrayList<Category>): RecyclerView.Adapter<CategoryListAdapter.ViewHolder>() {

    private var categoryDisableList: List<CategoryTarget> = emptyList()
    var onItemClick: ((Category) -> Unit)? = null

    fun add(listData: List<Category>) {
        mList.clear()
        mList.addAll(listData)
        notifyDataSetChanged()
    }

    fun addToFirst(category: Category) {
        mList.add(0,category)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = mList[position]
        holder.tvCategory.text = category.name
        if (category.isSelected) {
            holder.clHolder.setBackgroundResource(R.drawable.bg_button_stroke_orange)
            holder.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.secondary))
        } else{
            holder.clHolder.setBackgroundResource(R.drawable.bg_stroke_white)
            holder.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.white))
        }
        categoryDisableList.forEach {
            if (category.name == it.category.name) {
                holder.itemView.isEnabled = false
                holder.tvCategory.setTextColor(ContextCompat.getColor(context,R.color.grayDark))
                holder.clHolder.setBackgroundResource(R.drawable.bg_stroke_gray_dark)
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun resetSelectedCategory() {
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

    fun addDisableCategories(categoryTargetList: List<CategoryTarget>) {
        this.categoryDisableList = categoryTargetList
    }


    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCategory : TextView = itemView.findViewById(R.id.tvCategory)
        val clHolder : ConstraintLayout = itemView.findViewById(R.id.clHolder)

        init {
            itemView.setOnClickListener {
                val category = mList[adapterPosition]
                resetSelectedCategory()
                category.isSelected = true
                notifyDataSetChanged()
                onItemClick?.invoke(category)
            }
        }
    }
}