package com.kamer.orny.data.domain.mapper

import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.model.GoogleExpense


interface ExpenseMapper {

    fun toGoogleExpense(expense: Expense): GoogleExpense

    fun toExpense(googleExpense: GoogleExpense): Expense

}