package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Completable
import io.reactivex.Observable
import java.util.*


interface GooglePageRepo {

    fun getPage(): Observable<GooglePage>

    fun updatePage(): Completable

    fun addExpense(expense: GoogleExpense): Completable

    fun savePageSettings(budget: Double, startDate: Date, period: Int): Completable

}