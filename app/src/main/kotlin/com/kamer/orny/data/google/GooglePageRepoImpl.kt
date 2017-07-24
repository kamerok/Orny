package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.data.room.AuthorDao
import com.kamer.orny.data.room.Database
import com.kamer.orny.data.room.ExpenseDao
import com.kamer.orny.data.room.SettingsDao
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.PageSettingsEntity
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.subjects.BehaviorSubject
import java.util.*
import javax.inject.Inject


@ApplicationScope
class GooglePageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val expenseDao: ExpenseDao,
        val authorDao: AuthorDao,
        val settingsDao: SettingsDao,
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

    override fun updatePage(): Completable = updateCompletable

    private fun savePageToDb(page: GooglePage) {
        database.runInTransaction {
            expenseDao.deleteAllExpenses()
            expenseDao.insertAll(page.expenses.map { (comment, date, isOffBudget, values) ->
                ExpenseEntity(
                        comment = comment.orEmpty(),
                        date = date ?: Date(),
                        isOffBudget = isOffBudget,
                        values = values
                )
            })
            authorDao.deleteAllAuthors()
            authorDao.insertAll(page.authors.mapIndexed { index, name ->
                AuthorEntity(
                        id = index.toString(),
                        position = index,
                        name = name,
                        color = ""
                )
            })
            settingsDao.setPageSettings(PageSettingsEntity(
                    budget = page.budget,
                    startDate = page.startDate,
                    period = page.periodDays
            ))
        }
    }

}
