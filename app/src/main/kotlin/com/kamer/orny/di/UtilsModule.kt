package com.kamer.orny.di

import android.content.Context
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.PrefsImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

    @Provides
    @Singleton
    fun providePrefs(context: Context): Prefs = PrefsImpl(context)

}