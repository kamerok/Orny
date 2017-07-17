package com.kamer.orny.interaction.addexpense

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.domain.model.NewExpense
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class AddExpenseInteractorImpl @Inject constructor(
        val expenseRepo: ExpenseRepo,
        val pageRepo: PageRepo
) : AddExpenseInteractor {

    override fun getAuthors(): Single<List<Author>> = pageRepo
            .getPageAuthors()
            .defaultBackgroundSchedulers()
            .first(listOf())

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