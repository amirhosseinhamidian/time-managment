package com.amirhosseinhamidian.my.presenter.addTargetScreed

import android.annotation.SuppressLint
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
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.CategoryTarget
import com.amirhosseinhamidian.my.presenter.adapter.CategoryListAdapter
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.hideKeyboard
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ssarcseekbar.app.segmented.SegmentedArc
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_target.*
import kotlinx.android.synthetic.main.bottom_sheet_add_category_target_time.view.*

@AndroidEntryPoint
class AddTargetActivity : AppCompatActivity() {
    private val viewModel: AddTargetViewModel by viewModels()
    private lateinit var categoryListAdapter: CategoryListAdapter
    private var weekStatus = Constants.CURRENT_WEEK
    private val freeTime = 112

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_target)
        setupChartView()
        fabAddCategory.setOnClickListener {
            showAddCategoryTargetTimeBottomSheet()
        }

        setWeekDateView(Constants.CURRENT_WEEK)
        setupWeekSpinner()

        setDataChart(listOf(30f,15.5f,16.25f));
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
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun setWeekDateView(week: Int) {
        tvDatePeriod.text = viewModel.getStartDateWeek(week) + " - " + viewModel.getEndDateWeek(week)
        tvNumWeek.text = "Week" + viewModel.getNumberWeek(week)
    }

    private fun showAddCategoryTargetTimeBottomSheet() {
        val bottomSheet = BottomSheetDialog(this, R.style.DialogStyle)
        val view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_add_category_target_time, null)

        //region select category

        //region setup suggestion recyclerview
        viewModel.getCategoryList().observe(this) { categoryList ->
            if (categoryList.isNotEmpty()) {
                view.rvSuggestion.visibility = View.VISIBLE
                view.rvSuggestion.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
                categoryListAdapter = CategoryListAdapter(this, categoryList as ArrayList<Category>)
                view.rvSuggestion.adapter = categoryListAdapter
                categoryListAdapter.onItemClick = { categorySelected ->
                    view.edtCategory.setText(categorySelected.name)
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
                        categoryListAdapter.resetSelectedCategory()
                        categoryListAdapter.selectCategory(text.toString())
                    } else {
                        view.ivAddCategory.visibility = View.VISIBLE
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
                viewModel.addNewCategory(view.edtCategory.text.toString()).observe(this) {
                    if (it >=0) {
                        view.tvErrorCategory.visibility = View.INVISIBLE
                        view.pbAddCategory.visibility = View.INVISIBLE
                        view.tvErrorCategory.visibility = View.INVISIBLE
                        view.ivCategoryAdded.visibility = View.VISIBLE
                        val category = Category(id = it , name = view.edtCategory.text.toString())
                        categoryListAdapter.addToFirst(category)
                        categoryListAdapter.selectCategory(category.name)
                    } else {
                        view.tvErrorCategory.text = "Error saving new category, please choose another name."
                        view.tvErrorCategory.visibility = View.VISIBLE
                    }
                }
            } else {
                view.tvErrorCategory.text = "Please type new category title."
                view.tvErrorCategory.visibility = View.VISIBLE
            }
        }
        //endregion

        //endregion

        //region select time

        //region hour
        view.saHour.setMin(0)
        view.saHour.setMax(112)
        view.saHour.setSegmentedProgress(10)
        view.saHour.setOnProgressChangedListener(object : SegmentedArc.onProgressChangedListener {
            override fun onProgressChanged(progress: Int) {
                view.tvHour.text = "%02d".format(progress)
            }
        })
        //endregion

        //region minute

        //endregion
        var minute = 0
        view.saMinute.setMax(11)
        view.saMinute.setMin(0)
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

        view.btSave.setOnClickListener {
            if (categoryListAdapter.getCategorySelected().name.isNotEmpty()) {
                viewModel.saveCategoryTarget(CategoryTarget(
                    category = categoryListAdapter.getCategorySelected(),
                    hourTarget = view.saHour.getSegmentedProgress(),
                    minuteTarget = view.saMinute.getSegmentedProgress() * 5,
                    weekNumber = viewModel.getNumberWeek(weekStatus),
                    startDateTarget = viewModel.getStartDateWeek(weekStatus),
                    endDateTarget = viewModel.getEndDateWeek(weekStatus),
                    createAt = System.currentTimeMillis()
                ))
            } else {
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
        pieChartView.description.isEnabled = false
        pieChartView.setExtraOffsets(5f, 10f, 5f, 5f)

        pieChartView.dragDecelerationFrictionCoef = 0.95f

//        pieChartView.setCenterTextTypeface(tfLight)
        pieChartView.centerText = generateCenterSpannableText()

        pieChartView.isDrawHoleEnabled = true
        pieChartView.setHoleColor(Color.TRANSPARENT)

//        pieChartView.setTransparentCircleColor(Color.WHITE)
//        pieChartView.setTransparentCircleAlpha(110)

        pieChartView.holeRadius = 54f
        pieChartView.transparentCircleRadius = 56f

        pieChartView.setDrawCenterText(true)

        pieChartView.rotationAngle = 0f
        // enable rotation of the chart by touch
        // enable rotation of the chart by touch
        pieChartView.isRotationEnabled = true
        pieChartView.isHighlightPerTapEnabled = true

        // pieChartView.setUnit(" €");
        // pieChartView.setDrawUnitsInChart(true);

        // add a selection listener

        // pieChartView.setUnit(" €");
        // pieChartView.setDrawUnitsInChart(true);

        // add a selection listener
//        pieChartView.setOnChartValueSelectedListener(this)



        pieChartView.animateY(1400, Easing.EaseInOutQuad)
        // chart.spin(2000, 0, 360);

        // chart.spin(2000, 0, 360);
        val l: Legend = pieChartView.legend
        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        l.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        l.orientation = Legend.LegendOrientation.VERTICAL
        l.setDrawInside(false)
        l.xEntrySpace = 7f
        l.yEntrySpace = 0f
        l.yOffset = 0f

    }

    private fun setDataChart(values: List<Float>) {
        val entries: ArrayList<PieEntry> = ArrayList()

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (value in values) {
            entries.add(
                PieEntry(
                    value
                )
            )
        }
        val dataSet = PieDataSet(entries, "Election Results")
        dataSet.setDrawIcons(false)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors
        val colors: ArrayList<Int> = ArrayList()
        colors.add(ColorTemplate.rgb("#512DA8"))
        colors.add(ColorTemplate.rgb("#1976D2"))
        colors.add(ColorTemplate.rgb("#C2185B"))
        dataSet.colors = colors
        //dataSet.setSelectionShift(0f);
        val data = PieData(dataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(18f)
        data.setValueTextColor(Color.WHITE)
//        data.setValueTypeface(tfLight)
        pieChartView.data = data

        // undo all highlights
        pieChartView.highlightValues(null)
        pieChartView.invalidate()
    }

    private fun generateCenterSpannableText(): SpannableString {
        val s = SpannableString("${freeTime}h Free\nTab to change")
        s.setSpan(RelativeSizeSpan(1.9f), 0, freeTime.toString().length + 6, 0)
        s.setSpan(ForegroundColorSpan(Color.WHITE), 0, freeTime.toString().length + 6 , 0)
        s.setSpan(StyleSpan(Typeface.BOLD), 0, freeTime.toString().length + 6, 0)
        s.setSpan(ForegroundColorSpan(ContextCompat.getColor(this,R.color.grayNormal)), freeTime.toString().length + 6, s.length, 0)
        s.setSpan(RelativeSizeSpan(.8f), freeTime.toString().length + 6, s.length, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), freeTime.toString().length + 6, s.length, 0)
        return s
    }

}