package com.kamer.orny.di.app

import android.arch.persistence.room.Room
import android.content.Context
import com.kamer.orny.data.android.*
import com.kamer.orny.data.domain.*
import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.mapper.ExpenseMapperImpl
import com.kamer.orny.data.google.GoogleAuthHolder
import com.kamer.orny.data.google.GoogleAuthHolderImpl
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.GoogleRepoImpl
import com.kamer.orny.data.room.*
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
abstract class DataModule {

    //Android

    @Binds
    @ApplicationScope
    abstract fun bindActivityHolder(holder: ActivityHolderImpl): ActivityHolder

    @Binds
    @ApplicationScope
    abstract fun bindActivityHolderSetter(holder: ActivityHolderImpl): ActivityHolderSetter

    @Binds
    @ApplicationScope
    abstract fun bindReactiveActivities(reactiveActivities: ReactiveActivitiesImpl): ReactiveActivities

    //Google

    @Binds
    @ApplicationScope
    abstract fun bindGoogleAuthHolder(authHolder: GoogleAuthHolderImpl): GoogleAuthHolder

    @Binds
    @ApplicationScope
    abstract fun bindGoogleRepo(googleRepo: GoogleRepoImpl): GoogleRepo

    //Room

    @Binds
    @ApplicationScope
    abstract fun bindDatabaseGateway(gateway: DatabaseGatewayImpl): DatabaseGateway

    @Module
    companion object {
        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideDatabase(context: Context): Database = Room
                .databaseBuilder(context, Database::class.java, "database")
                .build()

        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideExpenseDao(database: Database): ExpenseDao = database.expenseDao()

        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideAuthorDao(database: Database): AuthorDao = database.authorDao()

        @JvmStatic
        @Provides
        @ApplicationScope
        fun provideSettingsDao(database: Database): SettingsDao = database.settingsDao()
    }

    //Domain

    @Binds
    @ApplicationScope
    abstract fun bindAppSettingsRepo(repo: AppSettingsRepoImpl): AppSettingsRepo

    @Binds
    @ApplicationScope
    abstract fun bindAuthRepo(authRepo: AuthRepoImpl): AuthRepo

    @Binds
    @ApplicationScope
    abstract fun bindPageRepo(pageRepo: PageRepoImpl): PageRepo

    @Binds
    @ApplicationScope
    abstract fun bindExpenseRepo(expenseRepo: ExpenseRepoImpl): ExpenseRepo

    @Binds
    @ApplicationScope
    abstract fun bindSpreadsheetRepo(spreadsheetRepoImpl: SpreadsheetRepoImpl): SpreadsheetRepo

    //Mapping

    @Binds
    @ApplicationScope
    abstract fun bindExpenseMapper(expenseMapper: ExpenseMapperImpl): ExpenseMapper
}