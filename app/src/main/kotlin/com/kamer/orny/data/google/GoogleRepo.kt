package com.kamer.orny.data.google

import com.kamer.orny.data.model.Expense
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface GoogleRepo {

    fun isAuthorized(): Observable<Boolean>

    fun login(): Completable

    fun logout(): Completable

    fun getAllExpenses(): Single<List<Expense>>

    fun addExpense(expense: Expense): Completable

}