package com.kamer.orny.data.domain.mapper

import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.model.GoogleExpense


class ExpenseMapperImpl : ExpenseMapper{

    override fun toGoogleExpense(expense: Expense): GoogleExpense {
        val values = mutableListOf<Double>()
        //todo fix author
        if (expense.author?.id == "1") {
            values.add(0.0)
        }
        values.add(expense.amount)
        return GoogleExpense(
                comment = expense.comment,
                date = expense.date,
                isOffBudget = expense.isOffBudget,
                values = values
        )
    }

    override fun toExpense(googleExpense: GoogleExpense): Expense {
        /*val date = try {
            DATE_FORMAT.parse(googleExpense.date)
        } catch (e: ParseException) {
            Date()
        }*/
        //todo fix Expense model and author
        return Expense(
                comment = googleExpense.comment ?: "",
//                date = date,
                isOffBudget = googleExpense.isOffBudget
        )
    }
}