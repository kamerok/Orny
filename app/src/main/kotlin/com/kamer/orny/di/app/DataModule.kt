package com.kamer.orny.di.app

import com.kamer.orny.data.android.*
import com.kamer.orny.data.domain.*
import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.mapper.ExpenseMapperImpl
import com.kamer.orny.data.google.*
import dagger.Binds
import dagger.Module

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

    @Binds
    @ApplicationScope
    abstract fun bindGooglePageHolder(googlePageHolder: GooglePageHolderImpl): GooglePageHolder

    //Domain

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