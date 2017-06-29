package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GoogleRepo
import io.reactivex.Completable


class ExpenseRepoImpl(val googleRepo: GoogleRepo, val expenseMapper: ExpenseMapper) : ExpenseRepo {

    override fun saveExpense(expense: Expense): Completable = googleRepo.addExpense(expenseMapper.toGoogleExpense(expense))

}