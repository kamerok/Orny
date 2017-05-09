package com.kamer.orny.interaction

import com.kamer.orny.data.model.Author
import io.reactivex.Single


interface GetAuthorsInteractor {

    fun getAuthors(): Single<List<Author>>

}