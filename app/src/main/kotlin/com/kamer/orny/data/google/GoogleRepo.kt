package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GoogleExpense
import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Completable
import io.reactivex.Single

interface GoogleRepo {

    fun getAllExpenses(): Single<List<GoogleExpense>>

    fun getPage(): Single<GooglePage>

    fun addExpense(googleExpense: GoogleExpense): Completable

}