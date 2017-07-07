package com.kamer.orny.interaction.model


data class Statistics(
        val daysTotal: Int,
        val currentDay: Int,
        val budgetLimit: Double,
        val budgetLeft: Double,
        val spentTotal: Double,
        val budgetSpentTotal: Double,
        val offBudgetSpentTotal: Double,
        val budgetDifference: Double,
        val toSpendToday: Double,
        val averageSpendPerDay: Double,
        val averageSpendPerDayAccordingBudgetLeft: Double,
        val usersStatistics: List<UserStatistics>,
        val debts: List<Debt>
)