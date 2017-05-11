package com.kamer.orny.interaction

import com.kamer.orny.data.PageRepo
import com.kamer.orny.data.model.Author
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class GetAuthorsInteractorImpl(val pageRepo: PageRepo) : GetAuthorsInteractor {

    override fun getAuthors(): Single<List<Author>> = pageRepo
            .getPageAuthors()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}