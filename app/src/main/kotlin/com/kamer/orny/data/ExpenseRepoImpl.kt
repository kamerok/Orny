package com.kamer.orny.data

import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.model.Expense
import io.reactivex.Completable


class ExpenseRepoImpl(val googleRepo: GoogleRepo) : ExpenseRepo {

    override fun saveExpense(expense: Expense): Completable = googleRepo.addExpense(expense)

}