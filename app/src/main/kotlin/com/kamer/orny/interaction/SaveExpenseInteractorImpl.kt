package com.kamer.orny.interaction

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable


class SaveExpenseInteractorImpl(val expenseRepo: ExpenseRepo) : SaveExpenseInteractor {

    override fun saveExpense(expense: Expense): Completable = expenseRepo
            .saveExpense(expense)
            .defaultBackgroundSchedulers()

}