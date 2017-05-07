package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.PrefsImpl
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {

    @Provides
    @ApplicationScope
    fun providePrefs(context: Context): Prefs = PrefsImpl(context)

}