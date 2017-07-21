package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageRepo
import com.kamer.orny.data.room.AuthorDao
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googlePageRepo: GooglePageRepo,
        val authorDao: AuthorDao
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
            authorDao
                    .getAllAuthors()
                    .toObservable()
                    .doOnNext { Timber.d("authors: ${it.size}") }
                    .map { it.map { Author(it.id, it.position, it.name, it.color) } }

}