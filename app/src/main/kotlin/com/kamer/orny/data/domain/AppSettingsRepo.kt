package com.kamer.orny.data.domain

import com.kamer.orny.data.domain.model.Author
import io.reactivex.Completable
import io.reactivex.Observable


interface AppSettingsRepo {

    fun getDefaultAuthor(): Observable<Author>

    fun setDefaultAuthor(author: Author): Completable

}