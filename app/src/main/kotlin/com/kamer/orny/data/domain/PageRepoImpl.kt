package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageRepo
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.room.AuthorDao
import com.kamer.orny.data.room.SettingsDao
import com.kamer.orny.data.room.entity.DbPageSettings
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val googlePageRepo: GooglePageRepo,
        val authorDao: AuthorDao,
        val settingsDao: SettingsDao
) : PageRepo {

    override fun updatePage(): Completable = googlePageRepo.updatePage()

    override fun getPageSettings(): Observable<PageSettings> =
            settingsDao
                    .getPageSettings()
                    .toObservable()
                    .doOnNext { Timber.d(it.toString()) }
                    .map { it.toPageSettings() }

    override fun savePageSettings(pageSettings: PageSettings): Completable =
            googleRepo
                    .savePageSettings(pageSettings.budget, pageSettings.startDate, pageSettings.period)
                    .doOnComplete {
                        settingsDao.setPageSettings(DbPageSettings.fromPageSettings(pageSettings))
                    }

    override fun getPageAuthors(): Observable<List<Author>> =
            authorDao
                    .getAllAuthors()
                    .toObservable()
                    .doOnNext { Timber.d("authors: ${it.size}") }
                    .map { it.map { dbAuthor ->  dbAuthor.toAuthor() } }

}