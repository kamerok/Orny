package com.kamer.orny.data.domain.model

import java.util.*


data class NewExpense(
        var comment: String = "",
        var date: Date = Date(),
        var isOffBudget: Boolean = false,
        var amount: Double = 0.0,
        var author: Author
)