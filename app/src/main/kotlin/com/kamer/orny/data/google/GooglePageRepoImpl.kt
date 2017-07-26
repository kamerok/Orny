package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.data.room.AuthorDao
import com.kamer.orny.data.room.Database
import com.kamer.orny.data.room.ExpenseDao
import com.kamer.orny.data.room.SettingsDao
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
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
            authorDao.deleteAllAuthors()
            authorDao.insertAll(page.authors.mapIndexed { index, name ->
                AuthorEntity(
                        id = index.toString(),
                        position = index,
                        name = name,
                        color = ""
                )
            })
            expenseDao.deleteAllExpenses()
            expenseDao.insertAll(page.expenses.map { (id, comment, date, isOffBudget) ->
                ExpenseEntity(
                        id = id,
                        comment = comment.orEmpty(),
                        date = date ?: Date(),
                        isOffBudget = isOffBudget
                )
            })
            val entries = mutableListOf<ExpenseEntryEntity>()
            page.expenses.forEach { expense ->
                entries.addAll(expense.values.mapIndexed { index, amount ->
                    ExpenseEntryEntity(
                            authorId = index.toString(),
                            expenseId = expense.id,
                            amount = amount
                    )
                })
            }
            expenseDao.deleteAllEntries()
            expenseDao.insertAllEntries(entries)
            settingsDao.setPageSettings(PageSettingsEntity(
                    budget = page.budget,
                    startDate = page.startDate,
                    period = page.periodDays
            ))
        }
    }

}
