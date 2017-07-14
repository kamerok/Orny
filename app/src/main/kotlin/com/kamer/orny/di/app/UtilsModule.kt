package com.kamer.orny.di.app

import android.content.Context
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.ErrorMessageParserImpl
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.PrefsImpl
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {

    @Provides
    @ApplicationScope
    fun provideErrorParser(): ErrorMessageParser = ErrorMessageParserImpl()

    @Provides
    @ApplicationScope
    fun providePrefs(context: Context): Prefs = PrefsImpl(context)

}