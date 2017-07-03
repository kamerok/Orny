package com.kamer.orny.data.domain.model

import java.util.*


data class Expense(val id: String = NO_ID,
                   var comment: String = "",
                   var date: Date = Date(),
                   var isOffBudget: Boolean = false,
                   var values: Map<Author, Double> = emptyMap()) {

    companion object {
        const val NO_ID = "-1"
    }

}