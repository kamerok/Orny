package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Observable
import io.reactivex.Single


interface PageRepo {

    fun getPageSettings(): Observable<PageSettings>

    fun getPageAuthors(): Single<List<Author>>

}