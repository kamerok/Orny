package com.kamer.orny.utils

import io.reactivex.subjects.Subject

fun Subject<Any>.onNext() = this.onNext(Any())
