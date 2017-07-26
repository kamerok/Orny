package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.room.ExpenseDao
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
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
                    .flatMapCompletable {
                        Completable.fromAction {
                            expenseDao.insert(ExpenseEntity(
                                    id = it,
                                    comment = expense.comment,
                                    date = expense.date,
                                    isOffBudget = expense.isOffBudget
                            ))
                            expenseDao.insertAllEntries(
                                    expense.values
                                            .map {
                                                ExpenseEntryEntity(
                                                        authorId = it.key.id,
                                                        expenseId = expense.id,
                                                        amount = it.value
                                                )
                                            }
                            )
                        }
                    }

    override fun getAllExpenses(): Observable<List<Expense>> =
            Observable.combineLatest(
                    expenseDao
                            .getAllExpenses()
                            .toObservable()
                            .doOnNext { Timber.d("$it") }
                            .map {
                                it.map {
                                    GoogleExpense(
                                            id = it.expense.id,
                                            comment = it.expense.comment,
                                            date = it.expense.date,
                                            isOffBudget = it.expense.isOffBudget,
                                            values = it.entries.map { it.amount }
                                    )
                                }
                            },
                    pageRepo.getPageAuthors(),
                    BiFunction { expenses, authors ->
                        expenses.map { expenseMapper.toExpense(it, authors.sortedBy { it.position }) }
                    }
            )

}