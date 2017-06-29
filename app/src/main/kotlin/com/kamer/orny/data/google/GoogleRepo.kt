package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GoogleExpense
import io.reactivex.Completable
import io.reactivex.Single

interface GoogleRepo {

    fun getAllExpenses(): Single<List<GoogleExpense>>

    fun addExpense(googleExpense: GoogleExpense): Completable

}