package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Observable


interface PageRepo {

    fun getPageSettings(): Observable<PageSettings>

    fun getPageAuthors(): Observable<List<Author>>

}