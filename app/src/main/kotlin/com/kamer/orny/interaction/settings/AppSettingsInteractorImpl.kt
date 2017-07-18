package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.AppSettingsRepo
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.common.GetAuthorsWithDefaultSingleUseCase
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class AppSettingsInteractorImpl @Inject constructor(
        val appSettingsRepo: AppSettingsRepo,
        val getAuthorsUserCase: GetAuthorsWithDefaultSingleUseCase
) : AppSettingsInteractor {

    override fun getAuthorsWithDefault(): Single<AuthorsWithDefault> = getAuthorsUserCase.get()

    override fun saveDefaultAuthor(author: Author): Completable = appSettingsRepo
            .setDefaultAuthor(author)
            .defaultBackgroundSchedulers()
}