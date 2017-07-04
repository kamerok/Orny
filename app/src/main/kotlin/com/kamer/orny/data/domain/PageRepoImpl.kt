package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageHolder
import io.reactivex.Observable
import io.reactivex.Single


class PageRepoImpl(val googlePageHolder: GooglePageHolder) : PageRepo {

    override fun getPageSettings(): Observable<PageSettings> = googlePageHolder
            .getPage()
            .map { (budget, periodDays, startDate) -> PageSettings(budget, startDate, periodDays) }

    override fun getPageAuthors(): Single<List<Author>> = googlePageHolder
            .getPage()
            .map { page ->
                val authorsNames = page.authors
                val authors = mutableListOf<Author>()
                authorsNames.indices.mapTo(authors) { Author("$it", it, authorsNames[it], "") }
                return@map authors
            }
            .first(mutableListOf<Author>())
            .map { it }

}