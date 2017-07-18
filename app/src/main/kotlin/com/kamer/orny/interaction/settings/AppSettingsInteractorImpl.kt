package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.AppSettingsRepo
import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.model.AuthorsWithDefault
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import javax.inject.Inject


class AppSettingsInteractorImpl @Inject constructor(
        val pageRepo: PageRepo,
        val appSettingsRepo: AppSettingsRepo
) : AppSettingsInteractor {

    override fun getAuthorsWithDefault(): Single<AuthorsWithDefault> = Single
            .zip(
                    pageRepo
                            .getPageAuthors()
                            .defaultBackgroundSchedulers()
                            .firstOrError(),
                    appSettingsRepo
                            .getDefaultAuthor()
                            .defaultBackgroundSchedulers()
                            .firstOrError(),
                    BiFunction { authors, defaultAuthor ->
                        AuthorsWithDefault(
                                if (defaultAuthor == Author.EMPTY_AUTHOR) authors.first() else defaultAuthor,
                                authors
                        )
                    }
            )

    override fun saveDefaultAuthor(author: Author): Completable = appSettingsRepo
            .setDefaultAuthor(author)
            .defaultBackgroundSchedulers()
}