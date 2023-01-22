package com.amirhosseinhamidian.my.presenter.statisticScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amirhosseinhamidian.my.R
import com.amirhosseinhamidian.my.presenter.adapter.StatisticViewpagerAdapter
import com.amirhosseinhamidian.my.presenter.statisticScreen.fragments.DailyStatisticFragment
import com.amirhosseinhamidian.my.presenter.statisticScreen.fragments.MonthlyStatisticFragment
import com.amirhosseinhamidian.my.presenter.statisticScreen.fragments.WeeklyStatisticFragment
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_statistic.*

@AndroidEntryPoint
class StatisticActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)
        ivBack.setOnClickListener {
            onBackPressed()
        }

        val adapter = StatisticViewpagerAdapter(this)
        adapter.addFragment(DailyStatisticFragment(), "Daily")
        adapter.addFragment(WeeklyStatisticFragment(), "Weekly")
        adapter.addFragment(MonthlyStatisticFragment(), "Monthly")

        viewPager.adapter = adapter
        viewPager.currentItem = 0
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = adapter.getTabTitle(position)
        }.attach()

    }
}