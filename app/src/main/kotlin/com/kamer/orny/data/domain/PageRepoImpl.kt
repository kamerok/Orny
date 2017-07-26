package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.data.google.GooglePageRepo
import com.kamer.orny.data.google.GoogleRepo
import com.kamer.orny.data.room.DatabaseGateway
import com.kamer.orny.data.room.entity.PageSettingsEntity
import com.kamer.orny.di.app.ApplicationScope
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


@ApplicationScope
class PageRepoImpl @Inject constructor(
        val googleRepo: GoogleRepo,
        val googlePageRepo: GooglePageRepo,
        val databaseGateway: DatabaseGateway
) : PageRepo {

    override fun updatePage(): Completable = googlePageRepo.updatePage()

    override fun getPageSettings(): Observable<PageSettings> =
            databaseGateway
                    .getPageSettings()
                    .map { it.toPageSettings() }

    override fun savePageSettings(pageSettings: PageSettings): Completable =
            googleRepo
                    .savePageSettings(pageSettings.budget, pageSettings.startDate, pageSettings.period)
                    .andThen(
                            databaseGateway
                                    .setPageSettings(PageSettingsEntity.fromPageSettings(pageSettings))
                    )

    override fun getPageAuthors(): Observable<List<Author>> =
            databaseGateway
                    .getAllAuthors()
                    .map { it.map { dbAuthor -> dbAuthor.toAuthor() } }

}