package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.data.room.AuthorDao
import com.kamer.orny.data.room.Database
import com.kamer.orny.data.room.ExpenseDao
import com.kamer.orny.data.room.model.DbAuthor
import com.kamer.orny.data.room.model.DbExpense
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject


@ApplicationScope
class GooglePageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val expenseDao: ExpenseDao,
        val authorDao: AuthorDao,
        val database: Database
) : GooglePageRepo {

    private val pageSubject = BehaviorSubject.create<GooglePage>()

    private val updateCompletable by lazy {
        googleRepo
                .getPage()
                .doOnSuccess {
                    savePageToDb(it)
                }
                .toObservable()
                .share()
                .firstOrError()
                .doOnSuccess { pageSubject.onNext(it) }
                .toCompletable()
    }

    override fun getPage(): Observable<GooglePage> =
            if (pageSubject.hasValue()) {
                pageSubject
            } else {
                updateCompletable
                        .andThen(pageSubject)
            }

    override fun updatePage(): Completable = updateCompletable

    override fun savePageSettings(budget: Double, startDate: Date, period: Int): Completable =
            googleRepo
                    .savePageSettings(budget, startDate, period)
                    .andThen(pageSubject)
                    .firstElement()
                    .flatMapCompletable {
                        Completable
                                .fromAction {
                                    pageSubject.onNext(it.copy(
                                            budget = budget,
                                            startDate = startDate,
                                            periodDays = period
                                    ))
                                }
                    }

    private fun savePageToDb(page: GooglePage) {
        database.runInTransaction {
            expenseDao.deleteAllExpenses()
            expenseDao.insertAll(page.expenses.map { (comment, date, isOffBudget, values) ->
                DbExpense(
                        comment = comment.orEmpty(),
                        date = date ?: Date(),
                        isOffBudget = isOffBudget,
                        values = values
                )
            })
            authorDao.deleteAllAuthors()
            authorDao.insertAll(page.authors.mapIndexed { index, name ->
                DbAuthor(
                        id = index.toString(),
                        position = index,
                        name = name,
                        color = ""
                )
            })
        }
    }

}
