package com.kamer.orny.data.google

import io.reactivex.Completable


interface GooglePageRepo {

    fun updatePage(): Completable

}