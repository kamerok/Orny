package com.kamer.orny.interaction

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.domain.model.NewExpense
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import javax.inject.Inject


class CreateExpenseInteractorImpl @Inject constructor(
        val expenseRepo: ExpenseRepo
) : CreateExpenseInteractor {

    override fun createExpense(expense: NewExpense): Completable = expenseRepo
            .saveExpense(Expense(
                    id = Expense.NO_ID,
                    comment = expense.comment,
                    date = expense.date,
                    isOffBudget = expense.isOffBudget,
                    values = mapOf(expense.author to expense.amount)
            ))
            .defaultBackgroundSchedulers()

}