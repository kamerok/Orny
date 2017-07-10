package com.kamer.orny.interaction.model


data class UserStatistics(
        val authorName: String,
        val spentTotal: Double = 0.0,
        val budgetSpend: Double = 0.0,
        val offBudgetSpend: Double = 0.0
)