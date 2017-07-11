package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.data.android.*
import com.kamer.orny.data.domain.*
import com.kamer.orny.data.domain.mapper.ExpenseMapper
import com.kamer.orny.data.domain.mapper.ExpenseMapperImpl
import com.kamer.orny.data.google.*
import com.kamer.orny.utils.Prefs
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    //Android

    @Provides
    @ApplicationScope
    fun provideActivityHolderImpl() = ActivityHolderImpl()

    @Provides
    @ApplicationScope
    fun provideActivityHolder(activityHolderImpl: ActivityHolderImpl): ActivityHolder = activityHolderImpl

    @Provides
    @ApplicationScope
    fun provideActivityHolderSetter(activityHolderImpl: ActivityHolderImpl): ActivityHolderSetter = activityHolderImpl

    @Provides
    @ApplicationScope
    fun provideReactiveActivities(activityHolder: ActivityHolder): ReactiveActivities
            = ReactiveActivitiesImpl(activityHolder)

    //Google

    @Provides
    @ApplicationScope
    fun provideGoogleAuthHolder(context: Context, prefs: Prefs, activityHolder: ActivityHolder, reactiveActivities: ReactiveActivities): GoogleAuthHolder
            = GoogleAuthHolderImpl(context, prefs, activityHolder, reactiveActivities)

    @Provides
    @ApplicationScope
    fun provideGoogleRepo(googleAuthHolder: GoogleAuthHolder, reactiveActivities: ReactiveActivities): GoogleRepo
            = GoogleRepoImpl(googleAuthHolder, reactiveActivities)

    @Provides
    @ApplicationScope
    fun provideGooglePageHolder(googleRepo: GoogleRepo): GooglePageHolder = GooglePageHolderImpl(googleRepo)

    //Domain

    @Provides
    @ApplicationScope
    fun provideAuthRepo(googleAuthHolder: GoogleAuthHolder): AuthRepo = AuthRepoImpl(googleAuthHolder)

    @Provides
    @ApplicationScope
    fun providePageRepo(googlePageHolder: GooglePageHolder): PageRepo = PageRepoImpl(googlePageHolder)

    @Provides
    @ApplicationScope
    fun provideExpenseRepo(googlePageHolder: GooglePageHolder, googleRepo: GoogleRepo, pageRepo: PageRepo, expenseMapper: ExpenseMapper): ExpenseRepo
            = ExpenseRepoImpl(googlePageHolder, googleRepo, pageRepo, expenseMapper)

    @Provides
    @ApplicationScope
    fun provideSpreadsheetRepo(googleRepo: GoogleRepo): SpreadsheetRepo = SpreadsheetRepoImpl(googleRepo)

    //Mapping

    @Provides
    @ApplicationScope
    fun provideExpenseMapper(): ExpenseMapper = ExpenseMapperImpl()
}