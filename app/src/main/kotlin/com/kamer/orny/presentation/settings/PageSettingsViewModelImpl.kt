package com.kamer.orny.presentation.settings

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
    private var newSettings: PageSettings by Delegates.observable(PageSettings(0.0, Date().dayStart(), 0)) { _, _, _ ->
        updateSaveButtonState()
    }

    override val fieldsEditableStream = MutableLiveData<Boolean>()

    override val saveButtonEnabledStream = MutableLiveData<Boolean>()
    override val loadingProgressStream = MutableLiveData<Boolean>()
    override val savingProgressStream = MutableLiveData<Boolean>()
    override val pageSettingsStream = MutableLiveData<PageSettings>()
    override val showDatePickerStream = SingleLiveEvent<Date>()
    override val errorStream = SingleLiveEvent<String>()

    init {
        fieldsEditableStream.value = false
        saveButtonEnabledStream.value = false
        savingProgressStream.value = false
        getInteractor
                .getSettings()
                .disposeOnDestroy()
                .doOnSubscribe { loadingProgressStream.value = true }
                .doFinally { loadingProgressStream.value = false }
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

    override fun budgetChanged(budget: String) {
        if (loadedSettings == null) {
            errorStream.value = errorParser.getMessage(GetSettingsException("Edit budget before loading"))
            return
        }
        val parsedBudget = if (budget.isEmpty()) 0.0 else budget.toDoubleOrNull()
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
        val parsedPeriod = if (period.isEmpty()) 0 else period.toIntOrNull()
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
        showDatePickerStream.value = newSettings.startDate
    }

    private fun updateSaveButtonState() {
        saveButtonEnabledStream.value = loadedSettings != null && newSettings != loadedSettings
    }

    override fun saveSettings() {
        saveInteractor
                .saveSettings(newSettings)
                .disposeOnDestroy()
                .doOnSubscribe {
                    savingProgressStream.value = true
                    fieldsEditableStream.value = false
                }
                .doFinally {
                    savingProgressStream.value = false
                    fieldsEditableStream.value = true
                }
                .subscribe({
                    loadedSettings = newSettings
                    updateSaveButtonState()
                }, {
                    errorStream.value = errorParser.getMessage(SaveSettingsException(it))
                })
    }
}