package com.amirhosseinhamidian.my.presenter.statisticScreen.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.Category
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.presenter.adapter.CategoryListAdapter
import com.amirhosseinhamidian.my.presenter.adapter.TaskStatisticsAdapter
import com.amirhosseinhamidian.my.presenter.statisticScreen.StatisticViewModel
import com.amirhosseinhamidian.my.utils.Constants
import com.amirhosseinhamidian.my.utils.Date
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_add_target.rvCategory
import kotlinx.android.synthetic.main.activity_add_target.spinnerWeek
import kotlinx.android.synthetic.main.activity_add_target.tvDatePeriod
import kotlinx.android.synthetic.main.activity_add_target.tvNumWeek
import kotlinx.android.synthetic.main.fragment_weekly_statistic.*


@AndroidEntryPoint
class WeeklyStatisticFragment : Fragment() {

    private val viewModel: StatisticViewModel by viewModels()
    private var weekStatus = Constants.CURRENT_WEEK
    private lateinit var categoryListAdapter: CategoryListAdapter
    private lateinit var taskStatisticAdapter: TaskStatisticsAdapter
    private var totalSec = 0
    private var categorySelected = Category.getAllCategoryItem()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_statistic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvDate.text = viewModel.getTodayDate()
        setupWeekSpinner()
        setupRecyclerviewTaskStatistics()

    }

    private fun setupRecyclerviewTaskStatistics() {
        (recyclerview.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        recyclerview.layoutManager = LinearLayoutManager(requireContext())
        taskStatisticAdapter = TaskStatisticsAdapter(requireContext(), arrayListOf())
        recyclerview.adapter = taskStatisticAdapter
        recyclerview.setHasFixedSize(true)
    }

    private fun setupRecyclerviewCategorySelect() {
        rvCategory.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        categoryListAdapter = CategoryListAdapter(requireContext(), arrayListOf(),true)
        rvCategory.adapter = categoryListAdapter
        viewModel.getCategoryList(weekStatus).observe(requireActivity()) { categoryList ->
            categoryListAdapter.add(categoryList)
            categoryListAdapter.selectCategory(categorySelected.name)
            categoryListAdapter.onItemClick = {
                categorySelected = it
                getBoxesData()
                getDetailTasksData()
            }
        }
    }

    private fun setupWeekSpinner() {
        val items = arrayOf("This week", "Last week", "2 week ago", "3 week ago")
        val adapter: ArrayAdapter<String> = ArrayAdapter(requireContext(),R.layout.spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerWeek.adapter = adapter
        spinnerWeek.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, v: View?, position: Int, id: Long) {
                when(position) {
                    0 -> setWeekDateView(Constants.CURRENT_WEEK)
                    1 -> setWeekDateView(Constants.LAST_WEEK)
                    2 -> setWeekDateView(Constants.LAST_TWO_WEEK)
                    3 -> setWeekDateView(Constants.LAST_THREE_WEEK)
                }

                getBoxesData()
                getDetailTasksData()
                setupRecyclerviewCategorySelect()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun getDetailTasksData() {
        viewModel.getTaskStatisticWeekly(weekStatus,categorySelected.name).observe(requireActivity()) {
            taskStatisticAdapter.add(it)
        }
    }

    private fun getBoxesData() {
        viewModel.getDetailsWeekly(categorySelected.name,weekStatus).observe(requireActivity()) { dailyDetails ->
            setupInfoBoxes(dailyDetails)
            viewModel.getTargetWeekly(weekStatus).observe(requireActivity()) { categoryTargetList ->
                if(categoryTargetList.isNotEmpty()) {
                    viewModel.getCategoryGradeWeekly(categoryTargetList,weekStatus, categorySelected.name).observe(requireActivity()) { grade ->
                        setupGradeBoxes(grade)
                    }
                    setupTotalTargetTimeBoxes(viewModel.getTotalTargetTime(categoryTargetList,categorySelected.name))
                } else {
                    llTargetBoxes.visibility = View.GONE
                }
            }
        }
    }

    private fun setupInfoBoxes(dailyDetails: List<DailyDetails>) {

        //region total tasks
        val taskIds = arrayListOf<Long>()
        dailyDetails.forEach {
            if (!taskIds.contains(it.taskId)) {
                taskIds.add(it.taskId)
            }
        }
        tvCountTasks.text = taskIds.size.toString()
        //endregion

        //region total time
        totalSec = 0
        dailyDetails.forEach {
            totalSec += it.time
        }
        tvTotalTime.text = Date.calculateTimeInHourMinuteFormat(totalSec)
        //endregion
    }

    private fun setupGradeBoxes(grade: Double) {
        llTargetBoxes.visibility = View.VISIBLE
        tvGrade.setTextColor(viewModel.getGradeTextColor(requireContext(),grade))
        tvGrade.text = String.format("%.1f",grade)+"%"

    }

    private fun setupTotalTargetTimeBoxes(totalTime: Int) {
        tvTotalTargetTime.text = Date.calculateTimeInHourMinuteFormat(totalTime)
    }

    @SuppressLint("SetTextI18n")
    private fun setWeekDateView(week: Int) {
        tvDatePeriod.text = viewModel.getStartDateWeek(week) + " - " + viewModel.getEndDateWeek(week)
        tvNumWeek.text = "Week " + viewModel.getNumberWeek(week)
        weekStatus = week
    }


}