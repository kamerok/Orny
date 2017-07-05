package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Expense
import io.reactivex.Completable
import io.reactivex.Observable


interface ExpenseRepo {

    fun saveExpense(expense: Expense): Completable

    fun getAllExpenses(): Observable<List<Expense>>

}