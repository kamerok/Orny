package com.kamer.orny.data.domain

import io.reactivex.Completable

interface AuthRepo {

//    fun isAuthorized(): Observable<Boolean>

    fun login(): io.reactivex.Completable

    fun logout(): io.reactivex.Completable

}