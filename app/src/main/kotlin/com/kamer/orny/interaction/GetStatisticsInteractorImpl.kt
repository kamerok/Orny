package com.kamer.orny.interaction

import android.text.format.DateUtils
import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.interaction.model.Debt
import com.kamer.orny.interaction.model.Statistics
import com.kamer.orny.interaction.model.UserStatistics
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.*


class GetStatisticsInteractorImpl(val pageRepo: PageRepo, val expenseRepo: ExpenseRepo) : GetStatisticsInteractor {

    override fun getStatistics(): Observable<Statistics> = Observable
            .zip(pageRepo.getPageSettings(), expenseRepo.getAllExpenses(), BiFunction {
                (budget, startDate, period), expenses ->
                val daysDifference = calculateDayDifference(startDate)
                var spendTotal = 0.0
                var budgetSpendTotal = 0.0
                var budgetSpendToday = 0.0
                expenses.forEach { (_, _, date, isOffBudget, values) ->
                    values.forEach {
                        spendTotal += it.value
                        if (!isOffBudget) {
                            budgetSpendTotal += it.value
                            if (isToday(date)) {
                                budgetSpendToday += it.value
                            }
                        }
                    }
                }
                val budgetLeft = budget - budgetSpendTotal
                val averageSpendPerDay = budget / period
                val averageSpendPerDayAccordingBudgetLeft
                        = (budget - (budgetSpendTotal - budgetSpendToday)) / period
                Statistics(
                        daysTotal = period,
                        currentDay = daysDifference.coerceIn(0, period),
                        budgetLimit = budget,
                        budgetLeft = budgetLeft.coerceAtLeast(0.0),
                        spentTotal = spendTotal,
                        budgetSpentTotal = budgetSpendTotal,
                        offBudgetSpentTotal = spendTotal - budgetSpendTotal,
                        budgetDifference = daysDifference.coerceIn(0, period) * averageSpendPerDay - budgetSpendTotal,
                        toSpendToday = (averageSpendPerDayAccordingBudgetLeft - budgetSpendToday).coerceAtLeast(0.0),
                        averageSpendPerDay = averageSpendPerDay,
                        averageSpendPerDayAccordingBudgetLeft = averageSpendPerDayAccordingBudgetLeft.coerceAtLeast(0.0),
                        usersStatistics = listOf(
                                UserStatistics("Max", 100.0, 200.0, 10.00),
                                UserStatistics("Lena", 100.0, 200.0, 10.00)
                        ),
                        debts = listOf(Debt("Max", "Lena", 10.0)))
            })

    private fun calculateDayDifference(date: Date): Int {
        return ((Date().time / DateUtils.DAY_IN_MILLIS) - (date.time / DateUtils.DAY_IN_MILLIS)).toInt() + 1
    }

    private fun isToday(day: Date): Boolean {
        val today = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply { time = day }
        return calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
                && calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR)
    }
}