package com.kamer.orny.presentation.core

import android.arch.lifecycle.ViewModel
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable


abstract class BaseViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    protected fun <T> Single<T>.disposeOnDestroy(): Single<T> = doOnSubscribe { compositeDisposable.add(it) }
    protected fun <T> Observable<T>.disposeOnDestroy(): Observable<T> = doOnSubscribe { compositeDisposable.add(it) }
    protected fun Completable.disposeOnDestroy(): Completable = doOnSubscribe { compositeDisposable.add(it) }

    override fun onCleared() {
        compositeDisposable.dispose()
    }
}