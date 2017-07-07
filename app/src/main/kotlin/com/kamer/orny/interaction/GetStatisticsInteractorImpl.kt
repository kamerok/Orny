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
                val daysDifference = ((Date().time - startDate.time) / DateUtils.DAY_IN_MILLIS).toInt() + 1
                var spendTotal = 0.0
                var budgetSpendTotal = 0.0
                var budgetSpendToday = 0.0
                val today = Calendar.getInstance()
                expenses.forEach { expense ->
                    expense.values.forEach {
                        spendTotal += it.value
                        if (!expense.isOffBudget) {
                            budgetSpendTotal += it.value
                            val expenseDay = Calendar.getInstance().apply { time = expense.date }
                            if (isSameDay(expenseDay, today)) {
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

    private fun isSameDay(day1: Calendar, day2: Calendar)
            = (day1.get(Calendar.DAY_OF_YEAR) == day2.get(Calendar.DAY_OF_YEAR)
            && day1.get(Calendar.YEAR) == day2.get(Calendar.YEAR))
}