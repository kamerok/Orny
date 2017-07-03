package com.kamer.orny.data.domain.mapper

import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.model.GoogleExpense
import java.util.*


class ExpenseMapperImpl : ExpenseMapper{

    override fun toGoogleExpense(expense: Expense): GoogleExpense {
        val values = mutableListOf<Double>()
        for ((key, value) in expense.values) {
            while (values.size < key.position + 1) {
                values.add(0.0)
            }
            values.add(key.position, value)
        }
        return GoogleExpense(
                comment = expense.comment,
                date = expense.date,
                isOffBudget = expense.isOffBudget,
                values = values
        )
    }

    override fun toExpense(googleExpense: GoogleExpense): Expense {
        //todo fix Expense model and author
        return Expense(
                comment = googleExpense.comment ?: "",
                isOffBudget = googleExpense.isOffBudget,
                date = googleExpense.date ?: Date()
        )
    }
}