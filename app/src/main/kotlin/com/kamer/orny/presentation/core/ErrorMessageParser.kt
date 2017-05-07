package com.kamer.orny.presentation.core

interface ErrorMessageParser {

    fun getMessage(throwable: Throwable): String

}