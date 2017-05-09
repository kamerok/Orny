package com.kamer.orny.interaction

import com.kamer.orny.data.PageRepo
import com.kamer.orny.data.model.Author
import io.reactivex.Single


class GetAuthorsInteractorImpl(val pageRepo: PageRepo) : GetAuthorsInteractor {

    override fun getAuthors(): Single<List<Author>> = pageRepo.getPageAuthors()
}