package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.PageSettings


class PageRepoImpl : PageRepo {

    override fun getPageSettings(): io.reactivex.Observable<PageSettings> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun savePageSettings(pageSettings: com.kamer.orny.data.domain.model.PageSettings): io.reactivex.Completable {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getPageAuthors(): io.reactivex.Single<List<Author>> =
            io.reactivex.Single
                    .just(listOf(
                            com.kamer.orny.data.domain.model.Author(id = "1", name = "Макс", color = "#ff5722"),
                            com.kamer.orny.data.domain.model.Author(id = "0", name = "Лена", color = "#8a2be2")))
}