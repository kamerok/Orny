package com.kamer.orny.interaction

import com.kamer.orny.data.ExpenseRepo
import com.kamer.orny.data.model.Expense
import io.reactivex.Completable


class SaveExpenseInteractorImpl(val expenseRepo: ExpenseRepo) : SaveExpenseInteractor {

    override fun saveExpense(expense: Expense): Completable = expenseRepo.saveExpense(expense)

}