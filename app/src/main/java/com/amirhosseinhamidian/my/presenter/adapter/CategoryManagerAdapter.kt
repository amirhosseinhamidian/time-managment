package com.amirhosseinhamidian.my.presenter.adapter

import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category

class CategoryManagerAdapter(private val mList: ArrayList<Category>): RecyclerView.Adapter<CategoryManagerAdapter.ViewHolder>() {

    var onItemClick: ((Category) -> Unit)? = null

    fun add(list: List<Category>) {
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.category_manager_item, parent , false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = mList[position]

        holder.tvCategory.text = category.name
        holder.tvNumTasks.text = "Tasks: ${category.numberTasks}"
//        val background: Drawable = holder.vColor.background
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            background.colorFilter = BlendModeColorFilter(category.color!!, BlendMode.SRC_ATOP)
//        } else {
//            holder.vColor.background.setColorFilter(category.color!!, PorterDuff.Mode.SRC_ATOP)
//        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    fun updateCategory(category: Category) {
        mList.forEach {
            if (it.id == category.id) {
                mList[mList.indexOf(it)] = category
            }
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        val tvNumTasks: TextView = itemView.findViewById(R.id.tvNumTasks)
        val vColor: View = itemView.findViewById(R.id.vColor)

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(mList[adapterPosition])
            }
        }
    }
}