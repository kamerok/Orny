package com.kamer.orny.interaction.model

import com.kamer.orny.data.domain.model.Author


data class UserStatistics(
        val author: Author,
        val spentTotal: Double,
        val budgetSpent: Double,
        val offBudgetSpent: Double
)