package com.kamer.orny.presentation.core

import com.kamer.orny.di.app.ApplicationScope
import javax.inject.Inject


@ApplicationScope
class ErrorMessageParserImpl @Inject constructor() : ErrorMessageParser {

    override fun getMessage(throwable: Throwable): String = throwable.message ?: ""

}