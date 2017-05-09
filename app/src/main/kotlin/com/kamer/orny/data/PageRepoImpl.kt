package com.kamer.orny.data

import com.kamer.orny.data.model.Author
import io.reactivex.Single


class PageRepoImpl : PageRepo {

    override fun getPageAuthors(): Single<List<Author>> =
            Single
                    .just(listOf(
                            Author(id = "0", name = "Лена", color = "#8a2be2"),
                            Author(id = "1", name = "Макс", color = "#ff5722")))
}