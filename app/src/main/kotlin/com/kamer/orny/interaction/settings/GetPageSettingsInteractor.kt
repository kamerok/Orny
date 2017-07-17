package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Single


interface GetPageSettingsInteractor {

    fun getSettings(): Single<PageSettings>

}