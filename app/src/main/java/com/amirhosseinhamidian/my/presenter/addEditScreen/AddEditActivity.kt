package com.amirhosseinhamidian.my.presenter.addEditScreen

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.Task
import com.amirhosseinhamidian.my.presenter.adapter.CategoryListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_edit.*
import kotlinx.android.synthetic.main.new_category_dialog.*


@AndroidEntryPoint
class AddEditActivity: AppCompatActivity() {
    private val viewModel: AddEditViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var categorySelected: Category

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        setupRecyclerview()

        viewModel.getCategoryList().observe(this, Observer {
            categoryListAdapter.add(it)
        })

        llNewCategory.setOnClickListener {
            showDialog()
        }

        btSave.setOnClickListener {
            categorySelected = categoryListAdapter.getCategorySelected()
            if (categorySelected.name == "") {
                Toast.makeText(this,"please select categroy", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (tvTaskTitle.text.isNotEmpty()){
                viewModel.saveTask(
                    Task(title = edtTaskTitle.text.toString(),
                    category = categorySelected.name,
                    elapsedTime = 0
                )
                )
                finish()
            }else{
                Toast.makeText(this,"please enter title of task", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setupRecyclerview() {
        categoryListAdapter = CategoryListAdapter(this, arrayListOf())
        rvCategory.layoutManager = GridLayoutManager(this,2, GridLayoutManager.HORIZONTAL,false)
        rvCategory.adapter = categoryListAdapter
    }

    private fun showDialog() {
        val dialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.new_category_dialog)
        dialog.tvClose.setOnClickListener{dialog.dismiss()}
        dialog.tvConfirm.setOnClickListener {
            if (dialog.edtCategoryTitle.text.isNotEmpty()) {
                val category = Category(name = dialog.edtCategoryTitle.text.toString())
                viewModel.saveCategory(category = category)
                categoryListAdapter.add(category)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}