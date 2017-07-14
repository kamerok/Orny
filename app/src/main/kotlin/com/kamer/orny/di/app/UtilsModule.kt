package com.kamer.orny.di.app

import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.ErrorMessageParserImpl
import com.kamer.orny.utils.Prefs
import com.kamer.orny.utils.PrefsImpl
import dagger.Binds
import dagger.Module

@Module
abstract class UtilsModule {

    @Binds
    @ApplicationScope
    abstract fun bindErrorParser(errorParser: ErrorMessageParserImpl): ErrorMessageParser

    @Binds
    @ApplicationScope
    abstract fun bindPrefs(prefs: PrefsImpl): Prefs

}