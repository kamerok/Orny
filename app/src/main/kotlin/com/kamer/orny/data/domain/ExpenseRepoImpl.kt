package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GoogleRepo
import io.reactivex.Completable


class ExpenseRepoImpl(val googleRepo: GoogleRepo) : ExpenseRepo {

    override fun saveExpense(expense: Expense): Completable = googleRepo.addExpense(expense)

}