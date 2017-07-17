package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.DefaultAuthor
import io.reactivex.Completable
import io.reactivex.Single


interface AppSettingsInteractor {

    fun getDefaultAuthor(): Single<DefaultAuthor>

    fun saveDefaultAuthor(author: Author): Completable

}