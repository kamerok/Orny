package com.kamer.orny.interaction.main

import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import javax.inject.Inject


class MainInteractorImpl @Inject constructor(
        val pageRepo: PageRepo
) : MainInteractor {

    override fun updatePage(): Completable = pageRepo.updatePage().defaultBackgroundSchedulers()

}