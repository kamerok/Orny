package com.kamer.orny.interaction.addexpense

import com.kamer.orny.data.domain.ExpenseRepo
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.domain.model.NewExpense
import com.kamer.orny.interaction.common.GetAuthorsWithDefaultSingleUseCase
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class AddExpenseInteractorImpl @Inject constructor(
        val expenseRepo: ExpenseRepo,
        val getAuthorsUseCase: GetAuthorsWithDefaultSingleUseCase
) : AddExpenseInteractor {

    override fun getAuthorsWithDefault(): Single<AuthorsWithDefault> = getAuthorsUseCase.get()

    override fun createExpense(expense: NewExpense): Completable = expenseRepo
            .addExpense(Expense(
                    id = Expense.NO_ID,
                    comment = expense.comment,
                    date = expense.date,
                    isOffBudget = expense.isOffBudget,
                    values = mapOf(expense.author to expense.amount)
            ))
            .defaultBackgroundSchedulers()

}