package com.kamer.orny.interaction.editexpense

import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Single
import javax.inject.Inject


class GetAuthorsInteractorImpl @Inject constructor(
        val pageRepo: PageRepo
) : GetAuthorsInteractor {

    override fun getAuthors(): Single<List<Author>> = pageRepo
            .getPageAuthors()
            .defaultBackgroundSchedulers()
            .first(listOf())
}