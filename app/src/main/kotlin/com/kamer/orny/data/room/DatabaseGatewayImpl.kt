package com.kamer.orny.data.room

import com.kamer.orny.data.room.entity.*
import com.kamer.orny.data.room.query.ExpenseWithEntities
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


class DatabaseGatewayImpl @Inject constructor(
        val database: Database,
        val settingsDao: SettingsDao,
        val expenseDao: ExpenseDao,
        val authorDao: AuthorDao
) : DatabaseGateway {

    override fun getDefaultAuthor(): Observable<List<AuthorEntity>> =
            settingsDao
                    .getDefaultAuthor()
                    .toObservable()

    override fun setAppSettings(appSettingsEntity: AppSettingsEntity): Completable =
            Completable.fromAction {
                settingsDao.setAppSettings(appSettingsEntity)
            }

    override fun savePage(
            pageSettingsEntity: PageSettingsEntity,
            authors: List<AuthorEntity>,
            expenses: List<ExpenseEntity>,
            entries: List<ExpenseEntryEntity>
    ): Completable =
            Completable.fromAction {
                database.runInTransaction {
                    authorDao.deleteAllAuthors()
                    authorDao.insertAll(authors)
                    expenseDao.deleteAllExpenses()
                    expenseDao.insertAll(expenses)
                    expenseDao.deleteAllEntries()
                    expenseDao.insertAllEntries(entries)
                    settingsDao.setPageSettings(pageSettingsEntity)
                }
            }

    override fun getPageSettings(): Observable<PageSettingsEntity> =
            settingsDao
                    .getPageSettings()
                    .toObservable()

    override fun setPageSettings(pageSettingsEntity: PageSettingsEntity): Completable =
            Completable.fromAction {
                settingsDao.setPageSettings(pageSettingsEntity)
            }


    override fun addExpense(expense: ExpenseWithEntities): Completable =
            Completable.fromAction {
                database.runInTransaction {
                    expenseDao.insert(expense.expense)
                    expenseDao.insertAllEntries(expense.entries)
                }
            }

    override fun getAllExpensesWithEntities(): Observable<List<ExpenseWithEntities>> =
            expenseDao
                    .getAllExpenses()
                    .toObservable()

    override fun getAllAuthors(): Observable<List<AuthorEntity>> =
            authorDao
                    .getAllAuthors()
                    .toObservable()
}