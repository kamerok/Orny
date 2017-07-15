package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.presentation.core.SingleLiveEvent
import java.util.*


interface PageSettingsViewModel {

    fun bindSaveButtonEnabled(): LiveData<Boolean>
    fun bindLoadingProgress(): LiveData<Boolean>
    fun bindSavingProgress(): LiveData<Boolean>
    fun bindPageSettings(): LiveData<PageSettings>
    fun bindError(): SingleLiveEvent<String>

    fun budgetChanged(budget: String)
    fun startDateChanged(date: Date)
    fun periodChanged(period: String)
    fun saveSettings()

}