package com.amirhosseinhamidian.my.presenter.categoryManager

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.presenter.adapter.CategoryManagerAdapter
import com.amirhosseinhamidian.my.utils.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_target.*
import kotlinx.android.synthetic.main.category_manager_item.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.new_category_dialog.*

@AndroidEntryPoint
class CategoryManagerActivity : AppCompatActivity() {
    private val viewModel: CategoryManagerViewModel by viewModels()
    private lateinit var adapter: CategoryManagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_manager)

        ivBack.setOnClickListener {
            onBackPressed()
        }

        recyclerview.layoutManager = GridLayoutManager(this,2)
        adapter = CategoryManagerAdapter(arrayListOf())
        recyclerview.adapter = adapter
        adapter.onItemClick = { category ->
            showDialog(category)
        }

        viewModel.getCategoriesWithNumberTasks().observe(this) {
            adapter.add(it)
        }

    }

    private fun showDialog(category: Category) {
        val dialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.new_category_dialog)

        dialog.edtCategoryTitle.setText(category.name)
//        dialog.colorSlider.selectColor(category.color!!)
        dialog.tvClose.setOnClickListener{dialog.dismiss()}

        dialog.tvConfirm.setOnClickListener {
            if (dialog.edtCategoryTitle.text.isNotEmpty()) {
                hideKeyboard()
                val update = Category(
                    id = category.id,
                    name = dialog.edtCategoryTitle.text.toString(),
                    color = dialog.colorSlider.selectedColor)
                viewModel.updateCategory(category = update)
                adapter.updateCategory(update)
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}