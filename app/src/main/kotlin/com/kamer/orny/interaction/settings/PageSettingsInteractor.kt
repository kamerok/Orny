package com.kamer.orny.interaction.settings

import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Completable
import io.reactivex.Single


interface PageSettingsInteractor {

    fun getSettings(): Single<PageSettings>

    fun saveSettings(settings: PageSettings): Completable

}