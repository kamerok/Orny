package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.data.AuthRepo
import com.kamer.orny.data.AuthRepoImpl
import com.kamer.orny.data.SpreadsheetRepo
import com.kamer.orny.data.SpreadsheetRepoImpl
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.GoogleRepoImpl
import com.kamer.orny.utils.Prefs
import dagger.Module
import dagger.Provides

@Module
class DataModule {

    @Provides
    @ApplicationScope
    fun provideAuthRepo(googleRepo: GoogleRepo): AuthRepo = AuthRepoImpl(googleRepo)

    @Provides
    @ApplicationScope
    fun provideSpreadsheetRepo(googleRepo: GoogleRepo): SpreadsheetRepo = SpreadsheetRepoImpl(googleRepo)

    @Provides
    @ApplicationScope
    fun provideGoogleRepo(context: Context, prefs: Prefs): GoogleRepo = GoogleRepoImpl(context, prefs)
}