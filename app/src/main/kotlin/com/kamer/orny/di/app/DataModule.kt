package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.data.*
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.android.ActivityHolderImpl
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.GoogleRepoImpl
import com.kamer.orny.utils.Prefs
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    @ApplicationScope
    fun provideActivityHolder(): ActivityHolder = ActivityHolderImpl()

    @Provides
    @ApplicationScope
    fun provideGoogleRepo(context: Context, prefs: Prefs, activityHolder: ActivityHolder): GoogleRepo
            = GoogleRepoImpl(context, prefs, activityHolder)

    @Provides
    @ApplicationScope
    fun provideAuthRepo(googleRepo: GoogleRepo): AuthRepo = AuthRepoImpl(googleRepo)

    @Provides
    @ApplicationScope
    fun providePageRepo(): PageRepo = PageRepoImpl()

    @Provides
    @ApplicationScope
    fun provideExpenseRepo(googleRepo: GoogleRepo): ExpenseRepo = ExpenseRepoImpl(googleRepo)

    @Provides
    @ApplicationScope
    fun provideSpreadsheetRepo(googleRepo: GoogleRepo): SpreadsheetRepo = SpreadsheetRepoImpl(googleRepo)
}