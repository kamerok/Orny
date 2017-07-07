package com.kamer.orny.interaction.model


data class Statistics(
        val daysTotal: Int,
        val currentDay: Int,
        val budgetLimit: Double,
        val budgetLeft: Double,
        val spendTotal: Double,
        val budgetSpendTotal: Double,
        val offBudgetSpendTotal: Double,
        val budgetDifference: Double,
        val toSpendToday: Double,
        val averageSpendPerDay: Double,
        val averageSpendPerDayAccordingBudgetLeft: Double,
        val usersStatistics: List<UserStatistics>,
        val debts: List<Debt>
)