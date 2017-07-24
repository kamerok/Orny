package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.room.SettingsDao
import com.kamer.orny.data.room.entity.AppSettingsEntity
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


class AppSettingsRepoImpl @Inject constructor(
        val settingsDao: SettingsDao
) : AppSettingsRepo {

    override fun getDefaultAuthor(): Observable<Author> =
            settingsDao
                    .getDefaultAuthor()
                    .toObservable()
                    .map {
                        if (it.isEmpty()) {
                            return@map Author.EMPTY_AUTHOR
                        } else {
                            return@map it.first().toAuthor()
                        }
                    }

    override fun setDefaultAuthor(author: Author): Completable =
            Completable
                    .fromAction {
                        settingsDao
                                .setAppSettings(AppSettingsEntity(defaultAuthorId = author.id))
                    }

}