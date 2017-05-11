package com.kamer.orny.data.google

import android.app.Activity
import android.content.Intent
import com.kamer.orny.data.model.Expense
import io.reactivex.Completable
import io.reactivex.Observable

interface GoogleRepo {

    fun setActivity(activity: Activity)

    fun passActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun isAuthorized(): Observable<Boolean>

    fun login(): Completable

    fun logout(): Completable

    fun addExpense(expense: Expense): Completable

}