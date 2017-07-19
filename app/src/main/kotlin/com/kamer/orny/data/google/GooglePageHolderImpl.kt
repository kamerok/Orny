package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject


@ApplicationScope
class GooglePageHolderImpl @Inject constructor(
        val googleRepo: GoogleRepo
) : GooglePageHolder {

    private val pageSubject = BehaviorSubject.create<GooglePage>()

    private val updateCompletable by lazy {
        googleRepo
                .getPage()
                .toObservable()
                .share()
                .flatMapSingle { Single.just(it) }
                .firstOrError()
                .doOnSuccess { pageSubject.onNext(it) }
                .toCompletable()
    }

    private val pageObservable by lazy {
        if (pageSubject.hasValue()) {
            pageSubject
        } else {
            updateCompletable
                    .andThen(pageSubject)
        }
                .share()
                .replay(1)
                .autoConnect()
    }


    override fun getPage(): Observable<GooglePage> = pageObservable

    override fun updatePage(): Completable = updateCompletable

}