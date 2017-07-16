package com.kamer.orny.presentation.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.content.ContextCompat
import com.kamer.orny.R
import com.kamer.orny.di.app.features.MainModule
import com.kamer.orny.di.app.features.StatisticsModule
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.presentation.core.BaseActivity
import com.kamer.orny.presentation.statistics.StatisticsViewModel
import com.kamer.orny.presentation.statistics.StatisticsViewModelImpl
import com.kamer.orny.utils.gone
import com.kamer.orny.utils.visible
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named


class MainActivity : BaseActivity() {

    @field:[Inject Named(StatisticsModule.STATISTICS)] lateinit var statisticsViewModelFactory: ViewModelProvider.Factory
    @field:[Inject Named(MainModule.MAIN)] lateinit var mainViewModelFactory: ViewModelProvider.Factory

    private lateinit var statisticsViewModel: StatisticsViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statisticsViewModel = ViewModelProviders.of(this, statisticsViewModelFactory).get(StatisticsViewModelImpl::class.java)
        mainViewModel = ViewModelProviders.of(this, mainViewModelFactory).get(MainViewModelImpl::class.java)
        initViews()
        bindViewModels()
    }

    private fun initViews() {
        loadingProgressView.gone()
        addExpenseView.setOnClickListener { mainViewModel.addExpense() }
        settingsView.setOnClickListener { mainViewModel.openSettings() }
    }

    private fun bindViewModels() {
        statisticsViewModel.showLoadingStream.observe(this, Observer { if (it != null) setLoading(it) })
        statisticsViewModel.statisticsStream.observe(this, Observer { if (it != null) updateStatistics(it) })
    }

    private fun setLoading(isLoading: Boolean) = loadingProgressView.run { if (isLoading) visible() else gone() }

    private fun updateStatistics(statistics: Statistics) {
        daysView.text = getString(R.string.statistics_days, statistics.currentDay, statistics.daysTotal)
        budgetLeftView.text = getString(R.string.statistics_budget, statistics.budgetLeft, statistics.budgetLimit)
        budgetDifferenceView.text = String.format("%.1f", statistics.budgetDifference)
        budgetDifferenceView.setBackgroundColor(ContextCompat.getColor(this,
                if (statistics.budgetDifference < 0) R.color.budget_negative else R.color.budget_positive))
        canSpendView.text = getString(R.string.statistics_can_spend, statistics.toSpendToday)
        averageSpendView.text = getString(R.string.statistics_can_spend_daily, statistics.averageSpendPerDayAccordingBudgetLeft, statistics.averageSpendPerDay)
        val firstUser = statistics.usersStatistics[0]
        firstUserView.text = getString(R.string.statistics_user_spent,
                firstUser.authorName, firstUser.budgetSpend, firstUser.offBudgetSpend, firstUser.spentTotal)
        val secondUser = statistics.usersStatistics[1]
        secondUserView.text = getString(R.string.statistics_user_spent,
                secondUser.authorName, secondUser.budgetSpend, secondUser.offBudgetSpend, secondUser.spentTotal)
        totalView.text = getString(R.string.statistics_total_spent, statistics.budgetSpendTotal, statistics.offBudgetSpendTotal, statistics.spendTotal)
        val debt = statistics.debts.first()
        debtView.text = getString(R.string.statistics_debt, debt.fromName, debt.amount, debt.toName)
    }

}
