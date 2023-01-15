package com.amirhosseinhamidian.my.presenter.addEditScreen

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
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
    private lateinit var taskEdit: Task
    private var mode = MODE_ADD
    companion object {
        const val MODE_ADD = 1
        const val MODE_EDIT = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit)
        ivBack.setOnClickListener { onBackPressed() }
        if (intent.hasExtra("mode")) {
            mode = intent.getIntExtra("mode", MODE_ADD)
        }

        if (mode == MODE_EDIT && intent.hasExtra("task")) {
            taskEdit = intent.getSerializableExtra("task") as Task
            edtTaskTitle.setText(taskEdit.title)
            edtTaskDsc.setText(taskEdit.description)
            tvTitlePage.text = "Edit Task"
            btSave.text = "Update"
        }

        setupRecyclerview()
        viewModel.getCategoryList().observe(this) {
            categoryListAdapter.add(it)
            if (mode == MODE_EDIT) {
                categoryListAdapter.selectCategory(taskEdit.category)
            }
        }

        llNewCategory.setOnClickListener {
            showDialog()
        }

        btSave.setOnClickListener {
            categorySelected = categoryListAdapter.getCategorySelected()
            if (categorySelected.name == "") {
                Toast.makeText(this,"please select category", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (tvTaskTitle.text.isNotEmpty()){
                if (mode == MODE_ADD) {
                    viewModel.saveTask(
                        Task(title = edtTaskTitle.text.toString(),
                            description = edtTaskDsc.text.toString(),
                            category = categorySelected.name,
                            elapsedTime = 0)
                    )
                } else if (mode == MODE_EDIT) {
                    viewModel.updateTask(
                        Task(id = taskEdit.id,
                            title = edtTaskTitle.text.toString(),
                            description = edtTaskDsc.text.toString(),
                            category = categorySelected.name,
                            elapsedTime = taskEdit.elapsedTime)
                    )
                }
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
                categoryListAdapter.addToFirst(category)
            }
            dialog.dismiss()
        }
        dialog.show()
    }
}