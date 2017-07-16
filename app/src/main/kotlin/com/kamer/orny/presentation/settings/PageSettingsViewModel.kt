package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.presentation.core.SingleLiveEvent
import java.util.*


interface PageSettingsViewModel {

    val fieldsEditableStream: LiveData<Boolean>
    val saveButtonEnabledStream: LiveData<Boolean>
    val loadingProgressStream: LiveData<Boolean>
    val savingProgressStream: LiveData<Boolean>
    val pageSettingsStream: LiveData<PageSettings>
    val showDatePickerStream: SingleLiveEvent<Date>
    val errorStream: SingleLiveEvent<String>

    fun budgetChanged(budget: String)
    fun startDateChanged(date: Date)
    fun periodChanged(period: String)
    fun selectDate()
    fun saveSettings()

}