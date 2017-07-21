package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*


interface GooglePageRepo {

    fun getPage(): Observable<GooglePage>

    fun updatePage(): Completable

    fun savePageSettings(budget: Double, startDate: Date, period: Int): Completable

}