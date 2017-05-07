package com.kamer.orny.di.app

import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.ErrorMessageParserImpl
import dagger.Module
import dagger.Provides

@Module
class PresentationModule {

    @Provides
    @ApplicationScope
    fun provideErrorParser(): ErrorMessageParser = ErrorMessageParserImpl()

}