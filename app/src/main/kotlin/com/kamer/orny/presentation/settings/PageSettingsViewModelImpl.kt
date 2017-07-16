package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.interaction.GetPageSettingsInteractor
import com.kamer.orny.interaction.SavePageSettingsInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.SingleLiveEvent
import com.kamer.orny.presentation.settings.errors.GetSettingsException
import com.kamer.orny.presentation.settings.errors.SaveSettingsException
import com.kamer.orny.presentation.settings.errors.WrongBudgetFormatException
import com.kamer.orny.presentation.settings.errors.WrongPeriodFormatException
import com.kamer.orny.utils.dayStart
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates


class PageSettingsViewModelImpl @Inject constructor(
        val errorParser: ErrorMessageParser,
        getInteractor: GetPageSettingsInteractor,
        val saveInteractor: SavePageSettingsInteractor
) : BaseViewModel(), PageSettingsViewModel {

    private var loadedSettings: PageSettings? = null
    private var newSettings: PageSettings by Delegates.observable(PageSettings(0.0, Date().dayStart(), 0)) { _, _, new ->
        saveEnabledStream.value = loadedSettings != null && new != loadedSettings
    }

    private val saveEnabledStream = MutableLiveData<Boolean>()
    private val fieldsEditableStream = MutableLiveData<Boolean>()
    private val loadingStream = MutableLiveData<Boolean>()
    private val savingStream = MutableLiveData<Boolean>()
    private val pageSettingsStream = MutableLiveData<PageSettings>()
    private val showPickerStream = SingleLiveEvent<Date>()
    private val errorStream = SingleLiveEvent<String>()

    init {
        fieldsEditableStream.value = false
        saveEnabledStream.value = false
        savingStream.value = false
        getInteractor
                .getSettings()
                .disposeOnDestroy()
                .doOnSubscribe { loadingStream.value = true }
                .doFinally { loadingStream.value = false }
                .doOnSuccess { fieldsEditableStream.value = true }
                .subscribe({
                    val settings = it.copy(startDate = it.startDate.dayStart())
                    pageSettingsStream.value = settings
                    loadedSettings = settings
                    newSettings = settings
                }, {
                    errorStream.value = errorParser.getMessage(GetSettingsException(it))
                })
    }

    override fun bindFieldsEditable(): LiveData<Boolean> = fieldsEditableStream

    override fun bindSaveButtonEnabled(): LiveData<Boolean> = saveEnabledStream

    override fun bindLoadingProgress(): LiveData<Boolean> = loadingStream

    override fun bindSavingProgress(): LiveData<Boolean> = savingStream

    override fun bindPageSettings(): LiveData<PageSettings> = pageSettingsStream

    override fun bindShowDatePicker(): SingleLiveEvent<Date> = showPickerStream

    override fun bindError(): SingleLiveEvent<String> = errorStream

    override fun budgetChanged(budget: String) {
        if (loadedSettings == null) {
            errorStream.value = errorParser.getMessage(GetSettingsException("Edit budget before loading"))
            return
        }
        val parsedBudget = budget.toDoubleOrNull()
        when {
            parsedBudget == null -> errorStream.value = errorParser.getMessage(WrongBudgetFormatException("Budget not a number"))
            parsedBudget < 0 -> errorStream.value = errorParser.getMessage(WrongBudgetFormatException("Budget can't be negative"))
            else -> newSettings = newSettings.copy(budget = parsedBudget)
        }
    }

    override fun startDateChanged(date: Date) {
        if (loadedSettings == null) {
            errorStream.value = errorParser.getMessage(GetSettingsException("Edit date before loading"))
            return
        }
        newSettings = newSettings.copy(startDate = date.dayStart())
        pageSettingsStream.value = newSettings
    }

    override fun periodChanged(period: String) {
        if (loadedSettings == null) {
            errorStream.value = errorParser.getMessage(GetSettingsException("Edit period before loading"))
            return
        }
        val parsedPeriod = period.toIntOrNull()
        when {
            parsedPeriod == null -> errorStream.value = errorParser.getMessage(WrongPeriodFormatException("Period not a number"))
            parsedPeriod < 0 -> errorStream.value = errorParser.getMessage(WrongPeriodFormatException("Period can't be negative"))
            else -> newSettings = newSettings.copy(period = parsedPeriod)
        }
    }

    override fun selectDate() {
        if (loadedSettings == null) {
            errorStream.value = errorParser.getMessage(GetSettingsException("Select date before loading"))
            return
        }
        showPickerStream.value = newSettings.startDate
    }

    override fun saveSettings() {
        saveInteractor
                .saveSettings(newSettings)
                .disposeOnDestroy()
                .doOnSubscribe {
                    savingStream.value = true
                    fieldsEditableStream.value = false
                }
                .doFinally {
                    savingStream.value = false
                    fieldsEditableStream.value = true
                }
                .subscribe({}, {
                    errorStream.value = errorParser.getMessage(SaveSettingsException(it))
                })
    }
}