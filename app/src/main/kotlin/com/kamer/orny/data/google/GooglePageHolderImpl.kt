package com.kamer.orny.data.google

import com.kamer.orny.data.google.model.GooglePage
import io.reactivex.Observable


class GooglePageHolderImpl(val googleRepo: GoogleRepo) : GooglePageHolder {

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