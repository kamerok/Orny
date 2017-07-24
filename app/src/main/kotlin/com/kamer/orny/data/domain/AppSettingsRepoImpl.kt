package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.room.SettingsDao
import com.kamer.orny.data.room.entity.DbAppSettings
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import timber.log.Timber
import javax.inject.Inject


class AppSettingsRepoImpl @Inject constructor(
        val pageRepo: PageRepo,
        val settingsDao: SettingsDao
) : AppSettingsRepo {

    override fun getDefaultAuthor(): Observable<Author> =
            Observable.combineLatest(
                    pageRepo.getPageAuthors(),
                    settingsDao.getAppSettings().toObservable().doOnNext { Timber.d(it.toString()) },
                    BiFunction { authors, pageSettings ->
                        return@BiFunction if (pageSettings.defaultAuthorId.isEmpty()) {
                            Author.EMPTY_AUTHOR
                        } else {
                            authors.filter { it.id == pageSettings.defaultAuthorId }.first()
                        }
                    }
            )

    override fun setDefaultAuthor(author: Author): Completable =
            Completable
                    .fromAction {
                        settingsDao
                                .setAppSettings(DbAppSettings(defaultAuthorId = author.id))
                    }

}