package com.kamer.orny.data

import com.kamer.orny.data.model.Author
import io.reactivex.Single


interface PageRepo {

    fun getPageAuthors(): Single<List<Author>>

}