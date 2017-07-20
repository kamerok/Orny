package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageRepo
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googlePageRepo: GooglePageRepo
) : PageRepo {

    override fun updatePage(): Completable = googlePageRepo.updatePage()

    override fun getPageSettings(): Observable<PageSettings> =
            googlePageRepo
                    .getPage()
                    .map { (budget, periodDays, startDate) -> PageSettings(budget, startDate, periodDays) }

    override fun savePageSettings(pageSettings: PageSettings): Completable =
            googlePageRepo
                    .savePageSettings(pageSettings.budget, pageSettings.startDate, pageSettings.period)

    override fun getPageAuthors(): Observable<List<Author>> =
            googlePageRepo
                    .getPage()
                    .map { page ->
                        val authorsNames = page.authors
                        val authors = mutableListOf<Author>()
                        authorsNames.indices.mapTo(authors) { Author("$it", it, authorsNames[it], "") }
                        return@map authors
                    }

}