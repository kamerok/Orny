package com.kamer.orny.data.google

import android.app.Activity
import android.content.Intent
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface GoogleRepo {

    fun setActivity(activity: Activity)

    fun passActivityResult(requestCode: Int, resultCode: Int, data: Intent?)

    fun isAuthorized(): Observable<Boolean>

    fun login(): Completable

    fun logout(): Completable

    fun getData(): Single<List<String>>

}