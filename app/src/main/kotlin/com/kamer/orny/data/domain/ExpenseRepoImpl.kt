package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.room.ExpenseDao
import com.kamer.orny.data.room.model.DbExpense
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject


@ApplicationScope
class ExpenseRepoImpl @Inject constructor(
        val expenseDao: ExpenseDao,
        val googleRepo: GoogleRepo,
        val pageRepo: PageRepo,
        val expenseMapper: ExpenseMapper
) : ExpenseRepo {

    override fun addExpense(expense: Expense): Completable =
            googleRepo
                    .addExpense(expenseMapper.toGoogleExpense(expense))
                    .andThen(
                            Completable.fromAction {
                                expenseDao.insert(DbExpense(
                                        comment = expense.comment,
                                        date = expense.date,
                                        isOffBudget = expense.isOffBudget,
                                        values = expense.values.map { it.value }
                                ))
                            }
                    )

    override fun getAllExpenses(): Observable<List<Expense>> =
            Observable.combineLatest(
                    expenseDao
                            .getAllExpenses()
                            .toObservable()
                            .doOnNext { Timber.d("expenses ${it.size}") }
                            .map {
                                it.map {
                                    GoogleExpense(
                                            comment = it.comment,
                                            date = it.date,
                                            isOffBudget = it.isOffBudget,
                                            values = it.values)
                                }
                            },
                    pageRepo.getPageAuthors(),
                    BiFunction { expenses, authors ->
                        expenses.map { expenseMapper.toExpense(it, authors.sortedBy { it.position }) }
                    }
            )

}