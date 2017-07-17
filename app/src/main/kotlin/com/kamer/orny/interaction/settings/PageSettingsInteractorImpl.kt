package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject


class PageSettingsInteractorImpl @Inject constructor(
        val pageRepo: PageRepo
) : PageSettingsInteractor {

    override fun getSettings(): Single<PageSettings> = pageRepo
            .getPageSettings()
            .defaultBackgroundSchedulers()
            .firstOrError()

    override fun saveSettings(settings: PageSettings): Completable = pageRepo
            .savePageSettings(settings)
            .defaultBackgroundSchedulers()

}