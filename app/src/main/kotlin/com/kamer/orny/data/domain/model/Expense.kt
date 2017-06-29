package com.kamer.orny.data.domain.model

import java.util.*


data class Expense(val id: String = NO_ID,
                   var amount: Double = 0.0,
                   var comment: String = "",
                   var author: Author? = null,
                   var date: Date = Date(),
                   var isOffBudget: Boolean = false) {

    companion object {
        const val NO_ID = "-1"
    }

}