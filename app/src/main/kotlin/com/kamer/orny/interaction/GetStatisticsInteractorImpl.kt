package com.kamer.orny.interaction

import android.text.format.DateUtils
import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.model.Debt
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.interaction.model.UserStatistics
import com.kamer.orny.utils.dayStart
import io.reactivex.Observable
import io.reactivex.functions.Function3
import java.util.*
import javax.inject.Inject


class GetStatisticsInteractorImpl @Inject constructor(
        val pageRepo: PageRepo,
        val expenseRepo: ExpenseRepo
) : GetStatisticsInteractor {

    override fun getStatistics(): Observable<Statistics> = Observable
            .zip(pageRepo.getPageSettings(), pageRepo.getPageAuthors(), expenseRepo.getAllExpenses(), Function3 {
                (budget, startDate, period), authors, expenses ->

                var spendTotal = 0.0
                var budgetSpendTotal = 0.0
                var budgetSpendToday = 0.0
                val usersStatistics = authors
                        .associate { (id, _, name) -> id to UserStatistics(name) }
                        .toMutableMap()
                expenses.forEach { (_, _, date, isOffBudget, values) ->
                    values.forEach {
                        val author = it.key
                        val spend = it.value
                        val budgetSpend = if (isOffBudget) 0.0 else spend
                        val offBudgetSpend = if (isOffBudget) spend else 0.0

                        spendTotal += spend

                        if (!isOffBudget) {
                            budgetSpendTotal += budgetSpend
                            if (isToday(date)) {
                                budgetSpendToday += budgetSpend
                            }
                        }

                        val userId = author.id
                        val oldStatistics = usersStatistics[userId]
                        if (oldStatistics != null) {
                            usersStatistics.put(userId, oldStatistics.copy(
                                    spentTotal = oldStatistics.spentTotal + spend,
                                    budgetSpend = oldStatistics.budgetSpend + budgetSpend,
                                    offBudgetSpend = oldStatistics.offBudgetSpend + offBudgetSpend
                            ))
                        }
                    }
                }

                val daysDifference = startDate.daysFromToday()
                val currentDay = (daysDifference.inc()).coerceIn(0, period)
                val budgetLeft = (budget - budgetSpendTotal).coerceAtLeast(0.0)
                val offBudgetSpendTotal = spendTotal - budgetSpendTotal
                val averageSpendPerDay = budget / period
                val budgetDifference = currentDay * averageSpendPerDay - budgetSpendTotal
                val daysLeft = period - daysDifference
                val yesterdayBudgetLeft = budget - (budgetSpendTotal - budgetSpendToday)
                val toSpendToday = (yesterdayBudgetLeft / daysLeft - budgetSpendToday).coerceAtLeast(0.0)
                val averageSpendPerDayAccordingBudgetLeft = (budgetLeft / daysLeft).coerceAtLeast(0.0)

                val debts = mutableListOf<Debt>()
                if (usersStatistics.size == 2) {
                    val statistics = usersStatistics.values.toList()
                    if (statistics[0].spentTotal > statistics[1].spentTotal) {
                        debts.add(Debt(statistics[1].authorName, statistics[0].authorName,
                                (statistics[0].spentTotal - statistics[1].spentTotal) / 2))
                    } else {
                        debts.add(Debt(statistics[0].authorName, statistics[1].authorName,
                                (statistics[1].spentTotal - statistics[0].spentTotal) / 2))
                    }
                }
                Statistics(
                        daysTotal = period,
                        currentDay = currentDay,
                        budgetLimit = budget,
                        budgetLeft = budgetLeft,
                        spendTotal = spendTotal,
                        budgetSpendTotal = budgetSpendTotal,
                        offBudgetSpendTotal = offBudgetSpendTotal,
                        budgetDifference = budgetDifference,
                        toSpendToday = toSpendToday,
                        averageSpendPerDay = averageSpendPerDay,
                        averageSpendPerDayAccordingBudgetLeft = averageSpendPerDayAccordingBudgetLeft,
                        usersStatistics = usersStatistics.values.toList(),
                        debts = debts
                )
            })

    private fun Date.daysFromToday(): Int {
        val today = Calendar.getInstance().dayStart()
        val dateCalendar = Calendar.getInstance().apply {
            timeInMillis = this@daysFromToday.time
            dayStart()
        }
        val todayDay = today.timeInMillis / DateUtils.DAY_IN_MILLIS
        val dateDay = dateCalendar.timeInMillis / DateUtils.DAY_IN_MILLIS
        return (todayDay - dateDay).toInt()
    }

    private fun isToday(day: Date): Boolean {
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply { time = day }
        return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }
}