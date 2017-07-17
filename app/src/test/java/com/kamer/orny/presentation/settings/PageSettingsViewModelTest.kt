package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import android.text.format.DateUtils
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.interaction.settings.PageSettingsInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.settings.errors.GetSettingsException
import com.kamer.orny.presentation.settings.errors.SaveSettingsException
import com.kamer.orny.presentation.settings.errors.WrongBudgetFormatException
import com.kamer.orny.presentation.settings.errors.WrongPeriodFormatException
import com.kamer.orny.utils.TestUtils
import com.kamer.orny.utils.dayStart
import com.kamer.orny.utils.getResultValue
import com.kamer.orny.utils.getResultValues
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.verify
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.util.*
import kotlin.reflect.KClass

@RunWith(MockitoJUnitRunner::class)
class PageSettingsViewModelTest{

    private val PARSED_ERROR = "parsed error"

    @Mock lateinit var errorParser: ErrorMessageParser
    @Mock lateinit var interactor: PageSettingsInteractor

    private lateinit var viewModel: PageSettingsViewModel

    @Before
    fun setUp() {
        TestUtils.setupLiveDataExecutor()
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        initSettings()
        `when`(interactor.saveSettings(any())).thenReturn(Completable.complete())
    }

    @Test
    fun fieldsNotEditableOnStart() {
        `when`(interactor.getSettings()).thenReturn(Single.never())

        createViewModel()

        assertThat(viewModel.fieldsEditableStream.getResultValue()).isFalse()
    }

    @Test
    fun fieldsEditableWhenSettingsLoaded() {
        createViewModel()

        assertThat(viewModel.fieldsEditableStream.getResultValue()).isTrue()
    }

    @Test
    fun fieldsNotEditableWhenSettingsLoadingError() {
        `when`(interactor.getSettings()).thenReturn(Single.error(Exception()))

        createViewModel()

        assertThat(viewModel.fieldsEditableStream.getResultValue()).isFalse()
    }

    @Test
    fun fieldsNotEditableWhenSavingInProgress() {
        `when`(interactor.saveSettings(any())).thenReturn(Completable.never())

        createViewModel()
        viewModel.budgetChanged("1")
        viewModel.saveSettings()

        assertThat(viewModel.fieldsEditableStream.getResultValue()).isFalse()
    }

    @Test
    fun fieldsEditableAfterSaving() {
        createViewModel()
        viewModel.budgetChanged("1")
        viewModel.saveSettings()

        assertThat(viewModel.fieldsEditableStream.getResultValue()).isTrue()
    }

    @Test
    fun fieldsEditableAfterSavingError() {
        `when`(interactor.saveSettings(any())).thenReturn(Completable.error(Exception()))

        createViewModel()
        viewModel.budgetChanged("1")
        viewModel.saveSettings()

        assertThat(viewModel.fieldsEditableStream.getResultValue()).isTrue()
    }

    @Test
    fun hideProgressOnStart() {
        createViewModel()

        assertThat(viewModel.savingProgressStream.getResultValue()).isFalse()
    }

    @Test
    fun loadSettingsFromInteractor() {
        createViewModel()

        verify(interactor).getSettings()
    }

    @Test
    fun loadSettingsOnStart() {
        val pageSettings = PageSettings(0.0, Date().dayStart(), 0)
        initSettings(pageSettings)

        createViewModel()
        val result = viewModel.pageSettingsStream.getResultValue()

        assertThat(result).isEqualTo(pageSettings)
    }

    @Test
    fun showProgressOnStart() {
        val subject = PublishSubject.create<PageSettings>()
        `when`(interactor.getSettings()).thenReturn(subject.firstOrError())

        createViewModel()
        val results = viewModel.loadingProgressStream.getResultValues(2) { subject.onNext(PageSettings(0.0, Date(), 0)) }

        assertThat(results).containsExactly(true, false)
    }

    @Test
    fun showProgressOnStartWithError() {
        val subject = PublishSubject.create<PageSettings>()
        `when`(interactor.getSettings()).thenReturn(subject.firstOrError())

        createViewModel()
        val results = viewModel.loadingProgressStream.getResultValues(2) { subject.onError(Exception()) }

        assertThat(results).containsExactly(true, false)
    }

    @Test
    fun loadSettingsError() {
        `when`(interactor.getSettings()).thenReturn(Single.error(Exception()))

        createViewModel()
        val result = viewModel.pageSettingsStream.getResultValue()

        assertThat(result).isNull()
        assertError(viewModel.errorStream, GetSettingsException::class)

    }

    @Test
    fun saveDisabledByDefault() {
        createViewModel()
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun enableSaveWhenBudgetChanged() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("2")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isTrue()
    }

    @Test
    fun disableSaveWhenBudgetChangedOnSameValue() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("2")
        viewModel.budgetChanged("0")

        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun treatEmptyValuesAsZeros() {
        initSettings(budget = 1.0, period = 1)
        val captor = argumentCaptor<PageSettings>()

        createViewModel()
        viewModel.budgetChanged("")
        viewModel.periodChanged("")
        viewModel.saveSettings()

        verify(interactor).saveSettings(captor.capture())
        assertThat(captor.firstValue.budget).isZero()
        assertThat(captor.firstValue.period).isZero()
    }

