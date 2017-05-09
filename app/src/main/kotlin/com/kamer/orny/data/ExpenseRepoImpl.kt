package com.kamer.orny.data

import com.kamer.orny.data.model.Expense
import io.reactivex.Completable


class ExpenseRepoImpl : ExpenseRepo {

    override fun saveExpense(expense: Expense): Completable = Completable.fromAction { Thread.sleep(2000) }
}