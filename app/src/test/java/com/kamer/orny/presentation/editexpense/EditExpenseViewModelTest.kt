package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.model.Author
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.NoChangesException
import com.kamer.orny.presentation.editexpense.errors.SaveExpenseException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import com.nhaarman.mockito_kotlin.never
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.observers.TestObserver
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
    @Mock lateinit var saveExpenseInteractor: SaveExpenseInteractor

    private lateinit var viewModel: EditExpenseViewModel

    @Before
    fun setUp() {
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(emptyList()))
        `when`(saveExpenseInteractor.saveExpense(any())).thenReturn(Completable.complete())

        createViewModel()
    }

    @Test
    fun setAuthorsOnStart() {
        val authors = listOf(
                Author(id = "0", name = "name1", color = "color1"),
                Author(id = "1", name = "name2", color = "color2"))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))
        val observer = TestObserver.create<List<Author>>()

        createViewModel()
        viewModel.bindAuthors().subscribe(observer)

        observer.assertValues(authors)
    }

    @Test
    fun setCurrentDateOnStart() {
        val observer = TestObserver.create<Date>()

        viewModel.bindDate().subscribe(observer)

        observer.assertValueCount(1)
        assertThat(observer.values().first()).isToday()
    }

    @Test
    fun showGetAuthorsError() {
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.error(Exception()))
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        createViewModel()
        viewModel.bindShowError().subscribe(observer)

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(GetAuthorsException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNonDouble() {
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        viewModel.bindShowAmountError().subscribe(observer)
        viewModel.amountChanged("Wrong")

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNegative() {
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        viewModel.bindShowAmountError().subscribe(observer)
        viewModel.amountChanged("-1")

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun showDatePickerOnDateClicked() {
        val observer = TestObserver.create<Date>()
        val time = 199L

        viewModel.bindShowDatePicker().subscribe(observer)
        viewModel.dateChanged(Date(time))
        viewModel.selectDate()

        observer.assertValueCount(1)
        assertThat(observer.values().first()).isEqualTo(Date(time))
    }

    @Test
    fun closeScreenOnExitIfNothingChanged() {
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun closeScreenOnExitWhenAmountSetAndDeleted() {
        val observer = TestObserver.create<String>()

        viewModel.bindShowAmountError().subscribe(observer)
        viewModel.amountChanged("1")
        viewModel.amountChanged("")
        viewModel.exitScreen()

        observer.assertNoValues()
        observer.assertNoErrors()
        verify(router).closeScreen()
    }

    @Test
    fun firstAuthorNotCountAsChange() {
        val firstAuthor = Author(id = "0", name = "name1", color = "color1")
        val authors = listOf(
                firstAuthor,
                Author(id = "1", name = "name2", color = "color2"))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))

        createViewModel()
        viewModel.authorSelected(firstAuthor)
        viewModel.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun showDialogWhenExitIfAmountChanged() {
        val observer = TestObserver.create<Any>()

        viewModel.bindShowExitDialog().subscribe(observer)
        viewModel.amountChanged("1")
        viewModel.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfCommentChanged() {
        val observer = TestObserver.create<Any>()

        viewModel.bindShowExitDialog().subscribe(observer)
        viewModel.commentChanged("1")
        viewModel.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfAuthorChanged() {
        val observer = TestObserver.create<Any>()

        viewModel.bindShowExitDialog().subscribe(observer)
        viewModel.authorSelected(Author("1", "", ""))
        viewModel.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfDateChanged() {
        val observer = TestObserver.create<Any>()

        viewModel.bindShowExitDialog().subscribe(observer)
        viewModel.dateChanged(Date(100))
        viewModel.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfOffBudgetChanged() {
        val observer = TestObserver.create<Any>()

        viewModel.bindShowExitDialog().subscribe(observer)
        viewModel.offBudgetChanged(true)
        viewModel.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun closeScreenOnExitConfirmed() {
        viewModel.confirmExit()

        verify(router).closeScreen()
    }

    @Test
    fun showErrorOnSaveIfNothingChanged() {
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        viewModel.bindShowError().subscribe(observer)
        viewModel.saveExpense()

        verify(router, never()).closeScreen()
        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(NoChangesException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun saveExpense() {
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        verify(saveExpenseInteractor).saveExpense(any())
    }

    @Test
    fun showSaveExpenseProgress() {
        val observer = TestObserver.create<Boolean>()

        viewModel.bindSavingProgress().subscribe(observer)
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        observer.assertValues(true, false)
    }

    @Test
    fun closeScreenAfterSaving() {
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        verify(router).closeScreen()
    }

    @Test
    fun showSavingError() {
        `when`(saveExpenseInteractor.saveExpense(any())).thenReturn(Completable.error(Exception()))
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        viewModel.bindShowError().subscribe(observer)
        viewModel.amountChanged("1")
        viewModel.saveExpense()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(SaveExpenseException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    private fun createViewModel() {
        viewModel = EditExpenseViewModelImpl(errorParser, router, authorsInteractor, saveExpenseInteractor)
    }
}