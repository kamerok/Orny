package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.interaction.CreateExpenseInteractor
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.NoChangesException
import com.kamer.orny.presentation.editexpense.errors.SaveExpenseException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException
import com.kamer.orny.utils.TestUtils
import com.kamer.orny.utils.getResultValue
import com.kamer.orny.utils.getResultValues
import com.kamer.orny.utils.hasValue
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.never
import io.reactivex.Completable
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class EditExpenseViewModelTest {

    private val PARSED_ERROR = "parsed error"

    @Mock lateinit var errorParser: ErrorMessageParser
    @Mock lateinit var router: EditExpenseRouter
    @Mock lateinit var authorsInteractor: GetAuthorsInteractor
    @Mock lateinit var createExpenseInteractor: CreateExpenseInteractor

    private lateinit var viewModel: EditExpenseViewModel

    @Before
    fun setUp() {
        TestUtils.setupLiveDataExecutor()
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(listOf(createAuthor(0))))
        `when`(createExpenseInteractor.createExpense(any())).thenReturn(Completable.complete())

        createViewModel()
    }

    @Test
    fun setAuthorsOnStart() {
        val authors = listOf(createAuthor(0), createAuthor(1))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))

        createViewModel()
        val result = viewModel.bindAuthors().getResultValue()

        assertThat(result).isEqualTo(authors)
    }

    @Test
    fun setCurrentDateOnStart() {
        val result = viewModel.bindDate().getResultValue()

        assertThat(result).isToday()
    }

    @Test
    fun setDateWhenDateChanged() {
        val date = Date(10000)
        viewModel.dateChanged(date)
        val result = viewModel.bindDate().getResultValue()

        assertThat(result).isInSameDayAs(date)
    }

    @Test
    fun showGetAuthorsError() {
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.error(Exception()))
        val captor = argumentCaptor<Exception>()

        createViewModel()
        val result = viewModel.bindShowError().getResultValue()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(GetAuthorsException::class.java)
        assertThat(result).isEqualTo(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNonDouble() {
        val captor = argumentCaptor<Exception>()

        viewModel.amountChanged("Wrong")
        val result = viewModel.bindShowAmountError().getResultValue()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        assertThat(result).isEqualTo(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNegative() {
        val captor = argumentCaptor<Exception>()

        viewModel.amountChanged("-1")
        val result = viewModel.bindShowAmountError().getResultValue()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        assertThat(result).isEqualTo(PARSED_ERROR)
    }

    @Test
    fun showDatePickerOnDateClicked() {
        val time = 199L

        viewModel.dateChanged(Date(time))
        viewModel.selectDate()
        val result = viewModel.bindShowDatePicker().getResultValue()

        assertThat(result).isEqualTo(Date(time))
    }

    @Test
    fun closeScreenOnExitIfNothingChanged() {
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun closeScreenOnExitWhenAmountSetAndDeleted() {
        viewModel.amountChanged("1")
        viewModel.amountChanged("")
        viewModel.exitScreen()
        val result = viewModel.bindShowAmountError().getResultValue()

        assertThat(result).isNull()
        verify(router).closeScreen()
    }

    @Test
    fun firstAuthorNotCountAsChange() {
        val firstAuthor = createAuthor(0)
        val authors = listOf(firstAuthor, createAuthor(1))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))

        createViewModel()
        viewModel.authorSelected(firstAuthor)
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun showDialogWhenExitIfAmountChanged() {
        viewModel.amountChanged("1")
        viewModel.exitScreen()
        val result = viewModel.bindShowExitDialog().hasValue()

        verify(router, never()).closeScreen()
        assertThat(result).isTrue()
    }

    @Test
    fun showDialogWhenExitIfCommentChanged() {
        viewModel.commentChanged("1")
        viewModel.exitScreen()
        val result = viewModel.bindShowExitDialog().hasValue()

        verify(router, never()).closeScreen()
        assertThat(result).isTrue()
    }

    @Test
    fun closeScreenWhenExitIfAuthorChanged() {
        viewModel.authorSelected(createAuthor(0))
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun closeScreenWhenExitIfDateChanged() {
        viewModel.dateChanged(Date(100))
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun closeScreenWhenExitIfOffBudgetChanged() {
        viewModel.offBudgetChanged(true)
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun closeScreenOnExitConfirmed() {
        viewModel.confirmExit()

        verify(router).closeScreen()
    }

    @Test
    fun showErrorOnSaveIfNothingChanged() {
        val captor = argumentCaptor<Exception>()

        viewModel.saveExpense()
        val result = viewModel.bindShowError().getResultValue()

        verify(router, never()).closeScreen()
        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(NoChangesException::class.java)
        assertThat(result).isEqualTo(PARSED_ERROR)
    }

    @Test
    fun saveExpense() {
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        verify(createExpenseInteractor).createExpense(any())
    }

    @Test
    fun showSaveExpenseProgress() {
        val progress = viewModel.bindSavingProgress().getResultValues(2)
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        assertThat(progress[0]).isTrue()
        assertThat(progress[1]).isFalse()
    }

    @Test
    fun closeScreenAfterSaving() {
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        verify(router).closeScreen()
    }

    @Test
    fun showSavingError() {
        `when`(createExpenseInteractor.createExpense(any())).thenReturn(Completable.error(Exception()))
        val captor = argumentCaptor<Exception>()

        viewModel.amountChanged("1")
        viewModel.saveExpense()
        val result = viewModel.bindShowError().getResultValue()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(SaveExpenseException::class.java)
        assertThat(result).isEqualTo(PARSED_ERROR)
    }

    private fun createViewModel() {
        viewModel = EditExpenseViewModelImpl(errorParser, router, authorsInteractor, createExpenseInteractor)
    }

    private fun createAuthor(index: Int) = Author(id = "$index", position = index, name = "name$index", color = "color$index")
}