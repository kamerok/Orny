package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.PageRepo
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.utils.defaultBackgroundSchedulers
import io.reactivex.Single
import javax.inject.Inject


class GetPageSettingsInteractorImpl @Inject constructor(
        val pageRepo: PageRepo
) : GetPageSettingsInteractor {

    override fun getSettings(): Single<PageSettings> = pageRepo
            .getPageSettings()
            .defaultBackgroundSchedulers()
            .firstOrError()

}