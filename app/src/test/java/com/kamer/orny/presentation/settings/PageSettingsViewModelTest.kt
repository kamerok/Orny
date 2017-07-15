package com.kamer.orny.presentation.settings

import android.arch.lifecycle.LiveData
import android.text.format.DateUtils
import com.kamer.orny.data.domain.model.PageSettings
import com.kamer.orny.interaction.GetPageSettingsInteractor
import com.kamer.orny.interaction.SavePageSettingsInteractor
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
    @Mock lateinit var getInteractor: GetPageSettingsInteractor
    @Mock lateinit var saveInteractor: SavePageSettingsInteractor

    private lateinit var viewModel: PageSettingsViewModel

    @Before
    fun setUp() {
        TestUtils.setupLiveDataExecutor()
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        initSettings()
        `when`(saveInteractor.saveSettings(any())).thenReturn(Completable.complete())
    }

    @Test
    fun loadSettingsFromInteractor() {
        createViewModel()

        verify(getInteractor).getSettings()
    }

    @Test
    fun loadSettingsOnStart() {
        val pageSettings = PageSettings(0.0, Date().dayStart(), 0)
        initSettings(pageSettings)

        createViewModel()
        val result = viewModel.bindPageSettings().getResultValue()

        assertThat(result).isEqualTo(pageSettings)
    }

    @Test
    fun showProgressOnStart() {
        val subject = PublishSubject.create<PageSettings>()
        `when`(getInteractor.getSettings()).thenReturn(subject.firstOrError())

        createViewModel()
        val results = viewModel.bindLoadingProgress().getResultValues(2) { subject.onNext(PageSettings(0.0, Date(), 0)) }

        assertThat(results).containsExactly(true, false)
    }

    @Test
    fun showProgressOnStartWithError() {
        val subject = PublishSubject.create<PageSettings>()
        `when`(getInteractor.getSettings()).thenReturn(subject.firstOrError())

        createViewModel()
        val results = viewModel.bindLoadingProgress().getResultValues(2) { subject.onError(Exception()) }

        assertThat(results).containsExactly(true, false)
    }

    @Test
    fun loadSettingsError() {
        `when`(getInteractor.getSettings()).thenReturn(Single.error(Exception()))

        createViewModel()
        val result = viewModel.bindPageSettings().getResultValue()

        assertThat(result).isNull()
        assertError(viewModel.bindError(), GetSettingsException::class)

    }

    @Test
    fun saveDisabledByDefault() {
        createViewModel()
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun enableSaveWhenBudgetChanged() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("2")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isTrue()
    }

    @Test
    fun disableSaveWhenBudgetChangedOnSameValue() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("2")
        viewModel.budgetChanged("0")

        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenBudgetChangedBeforeSettingsLoaded() {
        `when`(getInteractor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.budgetChanged("2")

        assertError(viewModel.bindError(), GetSettingsException::class)
    }

    @Test
    fun notEnableSaveButtonWhenBudgetNotNumber() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("ad")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenBudgetNotNumber() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("ad")

        assertError(viewModel.bindError(), WrongBudgetFormatException::class)
    }

    @Test
    fun notEnableSaveButtonWhenBudgetNegative() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("-2")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenBudgetNegative() {
        initSettings(budget = 0.0)

        createViewModel()
        viewModel.budgetChanged("-2")

        assertError(viewModel.bindError(), WrongBudgetFormatException::class)
    }

    @Test
    fun enableSaveWhenStartDateChanged() {
        initSettings(startDate = Date())

        createViewModel()
        viewModel.startDateChanged(yesterday())
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isTrue()
    }

    @Test
    fun disableSaveWhenStartDateChangedOnSameValue() {
        initSettings(startDate = Date())

        createViewModel()
        viewModel.startDateChanged(yesterday())
        viewModel.startDateChanged(Date())
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenStartDateChangedBeforeSettingsLoaded() {
        `when`(getInteractor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.startDateChanged(yesterday())

        assertError(viewModel.bindError(), GetSettingsException::class)
    }

    @Test
    fun enableSaveWhenPeriodChanged() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("2")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isTrue()
    }

    @Test
    fun disableSaveWhenPeriodChangedOnSameValue() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("2")
        viewModel.periodChanged("0")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenPeriodChangedBeforeSettingsLoaded() {
        `when`(getInteractor.getSettings()).thenReturn(Single.never())

        createViewModel()
        viewModel.periodChanged("1")

        assertError(viewModel.bindError(), GetSettingsException::class)
    }

    @Test
    fun notEnableSaveButtonWhenPeriodNotNumber() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("ad")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenPeriodNotNumber() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("ad")

        assertError(viewModel.bindError(), WrongPeriodFormatException::class)
    }

    @Test
    fun notEnableSaveButtonWhenPeriodNegative() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("-1")
        val result = viewModel.bindSaveButtonEnabled().getResultValue()

        assertThat(result).isFalse()
    }

    @Test
    fun showErrorWhenPeriodNegative() {
        initSettings(period = 0)

        createViewModel()
        viewModel.periodChanged("-1")

        assertError(viewModel.bindError(), WrongPeriodFormatException::class)
    }

    @Test
    fun callInteractorOnSave() {
        val captor = argumentCaptor<PageSettings>()

        createViewModel()
        viewModel.budgetChanged("1")
        viewModel.saveSettings()

        verify(saveInteractor).saveSettings(captor.capture())
        assertThat(captor.firstValue).isEqualTo(PageSettings(1.0, Date().dayStart(), 0))
    }

    @Test
    fun showSavingProgress() {
        createViewModel()
        viewModel.periodChanged("1")
        val results = viewModel.bindSavingProgress().getResultValues(2) { viewModel.saveSettings() }

        assertThat(results).containsExactly(true, false)
    }

    @Test
    fun showSavingProgressError() {
        `when`(saveInteractor.saveSettings(any())).thenReturn(Completable.error(Exception()))

        createViewModel()
        viewModel.periodChanged("1")
        viewModel.saveSettings()

        assertError(viewModel.bindError(), SaveSettingsException::class)
    }

    private fun initSettings(budget: Double = 0.0, startDate: Date = Date(), period: Int = 0) {
        `when`(getInteractor.getSettings()).thenReturn(Single.just(PageSettings(budget, startDate, period)))
    }

    private fun initSettings(pageSettings: PageSettings) {
        `when`(getInteractor.getSettings()).thenReturn(Single.just(pageSettings))
    }

    private fun createViewModel() {
        viewModel = PageSettingsViewModelImpl(errorParser, getInteractor, saveInteractor)
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