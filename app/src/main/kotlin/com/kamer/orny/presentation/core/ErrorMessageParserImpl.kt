package com.kamer.orny.presentation.core


class ErrorMessageParserImpl : ErrorMessageParser {

    override fun getMessage(throwable: Throwable): String = throwable.message ?: ""

}