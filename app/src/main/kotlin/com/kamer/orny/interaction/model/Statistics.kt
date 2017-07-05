package com.kamer.orny.interaction.model


data class Statistics(
    val daysTotal: Int = 0,
    val currentDay: Int = 0,
    val budgetLimit: Double = 0.0,
    val budgetLeft: Double = 0.0,
    val spentTotal: Double = 0.0,
    val budgetSpentTotal: Double = 0.0,
    val offBudgetSpentTotal: Double = 0.0,
    val budgetDifference: Double = 0.0,
    val toSpendToday: Double = 0.0,
    val averageSpendInMonth: Double = 0.0,
    val averageSpendInMonthAccordingBudgetLeft: Double = 0.0,
    val usersStatistics: List<UserStatistics> = emptyList(),
    val debts: List<Debt> = emptyList()
)