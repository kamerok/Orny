package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Completable
import io.reactivex.Observable


interface PageRepo {

    fun updatePage(): Completable

    fun getPageSettings(): Observable<PageSettings>

    fun savePageSettings(pageSettings: PageSettings): Completable

    fun getPageAuthors(): Observable<List<Author>>

}