package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.android.ActivityHolderImpl
import com.kamer.orny.data.android.ReactiveActivities
import com.kamer.orny.data.android.ReactiveActivitiesImpl
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
    fun provideActivityHolder(): ActivityHolder = ActivityHolderImpl()

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
    fun provideExpenseRepo(googlePageHolder: GooglePageHolder, googleRepo: GoogleRepo, expenseMapper: ExpenseMapper): ExpenseRepo
            = ExpenseRepoImpl(googlePageHolder, googleRepo, expenseMapper)

    @Provides
    @ApplicationScope
    fun provideSpreadsheetRepo(googleRepo: GoogleRepo): SpreadsheetRepo = SpreadsheetRepoImpl(googleRepo)

    //Mapping

    @Provides
    @ApplicationScope
    fun provideExpenseMapper(): ExpenseMapper = ExpenseMapperImpl()
}