    @Test
    fun showErrorWhenBudgetChangedBeforeSettingsLoaded() {
        `when`(interactor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.budgetChanged("2")

        assertError(viewModel.errorStream, GetSettingsException::class)
    }

    @Test
    fun notEnableSaveButtonWhenBudgetNotNumber() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("ad")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenBudgetNotNumber() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("ad")

        assertError(viewModel.errorStream, WrongBudgetFormatException::class)
    }

    @Test
    fun notEnableSaveButtonWhenBudgetNegative() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("-2")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenBudgetNegative() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("-2")

        assertError(viewModel.errorStream, WrongBudgetFormatException::class)
    }

    @Test
    fun enableSaveWhenStartDateChanged() {
        initSettings(startDate = Date())

        createViewModel()
        viewModel.startDateChanged(yesterday())
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isTrue()
    }

    @Test
    fun updateSettingsWhenDateChanged() {
        initSettings(startDate = Date())

        createViewModel()
        viewModel.startDateChanged(yesterday())
        val result = viewModel.pageSettingsStream.getResultValue()

        assertThat(result.startDate).isEqualTo(yesterday().dayStart())
    }

    @Test
    fun disableSaveWhenStartDateChangedOnSameValue() {
        initSettings(startDate = Date())

        createViewModel()
        viewModel.startDateChanged(yesterday())
        viewModel.startDateChanged(Date())
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenStartDateChangedBeforeSettingsLoaded() {
        `when`(interactor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.startDateChanged(yesterday())

        assertError(viewModel.errorStream, GetSettingsException::class)
    }

    @Test
    fun enableSaveWhenPeriodChanged() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("2")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isTrue()
    }

    @Test
    fun disableSaveWhenPeriodChangedOnSameValue() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("2")
        viewModel.periodChanged("0")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenPeriodChangedBeforeSettingsLoaded() {
        `when`(interactor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.periodChanged("1")

        assertError(viewModel.errorStream, GetSettingsException::class)
    }

    @Test
    fun notEnableSaveButtonWhenPeriodNotNumber() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("ad")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenPeriodNotNumber() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("ad")

        assertError(viewModel.errorStream, WrongPeriodFormatException::class)
    }

    @Test
    fun notEnableSaveButtonWhenPeriodNegative() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("-1")
        val result = viewModel.saveButtonEnabledStream.getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenPeriodNegative() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("-1")

        assertError(viewModel.errorStream, WrongPeriodFormatException::class)
    }

    @Test
    fun callInteractorOnSave() {
        val captor = argumentCaptor<PageSettings>()

        createViewModel()
        viewModel.budgetChanged("1")
        viewModel.saveSettings()

        verify(interactor).saveSettings(captor.capture())
        assertThat(captor.firstValue).isEqualTo(PageSettings(1.0, Date().dayStart(), 0))
    }

    @Test
    fun showSavingProgress() {
        createViewModel()
        viewModel.periodChanged("1")
        val results = viewModel.savingProgressStream.getResultValues(3) { viewModel.saveSettings() }

        assertThat(results).containsExactly(false, true, false)
    }

    @Test
    fun showSavingProgressError() {
        `when`(interactor.saveSettings(any())).thenReturn(Completable.error(Exception()))

        createViewModel()
        viewModel.periodChanged("1")
        viewModel.saveSettings()

        assertError(viewModel.errorStream, SaveSettingsException::class)
    }

    @Test
    fun newSettingsIsDefaultAfterSaving() {
        createViewModel()
        viewModel.periodChanged("1")
        viewModel.saveSettings()

        assertThat(viewModel.saveButtonEnabledStream.getResultValue()).isFalse()
    }

    @Test
    fun showPicker() {
        val startDate = yesterday().dayStart()
        initSettings(startDate = startDate)

        createViewModel()
        viewModel.selectDate()
        val result = viewModel.showDatePickerStream.getResultValue()

        assertThat(result).isEqualTo(startDate)
    }

    @Test
    fun showPickerWithChangedDate() {
        val date = yesterday().dayStart()

        createViewModel()
        viewModel.startDateChanged(date)
        viewModel.selectDate()
        val result = viewModel.showDatePickerStream.getResultValue()

        assertThat(result).isEqualTo(date)
    }

    @Test
    fun showErrorWhenSelectDateBeforeSettingsLoaded() {
        `when`(interactor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.selectDate()
        val result = viewModel.showDatePickerStream.getResultValue()

        assertThat(result).isNull()
        assertError(viewModel.errorStream, GetSettingsException::class)
    }

    private fun initSettings(budget: Double = 0.0, startDate: Date = Date(), period: Int = 0) {
        `when`(interactor.getSettings()).thenReturn(Single.just(PageSettings(budget, startDate, period)))
    }

    private fun initSettings(pageSettings: PageSettings) {
        `when`(interactor.getSettings()).thenReturn(Single.just(pageSettings))
    }

    private fun createViewModel() {
        viewModel = PageSettingsViewModelImpl(errorParser, interactor)
    }

    private fun yesterday(): Date = Date().apply { time -= DateUtils.DAY_IN_MILLIS }

    private fun assertError(errorStream: LiveData<String>, errorClass: KClass<out Exception>) {
        val captor = argumentCaptor<Exception>()
        val errorResult = errorStream.getResultValue()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(errorClass.java)
        assertThat(errorResult).isEqualTo(PARSED_ERROR)
    }
}