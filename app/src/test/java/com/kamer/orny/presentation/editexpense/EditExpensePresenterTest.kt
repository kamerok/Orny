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
class EditExpensePresenterTest {

    private val PARSED_ERROR = "parsed error"

    @Mock lateinit var errorParser: ErrorMessageParser
    @Mock lateinit var router: EditExpenseRouter
    @Mock lateinit var authorsInteractor: GetAuthorsInteractor
    @Mock lateinit var saveExpenseInteractor: SaveExpenseInteractor

    @Mock lateinit var view: EditExpenseView

    private lateinit var presenter: EditExpensePresenter

    @Before
    fun setUp() {
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(emptyList()))
        `when`(saveExpenseInteractor.saveExpense(any())).thenReturn(Completable.complete())

        presenter = EditExpensePresenter(errorParser, router, authorsInteractor, saveExpenseInteractor)
        presenter.attachedViews.add(view)
    }

    @Test
    fun setAuthorsOnStart() {
        val authors = listOf(
                Author(id = "0", name = "name1", color = "color1"),
                Author(id = "1", name = "name2", color = "color2"))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))
        val observer = TestObserver.create<List<Author>>()

        presenter.bindAuthors().subscribe(observer)
        presenter.attachView(view)

        observer.assertValues(authors)
    }

    @Test
    fun setCurrentDateOnStart() {
        val observer = TestObserver.create<Date>()

        presenter.bindDate().subscribe(observer)
        presenter.attachView(view)

        observer.assertValueCount(1)
        assertThat(observer.values().first()).isToday()
    }

    @Test
    fun showGetAuthorsError() {
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.error(Exception()))
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        presenter.bindShowError().subscribe(observer)
        presenter.attachView(view)

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(GetAuthorsException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNonDouble() {
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        presenter.bindShowAmountError().subscribe(observer)
        presenter.amountChanged("Wrong")

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNegative() {
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        presenter.bindShowAmountError().subscribe(observer)
        presenter.amountChanged("-1")

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun showDatePickerOnDateClicked() {
        val observer = TestObserver.create<Date>()
        val time = 199L

        presenter.bindShowDatePicker().subscribe(observer)
        presenter.dateChanged(Date(time))
        presenter.selectDate()

        observer.assertValueCount(1)
        assertThat(observer.values().first()).isEqualTo(Date(time))
    }

    @Test
    fun closeScreenOnExitIfNothingChanged() {
        presenter.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun firstAuthorNotCountAsChange() {
        val firstAuthor = Author(id = "0", name = "name1", color = "color1")
        val authors = listOf(
                firstAuthor,
                Author(id = "1", name = "name2", color = "color2"))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))

        presenter.attachView(view)
        presenter.authorSelected(firstAuthor)
        presenter.exitScreen()

        verify(router).closeScreen()
    }

    @Test
    fun showDialogWhenExitIfAmountChanged() {
        val observer = TestObserver.create<Any>()

        presenter.bindShowExitDialog().subscribe(observer)
        presenter.amountChanged("1")
        presenter.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfCommentChanged() {
        val observer = TestObserver.create<Any>()

        presenter.bindShowExitDialog().subscribe(observer)
        presenter.commentChanged("1")
        presenter.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfAuthorChanged() {
        val observer = TestObserver.create<Any>()

        presenter.bindShowExitDialog().subscribe(observer)
        presenter.authorSelected(Author("1", "", ""))
        presenter.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfDateChanged() {
        val observer = TestObserver.create<Any>()

        presenter.bindShowExitDialog().subscribe(observer)
        presenter.dateChanged(Date(100))
        presenter.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun showDialogWhenExitIfOffBudgetChanged() {
        val observer = TestObserver.create<Any>()

        presenter.bindShowExitDialog().subscribe(observer)
        presenter.offBudgetChanged(true)
        presenter.exitScreen()

        verify(router, never()).closeScreen()
        observer.assertValueCount(1)
    }

    @Test
    fun closeScreenOnExitConfirmed() {
        presenter.confirmExit()

        verify(router).closeScreen()
    }

    @Test
    fun showErrorOnSaveIfNothingChanged() {
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        presenter.bindShowError().subscribe(observer)
        presenter.saveExpense()

        verify(router, never()).closeScreen()
        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(NoChangesException::class.java)
        observer.assertValue(PARSED_ERROR)
    }

    @Test
    fun saveExpense() {
        presenter.amountChanged("1")
        presenter.saveExpense()

        verify(saveExpenseInteractor).saveExpense(any())
    }

    @Test
    fun showSaveExpenseProgress() {
        val observer = TestObserver.create<Boolean>()

        presenter.bindSavingProgress().subscribe(observer)
        presenter.amountChanged("1")
        presenter.saveExpense()

        observer.assertValues(true, false)
    }

    @Test
    fun closeScreenAfterSaving() {
        presenter.amountChanged("1")
        presenter.saveExpense()

        verify(router).closeScreen()
    }

    @Test
    fun showSavingError() {
        `when`(saveExpenseInteractor.saveExpense(any())).thenReturn(Completable.error(Exception()))
        val captor = argumentCaptor<Exception>()
        val observer = TestObserver.create<String>()

        presenter.bindShowError().subscribe(observer)
        presenter.amountChanged("1")
        presenter.saveExpense()

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(SaveExpenseException::class.java)
        observer.assertValue(PARSED_ERROR)
    }
}