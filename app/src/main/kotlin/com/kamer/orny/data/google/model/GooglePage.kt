package com.kamer.orny.data.google.model

import java.util.*


data class GooglePage(
        val budget: Double,
        val periodDays: Int,
        val startDate: Date,
        val authors: List<String>,
        val expenses: List<GoogleExpense> = emptyList()
)