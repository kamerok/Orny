package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Completable
import io.reactivex.Observable


interface GooglePageRepo {

    fun getPage(): Observable<GooglePage>

    fun updatePage(): Completable

}