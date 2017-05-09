package com.kamer.orny.interaction

import com.kamer.orny.data.model.Expense
import io.reactivex.Completable


interface SaveExpenseInteractor {

    fun saveExpense(expense: Expense): Completable

}