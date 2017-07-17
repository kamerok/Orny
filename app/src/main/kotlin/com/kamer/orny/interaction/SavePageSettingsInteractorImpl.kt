package com.kamer.orny.interaction

import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import javax.inject.Inject


class SavePageSettingsInteractorImpl @Inject constructor(
        val pageRepo: PageRepo
) : SavePageSettingsInteractor {

    override fun saveSettings(settings: PageSettings): Completable = pageRepo
            .savePageSettings(settings)
            .defaultBackgroundSchedulers()

}