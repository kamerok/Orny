package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.data.*
import com.kamer.orny.data.android.ActivityHolder
import com.kamer.orny.data.android.ActivityHolderImpl
import com.kamer.orny.data.android.ReactiveActivities
import com.kamer.orny.data.android.ReactiveActivitiesImpl
import com.kamer.orny.data.google.GoogleAuthHolder
import com.kamer.orny.data.google.GoogleAuthHolderImpl
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
    fun provideReactiveActivities(activityHolder: ActivityHolder): ReactiveActivities
            = ReactiveActivitiesImpl(activityHolder)

    @Provides
    @ApplicationScope
    fun provideGoogleAuthHolder(context: Context, prefs: Prefs, activityHolder: ActivityHolder): GoogleAuthHolder
            = GoogleAuthHolderImpl(context, prefs, activityHolder)

    @Provides
    @ApplicationScope
    fun provideGoogleRepo(googleAuthHolder: GoogleAuthHolder, reactiveActivities: ReactiveActivities): GoogleRepo
            = GoogleRepoImpl(googleAuthHolder, reactiveActivities)

    @Provides
    @ApplicationScope
    fun provideAuthRepo(googleAuthHolder: GoogleAuthHolder): AuthRepo = AuthRepoImpl(googleAuthHolder)

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