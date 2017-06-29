package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings


interface PageRepo {

    fun getPageSettings(): io.reactivex.Observable<PageSettings>

    fun savePageSettings(pageSettings: com.kamer.orny.data.domain.model.PageSettings): io.reactivex.Completable

    fun getPageAuthors(): io.reactivex.Single<List<Author>>

}