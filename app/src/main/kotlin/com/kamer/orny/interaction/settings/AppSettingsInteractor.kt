package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.AuthorsWithDefault
import io.reactivex.Completable
import io.reactivex.Single


interface AppSettingsInteractor {

    fun getAuthorsWithDefault(): Single<AuthorsWithDefault>

    fun saveDefaultAuthor(author: Author): Completable

}