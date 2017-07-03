package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single


class PageRepoImpl : PageRepo {

    override fun getPageSettings(): Observable<PageSettings> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePageSettings(pageSettings: PageSettings): Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPageAuthors(): Single<List<Author>> =
            Single
                    .just(listOf(
                            Author(id = "1", name = "Макс", color = "#ff5722"),
                            Author(id = "0", name = "Лена", color = "#8a2be2")))
}