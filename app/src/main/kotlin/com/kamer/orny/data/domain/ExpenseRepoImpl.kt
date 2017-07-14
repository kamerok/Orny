package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GooglePageHolder
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject


@ApplicationScope
class ExpenseRepoImpl @Inject constructor(
        val googlePageHolder: GooglePageHolder,
        val googleRepo: GoogleRepo,
        val pageRepo: PageRepo,
        val expenseMapper: ExpenseMapper
) : ExpenseRepo {

    override fun saveExpense(expense: Expense): Completable = googleRepo.addExpense(expenseMapper.toGoogleExpense(expense))

    override fun getAllExpenses(): Observable<List<Expense>> = googlePageHolder
            .getPage()
            .zipWith(pageRepo.getPageAuthors(), BiFunction { page, authors ->
                page.expenses.map { expenseMapper.toExpense(it, authors.sortedBy { it.position }) }
            })

}