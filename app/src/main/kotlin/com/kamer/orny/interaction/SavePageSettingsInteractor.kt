package com.kamer.orny.interaction

import com.kamer.orny.data.domain.model.PageSettings
import io.reactivex.Completable


interface SavePageSettingsInteractor {

    fun saveSettings(settings: PageSettings): Completable

}