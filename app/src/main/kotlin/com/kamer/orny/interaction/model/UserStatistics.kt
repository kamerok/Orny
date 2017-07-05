package com.kamer.orny.interaction.model


data class UserStatistics(
        val authorName: String,
        val spentTotal: Double,
        val budgetSpent: Double,
        val offBudgetSpent: Double
)