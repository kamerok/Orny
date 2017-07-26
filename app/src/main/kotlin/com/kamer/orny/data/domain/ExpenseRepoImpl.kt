package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.room.DatabaseGateway
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
import com.kamer.orny.data.room.query.ExpenseWithEntities
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import javax.inject.Inject


@ApplicationScope
class ExpenseRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val expenseMapper: ExpenseMapper,
        val databaseGateway: DatabaseGateway
) : ExpenseRepo {

    override fun addExpense(expense: Expense): Completable =
            googleRepo
                    .addExpense(expenseMapper.toGoogleExpense(expense))
                    .flatMapCompletable {
                        databaseGateway.addExpense(ExpenseWithEntities(
                                ExpenseEntity(
                                        id = it,
                                        comment = expense.comment,
                                        date = expense.date,
                                        isOffBudget = expense.isOffBudget
                                ),
                                expense.values
                                        .map { entry ->
                                            ExpenseEntryEntity(
                                                    authorId = entry.key.id,
                                                    expenseId = it,
                                                    amount = entry.value
                                            )
                                        }
                        ))
                    }

    override fun getAllExpenses(): Observable<List<Expense>> =
            Observable.combineLatest(
                    databaseGateway.getAllExpensesWithEntities(),
                    databaseGateway.getAllAuthors(),
                    BiFunction { expenses, authors ->
                        expenses.map { expenseMapper.toExpense(it, authors.associate { it.id to it }) }
                    }
            )

}