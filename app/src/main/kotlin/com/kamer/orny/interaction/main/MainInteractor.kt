package com.kamer.orny.interaction.main

import io.reactivex.Completable


interface MainInteractor {

    fun updatePage(): Completable

}