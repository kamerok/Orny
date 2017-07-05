package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GooglePageHolder
import com.kamer.orny.data.google.GoogleRepo
import io.reactivex.Completable
import io.reactivex.Observable


class ExpenseRepoImpl(val googlePageHolder: GooglePageHolder, val googleRepo: GoogleRepo, val expenseMapper: ExpenseMapper) : ExpenseRepo {

    override fun saveExpense(expense: Expense): Completable = googleRepo.addExpense(expenseMapper.toGoogleExpense(expense))

    override fun getAllExpenses(): Observable<List<Expense>> = googlePageHolder
            .getPage()
            .map { it.expenses.map { expenseMapper.toExpense(it) } }

}