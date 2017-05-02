package com.kamer.orny.data

import io.reactivex.Completable
import io.reactivex.Observable

interface AuthRepo {

    fun isAuthorized(): Observable<Boolean>

    fun login(): Completable

    fun logout(): Completable

}