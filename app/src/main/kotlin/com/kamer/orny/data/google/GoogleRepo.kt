package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Completable
import io.reactivex.Single
import java.util.*

interface GoogleRepo {

    fun getPage(): Single<GooglePage>

    fun savePageSettings(budget: Double, startDate: Date, period: Int): Completable

    fun addExpense(googleExpense: GoogleExpense): Completable

}