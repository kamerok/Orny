package com.kamer.orny.di

import android.content.Context
import com.kamer.orny.data.AuthRepo
import com.kamer.orny.data.AuthRepoImpl
import com.kamer.orny.data.SpreadsheetRepo
import com.kamer.orny.data.SpreadsheetRepoImpl
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.google.GoogleRepoImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataModule {

    @Provides
    @Singleton
    fun provideAuthRepo(googleRepo: GoogleRepo): AuthRepo = AuthRepoImpl(googleRepo)

    @Provides
    @Singleton
    fun provideSpreadsheetRepo(googleRepo: GoogleRepo): SpreadsheetRepo = SpreadsheetRepoImpl(googleRepo)

    @Provides
    @Singleton
    fun provideGoogleRepo(context: Context): GoogleRepo = GoogleRepoImpl(context)
}