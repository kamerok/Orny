package com.kamer.orny.data

import com.kamer.orny.data.model.Expense
import io.reactivex.Completable


interface ExpenseRepo {

    fun saveExpense(expense: Expense): Completable

}