package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Expense
import io.reactivex.Completable


interface ExpenseRepo {

    fun saveExpense(expense: Expense): Completable

}