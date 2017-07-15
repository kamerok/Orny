package com.kamer.orny.presentation.settings.errors


class GetSettingsException : Exception {
    constructor(cause: Throwable) : super(cause)

    constructor(message: String) : super(message)
}