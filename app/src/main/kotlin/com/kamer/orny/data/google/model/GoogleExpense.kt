package com.kamer.orny.data.google.model

import java.util.*


data class GoogleExpense(val comment: String?, val date: String?, val isOffBudget: Boolean, val values: List<Double>)

fun GoogleExpense.toList(): MutableList<Any> {
    val dataRow: MutableList<Any> = ArrayList()
    dataRow.add(comment ?: "")
    dataRow.add(date ?: "")
    dataRow.add(if (isOffBudget) "1" else "")
    values.forEach { dataRow.add(if (it != 0.0) it.toString() else "") }
    return dataRow
}

fun MutableList<Any>.toExpense(): GoogleExpense {
    val comment = this[0].toString()
    val date = this[1].toString()
    val isOffBudget = this[2].toString() == "1"
    val values = this
            .takeLast(size - 3)
            .map { it.toString() }
            .map { it.toDoubleOrNull() ?: 0.0 }
    return GoogleExpense(
            comment = comment,
            date = date,
            isOffBudget = isOffBudget,
            values = values
    )
}