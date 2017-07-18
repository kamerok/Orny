package com.kamer.orny.interaction.common

import com.kamer.orny.interaction.model.AuthorsWithDefault
import io.reactivex.Single


interface GetAuthorsWithDefaultSingleUseCase {

    fun get(): Single<AuthorsWithDefault>

}