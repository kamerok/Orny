package com.kamer.orny.utils

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.Subject

fun Subject<Any>.onNext() = this.onNext(Any())

fun <T> Observable<T>.defaultBackgroundSchedulers(): Observable<T> =
        this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

fun <T> Single<T>.defaultBackgroundSchedulers(): Single<T> =
        this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

fun Completable.defaultBackgroundSchedulers(): Completable =
        this
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
