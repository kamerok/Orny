package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageHolder
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googlePageHolder: GooglePageHolder,
        val googleRepo: GoogleRepo
) : PageRepo {

    override fun getPageSettings(): Observable<PageSettings> = googlePageHolder
            .getPage()
            .map { (budget, periodDays, startDate) -> PageSettings(budget, startDate, periodDays) }

    override fun savePageSettings(pageSettings: PageSettings): Completable = googleRepo
            .savePageSettings(pageSettings.budget, pageSettings.startDate, pageSettings.period)

    override fun getPageAuthors(): Observable<List<Author>> = googlePageHolder
            .getPage()
            .map { page ->
                val authorsNames = page.authors
                val authors = mutableListOf<Author>()
                authorsNames.indices.mapTo(authors) { Author("$it", it, authorsNames[it], "") }
                return@map authors
            }

}