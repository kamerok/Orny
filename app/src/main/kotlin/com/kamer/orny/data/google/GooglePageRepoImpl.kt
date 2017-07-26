package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.data.room.DatabaseGateway
import com.kamer.orny.data.room.entity.AuthorEntity
import com.kamer.orny.data.room.entity.ExpenseEntity
import com.kamer.orny.data.room.entity.ExpenseEntryEntity
import com.kamer.orny.data.room.entity.PageSettingsEntity
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import java.util.*
import javax.inject.Inject


@ApplicationScope
class GooglePageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val databaseGateway: DatabaseGateway
) : GooglePageRepo {

    private val updateCompletable by lazy {
        googleRepo
                .getPage()
                .flatMapCompletable {
                    savePageToDb(it)
                }
                .toObservable<Any>()
                .share()
                .firstOrError()
                .toCompletable()
    }

    override fun updatePage(): Completable = updateCompletable

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
                            page.expenses.map { (id, comment, date, isOffBudget) ->
                                ExpenseEntity(
                                        id = id,
                                        comment = comment.orEmpty(),
                                        date = date ?: Date(),
                                        isOffBudget = isOffBudget
                                )
                            },
                            mutableListOf<ExpenseEntryEntity>().apply {
                                page.expenses.forEach { expense ->
                                    addAll(expense.values.mapIndexed { index, amount ->
                                        ExpenseEntryEntity(
                                                authorId = index.toString(),
                                                expenseId = expense.id,
                                                amount = amount
                                        )
                                    })
                                }
                            }
                    )
}
