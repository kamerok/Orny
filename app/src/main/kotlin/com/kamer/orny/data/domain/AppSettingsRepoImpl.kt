package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.room.DatabaseGateway
import com.kamer.orny.data.room.entity.AppSettingsEntity
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


class AppSettingsRepoImpl @Inject constructor(
        val databaseGateway: DatabaseGateway
) : AppSettingsRepo {

    override fun getDefaultAuthor(): Observable<Author> =
            databaseGateway
                    .getDefaultAuthor()
                    .map {
                        if (it.isEmpty()) {
                            return@map Author.EMPTY_AUTHOR
                        } else {
                            return@map it.first().toAuthor()
                        }
                    }

    override fun setDefaultAuthor(author: Author): Completable =
            databaseGateway
                    .setAppSettings(AppSettingsEntity(defaultAuthorId = author.id))

}