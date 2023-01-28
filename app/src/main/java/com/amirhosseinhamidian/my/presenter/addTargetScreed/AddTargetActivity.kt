package com.amirhosseinhamidian.my.presenter.addTargetScreed

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.CategoryTarget
import com.amirhosseinhamidian.my.domain.model.ChartValue
import com.amirhosseinhamidian.my.presenter.adapter.CategoryListAdapter
import com.amirhosseinhamidian.my.presenter.adapter.CategoryTargetAdapter
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.CustomDialog
import com.amirhosseinhamidian.my.utils.hideKeyboard
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssarcseekbar.app.segmented.SegmentedArc
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_target.*
import kotlinx.android.synthetic.main.bottom_sheet_add_category_target_time.view.*
import kotlinx.android.synthetic.main.max_sleep_time_in_week_dialog.*
import kotlinx.android.synthetic.main.new_category_dialog.tvClose
import kotlinx.android.synthetic.main.new_category_dialog.tvConfirm
import kotlinx.android.synthetic.main.option_category_dialog.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class AddTargetActivity : AppCompatActivity(), OnChartValueSelectedListener {
    private val viewModel: AddTargetViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter
    private var weekStatus = Constants.CURRENT_WEEK
    private lateinit var categoryTargetAdapter: CategoryTargetAdapter
    private var sleepTime = 8f
    private var totalTimeTargetedInSec = 0
    private var totalWeekUpTimeInSec = 0
    private var listDataChart = arrayListOf<ChartValue>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_target)
        ivBack.setOnClickListener {
            onBackPressed()
        }

        tvDate.text = viewModel.getTodayDate()

        fabAddCategory.setOnClickListener {
            showAddCategoryTargetTimeBottomSheet(null)
        }

        viewModel.getSleepTimePerDay().observe(this) {
            sleepTime = it
            setupWeekSpinner()
        }

        setWeekDateView(Constants.CURRENT_WEEK)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        rvCategory.layoutManager = GridLayoutManager(this,2)
        categoryTargetAdapter = CategoryTargetAdapter(arrayListOf())
        rvCategory.adapter = categoryTargetAdapter
        categoryTargetAdapter.onItemClick = { categoryTarget ->
            showOptionDialog(categoryTarget)
        }
    }

    private fun showOptionDialog(categoryTarget: CategoryTarget) {
        val dialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.option_category_dialog)
        dialog.tvClose.setOnClickListener{dialog.dismiss()}
        dialog.llEdit.setOnClickListener {
            showAddCategoryTargetTimeBottomSheet(categoryTarget)
            dialog.dismiss()
        }

        dialog.llDelete.setOnClickListener {
            val customDialog = CustomDialog(this)
            customDialog.setCancelable(false)
            customDialog.setTitle("Delete")
            customDialog.setMessage("Are you sure to delete this category target? \n It's clear forever")
            customDialog.setPositiveButton("Yes") {
                categoryTargetAdapter.remove(categoryTarget)
                viewModel.deleteCategoryTarget(categoryTarget)
                listDataChart.remove(ChartValue(categoryTarget.totalTimeTargetInSec!!.toFloat(),categoryTarget.category.color!!))
                setDataChart(listDataChart)
                customDialog.dismiss()
            }
            customDialog.setNegativeButton("Cancel") {
                customDialog.dismiss()
            }
            customDialog.show()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setupWeekSpinner() {
        val items = arrayOf("This week", "Next week")
        val adapter: ArrayAdapter<String> = ArrayAdapter(this,R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeek.adapter = adapter
        spinnerWeek.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                when(position) {
                    0 -> setWeekDateView(Constants.CURRENT_WEEK)
                    1 -> setWeekDateView(Constants.NEXT_WEEK)
                }
                totalTimeTargetedInSec = 0
                viewModel.getTargetsWeek(viewModel.getStartDateWeek(weekStatus)).observe(this@AddTargetActivity) {
                    categoryTargetAdapter.add(it)
                    it.forEach {
                        totalTimeTargetedInSec += it.totalTimeTargetInSec?: 0
                    }
                }
                totalWeekUpTimeInSec = viewModel.getTotalTimeLeftInWeek(sleepTime,weekStatus)
                viewModel.getChartValues(viewModel.getStartDateWeek(weekStatus),totalWeekUpTimeInSec).observe(this@AddTargetActivity) { list ->
                    listDataChart.clear()
                    listDataChart.addAll(list)
                    setupChartView()
                    setDataChart(list);
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWeekDateView(week: Int) {
        tvDatePeriod.text = viewModel.getStartDateWeek(week) + " - " + viewModel.getEndDateWeek(week)
        tvNumWeek.text = "Week " + viewModel.getNumberWeek(week)
        weekStatus = week
    }

    private fun showAddCategoryTargetTimeBottomSheet(editCategoryTarget: CategoryTarget?) {
        val bottomSheet = BottomSheetDialog(this, R.style.DialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_category_target_time, null)

        //region select category
        if (editCategoryTarget == null) {
            //region setup suggestion recyclerview
            view.rvSuggestion.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            categoryListAdapter = CategoryListAdapter(this, arrayListOf(),false)
            view.rvSuggestion.adapter = categoryListAdapter

            viewModel.getCategoryList().observe(this) { categoryList ->
                if (categoryList.isNotEmpty()) {
                    viewModel.getTargetsWeek(viewModel.getStartDateWeek(weekStatus))
                        .observe(this) { categoryTargetList ->
                            view.rvSuggestion.visibility = View.VISIBLE
                            categoryListAdapter.add(categoryList)
                            categoryListAdapter.onItemClick = { categorySelected ->
                                view.edtCategory.setText(categorySelected.name)
                                view.colorSlider.selectColor(categorySelected.color!!)
                                view.colorSlider.isEnabled = false
                            }
                            categoryListAdapter.addDisableCategories(categoryTargetList)
                        }
                }
            }

            //endregion

            //region edit text category name
            view.edtCategory.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun afterTextChanged(text: Editable?) {
                    view.pbAddCategory.visibility = View.VISIBLE
                    view.ivCategoryAdded.visibility = View.INVISIBLE
                    view.ivAddCategory.visibility = View.INVISIBLE
                    viewModel.isThereCategory(text.toString()).observe(this@AddTargetActivity) {
                        view.pbAddCategory.visibility = View.INVISIBLE
                        if (it) {
                            view.ivCategoryAdded.visibility = View.VISIBLE
                            view.colorSlider.isEnabled = false
                            categoryListAdapter.resetSelectedCategory()
                            val category = categoryListAdapter.selectCategory(text.toString())
                            if (category != null) {
                                view.colorSlider.selectColor(category.color!!)
                            }
                        } else {
                            view.ivAddCategory.visibility = View.VISIBLE
                            view.colorSlider.isEnabled = true
                            categoryListAdapter.resetSelectedCategory()
                            categoryListAdapter.notifyDataSetChanged()
                        }
                    }
                }
            })
            //endregion

            //region add new category
            view.ivAddCategory.setOnClickListener {
                hideKeyboard(view.ivAddCategory)
                if (view.edtCategory.text.isNotEmpty()) {
                    view.pbAddCategory.visibility = View.VISIBLE
                    view.ivAddCategory.visibility = View.INVISIBLE
                    view.ivCategoryAdded.visibility = View.INVISIBLE
                    viewModel.addNewCategory(
                        view.edtCategory.text.toString(),
                        view.colorSlider.selectedColor
                    ).observe(this) {
                        if (it >= 0) {
                            view.saHour.requestFocus()
                            view.tvErrorCategory.visibility = View.INVISIBLE
                            view.pbAddCategory.visibility = View.INVISIBLE
                            view.tvErrorCategory.visibility = View.INVISIBLE
                            view.ivCategoryAdded.visibility = View.VISIBLE
                            view.colorSlider.isEnabled = false
                            val category = Category(
                                id = it,
                                name = view.edtCategory.text.toString(),
                                color = view.colorSlider.selectedColor
                            )
                            categoryListAdapter.addToFirst(category)
                            categoryListAdapter.selectCategory(category.name)
                        } else {
                            view.tvErrorCategory.text =
                                "Error saving new category, please choose another name."
                            view.tvErrorCategory.visibility = View.VISIBLE
                        }
                    }
                } else {
                    view.tvErrorCategory.text = "Please type new category title."
                    view.tvErrorCategory.visibility = View.VISIBLE
                }
            }
            //endregion

        } else {
            view.btSave.text = "Update"
            view.tvTitle.text = editCategoryTarget.category.name
            view.rlHolderAddCategory.visibility = View.GONE
            view.rlSelectCategory.visibility = View.GONE
        }

        //endregion

        //region select time

        val maxSecondTarget = totalWeekUpTimeInSec - totalTimeTargetedInSec
        val maxHour : Int = maxSecondTarget / 3600
        val maxMin : Int = maxSecondTarget / 60

        //region hour
        view.saHour.setMin(0)
        if (editCategoryTarget == null) {
            view.saHour.setMax(maxHour)
            view.saHour.setSegmentedProgress(0)
        } else {
            view.saHour.setMax(maxHour + editCategoryTarget.hourTarget)
            view.saHour.setSegmentedProgress(editCategoryTarget.hourTarget)
        }
        view.saHour.setOnProgressChangedListener(object : SegmentedArc.onProgressChangedListener {
            override fun onProgressChanged(progress: Int) {
                view.tvHour.text = "%02d".format(progress)
            }
        })
        //endregion

        //region minute
        var minute = 0
        view.saMinute.setMin(0)
        if (editCategoryTarget == null) {
            if (maxMin >= 55){
                view.saMinute.setMax(11)
            } else {
                view.saMinute.setMax(maxMin/5)
            }
            view.saMinute.setSegmentedProgress(0)
        } else {
            view.saMinute.setMax(11)
            view.saMinute.setSegmentedProgress(editCategoryTarget.minuteTarget / 5)
        }

        view.saMinute.setOnProgressChangedListener(object : SegmentedArc.onProgressChangedListener {
            override fun onProgressChanged(progress: Int) {
                when(progress) {
                    0 -> minute = 0
                    1 -> minute = 5
                    2 -> minute = 10
                    3 -> minute = 15
                    4 -> minute = 20
                    5 -> minute = 25
                    6 -> minute = 30
                    7 -> minute = 35
                    8 -> minute = 40
                    9 -> minute = 45
                    10 -> minute = 50
                    11 -> minute = 55
                }
                view.tvMinute.text = "%02d".format(minute)
            }
        })
        //endregion

        //endregion

        view.btSave.setOnClickListener {
            //region add new
            if (editCategoryTarget == null && categoryListAdapter.getCategorySelected().name.isNotEmpty()) {
                val hourTarget = view.saHour.getSegmentedProgress()
                val minuteTarget = view.saMinute.getSegmentedProgress() * 5
                val timeTargetedInSec = hourTarget * 3600 + minuteTarget * 60
                val newCategoryTarget = CategoryTarget(
                    category = categoryListAdapter.getCategorySelected(),
                    hourTarget = hourTarget,
                    minuteTarget = minuteTarget,
                    totalTimeTargetInSec = timeTargetedInSec,
                    weekNumber = viewModel.getNumberWeek(weekStatus),
                    startDateTarget = viewModel.getStartDateWeek(weekStatus),
                    endDateTarget = viewModel.getEndDateWeek(weekStatus),
                    createAt = System.currentTimeMillis()
                )
                viewModel.saveCategoryTarget(newCategoryTarget).observe(this) {
                    if (it>=0) {
                        newCategoryTarget.id = it
                        totalTimeTargetedInSec += newCategoryTarget.totalTimeTargetInSec ?:0
                        categoryTargetAdapter.addToFirst(newCategoryTarget)

                        viewModel.getChartValues(viewModel.getStartDateWeek(weekStatus),totalWeekUpTimeInSec).observe(this@AddTargetActivity) { list ->
                            listDataChart.clear()
                            listDataChart.addAll(list)
                            setDataChart(list);
                        }
                    }
                }
                bottomSheet.dismiss()
            }
            //endregion

            //region update
            else if (editCategoryTarget != null) {
                val hourTarget = view.saHour.getSegmentedProgress()
                val minuteTarget = view.saMinute.getSegmentedProgress() * 5
                val timeTargetedInSec = hourTarget * 3600 + minuteTarget * 60
                val updateCategoryTarget = CategoryTarget(
                    id = editCategoryTarget.id,
                    category = editCategoryTarget.category,
                    hourTarget = hourTarget,
                    minuteTarget = minuteTarget,
                    totalTimeTargetInSec = timeTargetedInSec,
                    weekNumber = viewModel.getNumberWeek(weekStatus),
                    startDateTarget = viewModel.getStartDateWeek(weekStatus),
                    endDateTarget = viewModel.getEndDateWeek(weekStatus),
                    createAt = System.currentTimeMillis()
                )
                viewModel.updateCategoryTarget(updateCategoryTarget).observe(this) {
                    totalTimeTargetedInSec -= editCategoryTarget.totalTimeTargetInSec ?: 0
                    totalTimeTargetedInSec += updateCategoryTarget.totalTimeTargetInSec ?:0
                    categoryTargetAdapter.updateCategoryTarget(updateCategoryTarget)

                    viewModel.getChartValues(viewModel.getStartDateWeek(weekStatus),totalWeekUpTimeInSec).observe(this@AddTargetActivity) { list ->
                        listDataChart.clear()
                        listDataChart.addAll(list)
                        setDataChart(list);
                    }
                }
                bottomSheet.dismiss()
            }
            //endregion
            else {
                Toast.makeText(this,"Please select a category.",Toast.LENGTH_SHORT).show()
            }
        }

        bottomSheet.setCancelable(true)
        bottomSheet.setContentView(view)
        bottomSheet.setCanceledOnTouchOutside(true)
        bottomSheet.window!!.setBackgroundDrawable(
            ColorDrawable(
                ContextCompat.getColor(
                    this,
                    R.color.transparent
                )
            )
        )
        bottomSheet.show()
    }

    private fun setupChartView() {
        pieChartView.setUsePercentValues(true)
        pieChartView.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChartView.dragDecelerationFrictionCoef = 0.95f

        pieChartView.centerText = generateCenterSpannableText()
        pieChartView.setOnChartValueSelectedListener(this)

        pieChartView.isDrawHoleEnabled = true
        pieChartView.setHoleColor(Color.TRANSPARENT)

        pieChartView.holeRadius = 54f
        pieChartView.transparentCircleRadius = 56f

        pieChartView.setDrawCenterText(true)

        pieChartView.rotationAngle = 0f
        pieChartView.isRotationEnabled = true
        pieChartView.isHighlightPerTapEnabled = true


        pieChartView.animateY(1400, Easing.EaseInOutQuad)

        val l: Legend = pieChartView.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

    }

    private fun showEditMaxSleepTimeInWeekDialog() {
        val dialog = Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Dialog)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.max_sleep_time_in_week_dialog)

        //region sleep time
        val sleepTimesList = arrayOf("5.0","5.5","6.0","6.5","7.0","7.5","8.0","8.5","9.0","9.5","10.0","10.5","11.0")
        dialog.npAmountSleep.maxValue = sleepTimesList.size -1
        dialog.npAmountSleep.minValue = 0
        dialog.npAmountSleep.displayedValues = sleepTimesList
        dialog.npAmountSleep.value = sleepTimesList.indexOf(sleepTime.toString())

        //endregion

        dialog.tvClose.setOnClickListener{dialog.dismiss()}
        dialog.tvConfirm.setOnClickListener {
            sleepTime = sleepTimesList[dialog.npAmountSleep.value].toFloat()
            totalWeekUpTimeInSec = viewModel.getTotalTimeLeftInWeek(sleepTime,weekStatus)
            viewModel.saveAmountSleepTimePerDay(sleepTime)
            pieChartView.centerText = generateCenterSpannableText()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setDataChart(values: List<ChartValue>) {
        val entries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (value in values) {
            entries.add(
                PieEntry(
                    value.value
                )
            )
            colors.add(value.color)
        }
        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f


        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.WHITE)
//        data.setValueTypeface(tfLight)
        pieChartView.data = data

        pieChartView.description = null
        // undo all highlights
        pieChartView.highlightValues(null)
        pieChartView.invalidate()
    }

    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("${sleepTime.toInt()*7} Hour\nSleep time this week \n Tab to change")
        s.setSpan(RelativeSizeSpan(1.9f), 0, sleepTime.toInt().toString().length + 6, 0)
        s.setSpan(ForegroundColorSpan(Color.WHITE), 0, sleepTime.toInt().toString().length + 6 , 0)
        s.setSpan(StyleSpan(Typeface.BOLD), 0, sleepTime.toInt().toString().length + 6, 0)
        s.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.grayNormal)), sleepTime.toInt().toString().length + 6, s.length, 0)
        s.setSpan(RelativeSizeSpan(0.9f), sleepTime.toInt().toString().length + 6, s.length, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), sleepTime.toInt().toString().length + 6, s.length, 0)
        return s
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        showEditMaxSleepTimeInWeekDialog()
    }

    override fun onNothingSelected() {
    }

}