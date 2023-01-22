package com.amirhosseinhamidian.my.presenter.statisticScreen.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.domain.model.DailyDetails
import com.amirhosseinhamidian.my.presenter.adapter.CategoryListAdapter
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
    private var totalSec = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weekly_statistic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupWeekSpinner()
        setupRecyclerviewCategorySelect()

    }

    private fun setupRecyclerviewCategorySelect() {
        rvCategory.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        categoryListAdapter = CategoryListAdapter(requireContext(), arrayListOf())
        rvCategory.adapter = categoryListAdapter
        viewModel.getCategoryList().observe(requireActivity()) { categoryList ->
            categoryListAdapter.add(categoryList)
            categoryListAdapter.onItemClick = {

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

                viewModel.getDetailsWeekly(weekStatus).observe(requireActivity()) { dailyDetails ->
                    setupInfoBoxes(dailyDetails)
                    viewModel.getTargetWeekly(weekStatus).observe(requireActivity()) { categoryTargetList ->
                        if(categoryTargetList.isNotEmpty()) {
                            viewModel.getGradeWeekly(categoryTargetList,weekStatus).observe(requireActivity()) { grade ->
                                setupGradeBoxes(grade/categoryTargetList.size)
                            }
                            setupTotalTargetTimeBoxes(viewModel.getTotalTargetTime(categoryTargetList))
                        } else {
                            llTargetBoxes.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

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