package com.kamer.orny.interaction

import com.kamer.orny.data.ExpenseRepo
import com.kamer.orny.data.model.Expense
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SaveExpenseInteractorImpl(val expenseRepo: ExpenseRepo) : SaveExpenseInteractor {

    override fun saveExpense(expense: Expense): Completable = expenseRepo
            .saveExpense(expense)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())

}