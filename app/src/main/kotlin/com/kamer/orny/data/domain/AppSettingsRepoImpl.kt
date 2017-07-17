package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject


class AppSettingsRepoImpl @Inject constructor() : AppSettingsRepo {

    override fun getDefaultAuthor(): Observable<Author> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setDefaultAuthor(author: Author): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}