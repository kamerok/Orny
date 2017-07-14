package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Observable
import javax.inject.Inject


@ApplicationScope
class GooglePageHolderImpl @Inject constructor(
        val googleRepo: GoogleRepo
) : GooglePageHolder {

    private val pageSingle by lazy {
        googleRepo
                .getPage()
                .toObservable()
                .share()
                .replay()
                .autoConnect()
    }

    override fun getPage(): Observable<GooglePage> = pageSingle


}