package com.kamer.orny.data.model

import java.util.*


data class Expense(val id: String = "-1",
                   var amount: Double,
                   var comment: String,
                   var author: Author,
                   var date: Date,
                   var isOffBudget: Boolean)