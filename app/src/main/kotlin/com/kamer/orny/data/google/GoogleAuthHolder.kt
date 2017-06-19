package com.kamer.orny.data.google

import com.google.api.services.sheets.v4.Sheets
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


interface GoogleAuthHolder {

    fun isAuthorized(): Observable<Boolean>

    fun login(): Completable

    fun logout(): Completable

    fun getSheetsService(): Single<Sheets>

}