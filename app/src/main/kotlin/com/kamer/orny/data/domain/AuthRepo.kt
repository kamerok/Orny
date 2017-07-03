package com.kamer.orny.data.domain

import io.reactivex.Completable

interface AuthRepo {

//    fun isAuthorized(): Observable<Boolean>

    fun login(): Completable

    fun logout(): Completable

}