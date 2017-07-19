package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject


@ApplicationScope
class GooglePageHolderImpl @Inject constructor(
        val googleRepo: GoogleRepo
) : GooglePageHolder {

    private val pageSubject = BehaviorSubject.create<GooglePage>()

    override fun getPage(): Observable<GooglePage> =
            if (pageSubject.hasValue()) {
                pageSubject
            } else {
                updatePage()
                        .andThen(pageSubject)
            }

    override fun updatePage(): Completable = googleRepo
            .getPage()
            .doOnSuccess { pageSubject.onNext(it) }
            .toCompletable()

}