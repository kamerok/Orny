package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageHolder
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googlePageHolder: GooglePageHolder,
        val googleRepo: GoogleRepo
) : PageRepo {

    private val settingsSubject = PublishSubject.create<PageSettings>()

    private val settingsObservable by lazy {
        googlePageHolder
                .getPage()
                .map { (budget, periodDays, startDate) -> PageSettings(budget, startDate, periodDays) }
                .mergeWith(settingsSubject)
                .share()
                .replay(1)
                .autoConnect()
    }

    override fun updatePage(): Completable = googlePageHolder.updatePage()

    override fun getPageSettings(): Observable<PageSettings> = settingsObservable

    override fun savePageSettings(pageSettings: PageSettings): Completable = googleRepo
            .savePageSettings(pageSettings.budget, pageSettings.startDate, pageSettings.period)
            .doOnComplete {
                settingsSubject.onNext(pageSettings)
            }

    override fun getPageAuthors(): Observable<List<Author>> = googlePageHolder
            .getPage()
            .map { page ->
                val authorsNames = page.authors
                val authors = mutableListOf<Author>()
                authorsNames.indices.mapTo(authors) { Author("$it", it, authorsNames[it], "") }
                return@map authors
            }

}