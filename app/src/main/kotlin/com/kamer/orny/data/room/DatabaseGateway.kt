package com.kamer.orny.data.room

import com.kamer.orny.data.room.entity.*
import com.kamer.orny.data.room.query.ExpenseWithEntities
import io.reactivex.Completable
import io.reactivex.Observable


interface DatabaseGateway {

    fun getDefaultAuthor(): Observable<List<AuthorEntity>>

    fun setAppSettings(appSettingsEntity: AppSettingsEntity): Completable

    fun savePage(
            pageSettingsEntity: PageSettingsEntity,
            authors: List<AuthorEntity>,
            expenses: List<ExpenseEntity>,
            entries: List<ExpenseEntryEntity>
    ): Completable

    fun getPageSettings(): Observable<PageSettingsEntity>

    fun setPageSettings(pageSettingsEntity: PageSettingsEntity): Completable

    fun addExpense(expense: ExpenseWithEntities): Completable

    fun getAllExpensesWithEntities(): Observable<List<ExpenseWithEntities>>

    fun getAllAuthors(): Observable<List<AuthorEntity>>

}