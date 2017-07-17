package com.kamer.orny.interaction.addexpense

import com.kamer.orny.data.domain.model.Author
import io.reactivex.Single


interface GetAuthorsInteractor {

    fun getAuthors(): Single<List<Author>>

}