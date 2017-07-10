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


class GetStatisticsInteractorImpl(val pageRepo: PageRepo, val expenseRepo: ExpenseRepo) : GetStatisticsInteractor {

    override fun getStatistics(): Observable<Statistics> = Observable
            .zip(pageRepo.getPageSettings(), pageRepo.getPageAuthors(), expenseRepo.getAllExpenses(), Function3 {
                (budget, startDate, period), authors, expenses ->
                val daysDifference = calculateDayDifference(startDate)
                var spendTotal = 0.0
                var budgetSpendTotal = 0.0
                var budgetSpendToday = 0.0
                val usersStatistics = authors
                        .map { (id, _, name) -> id to UserStatistics(name, 0.0, 0.0, 0.0) }
                        .toMap().toMutableMap()
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
                val budgetLeft = budget - budgetSpendTotal
                val averageSpendPerDay = budget / period
                val averageSpendPerDayAccordingBudgetLeft
                        = (budget - (budgetSpendTotal - budgetSpendToday)) / (period - daysDifference)
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
                val currentDay = (daysDifference + 1).coerceIn(0, period)
                Statistics(
                        daysTotal = period,
                        currentDay = currentDay,
                        budgetLimit = budget,
                        budgetLeft = budgetLeft.coerceAtLeast(0.0),
                        spendTotal = spendTotal,
                        budgetSpendTotal = budgetSpendTotal,
                        offBudgetSpendTotal = spendTotal - budgetSpendTotal,
                        budgetDifference = currentDay * averageSpendPerDay - budgetSpendTotal,
                        toSpendToday = (averageSpendPerDayAccordingBudgetLeft - budgetSpendToday).coerceAtLeast(0.0),
                        averageSpendPerDay = averageSpendPerDay,
                        averageSpendPerDayAccordingBudgetLeft = averageSpendPerDayAccordingBudgetLeft.coerceAtLeast(0.0),
                        usersStatistics = usersStatistics.values.toList(),
                        debts = debts
                )
            })

    private fun calculateDayDifference(date: Date): Int {
        val today = Calendar.getInstance().dayStart()
        val dateCalendar = Calendar.getInstance().apply {
            timeInMillis = date.time
            dayStart()
        }
        return ((today.timeInMillis / DateUtils.DAY_IN_MILLIS) - (dateCalendar.timeInMillis / DateUtils.DAY_IN_MILLIS)).toInt()
    }

    private fun isToday(day: Date): Boolean {
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply { time = day }
        return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }
}