package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.data.room.DatabaseGateway
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
import com.kamer.orny.data.room.entity.PageSettingsEntity
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val databaseGateway: DatabaseGateway
) : PageRepo {

    private val updateCompletable by lazy {
        googleRepo
                .getPage()
                .flatMapCompletable {
                    savePageToDb(it)
                }
                .andThen(Observable.just(""))
                .share()
                .firstOrError()
                .toCompletable()
    }

    override fun updatePage(): Completable = updateCompletable

    override fun getPageSettings(): Observable<PageSettings> =
            databaseGateway
                    .getPageSettings()
                    .map { it.toPageSettings() }

    override fun savePageSettings(pageSettings: PageSettings): Completable =
            googleRepo
                    .savePageSettings(pageSettings.budget, pageSettings.startDate, pageSettings.period)
                    .andThen(
                            databaseGateway
                                    .setPageSettings(PageSettingsEntity.fromPageSettings(pageSettings))
                    )

    override fun getPageAuthors(): Observable<List<Author>> =
            databaseGateway
                    .getAllAuthors()
                    .map { it.map { dbAuthor -> dbAuthor.toAuthor() } }

    private fun savePageToDb(page: GooglePage): Completable =
            databaseGateway
                    .savePage(
                            PageSettingsEntity(
                                    budget = page.budget,
                                    startDate = page.startDate,
                                    period = page.periodDays
                            ),
                            page.authors.mapIndexed { index, name ->
                                AuthorEntity(
                                        id = index.toString(),
                                        position = index,
                                        name = name,
                                        color = ""
                                )
                            },
                            page.expenses.map { (row, comment, date, isOffBudget) ->
                                ExpenseEntity(
                                        id = row.toString(),
                                        comment = comment.orEmpty(),
                                        date = date ?: Date(),
                                        isOffBudget = isOffBudget
                                )
                            },
                            mutableListOf<ExpenseEntryEntity>().apply {
                                page.expenses.forEach { expense ->
                                    addAll(expense.values
                                            .mapIndexed { index, amount ->
                                                ExpenseEntryEntity(
                                                        authorId = index.toString(),
                                                        expenseId = expense.row.toString(),
                                                        amount = amount
                                                )
                                            }
                                            .filter { it.amount != 0.0 }
                                    )
                                }
                            }
                    )

}