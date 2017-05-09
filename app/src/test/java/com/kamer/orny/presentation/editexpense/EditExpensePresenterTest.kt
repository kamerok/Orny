package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.model.Author
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.argumentCaptor
import io.reactivex.Single
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class EditExpensePresenterTest {

    private val PARSED_ERROR = "parsed error"

    @Mock lateinit var errorParser: ErrorMessageParser
    @Mock lateinit var editExpenseRouter: EditExpenseRouter
    @Mock lateinit var authorsInteractor: GetAuthorsInteractor
    @Mock lateinit var saveExpenseInteractor: SaveExpenseInteractor

    @Mock lateinit var view: EditExpenseView

    private lateinit var presenter: EditExpensePresenter

    @Before
    fun setUp() {
        `when`(errorParser.getMessage(any())).thenReturn(PARSED_ERROR)
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(emptyList()))
//        `when`(saveExpenseInteractor.saveExpense(any())).thenReturn(Completable.complete())

        presenter = EditExpensePresenter(errorParser, editExpenseRouter, authorsInteractor, saveExpenseInteractor)
        presenter.attachedViews.add(view)
    }

    @Test
    fun setAuthorsOnStart() {
        val authors = listOf(
                Author(id = "0", name = "name1", color = "color1"),
                Author(id = "1", name = "name2", color = "color2"))
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.just(authors))

        presenter.attachView(view)

        verify(view).setAuthors(authors)
    }

    @Test
    fun showGetAuthorsError() {
        `when`(authorsInteractor.getAuthors()).thenReturn(Single.error(Exception()))
        val captor = argumentCaptor<Exception>()

        presenter.attachView(view)

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(GetAuthorsException::class.java)
        verify(view).showError(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNonDouble() {
        val captor = argumentCaptor<Exception>()

        presenter.amountChanged("Wrong")

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        verify(view).showAmountError(PARSED_ERROR)
    }

    @Test
    fun showWrongAmountFormatErrorOnNegative() {
        val captor = argumentCaptor<Exception>()

        presenter.amountChanged("-1")

        verify(errorParser).getMessage(captor.capture())
        assertThat(captor.firstValue).isInstanceOf(WrongAmountFormatException::class.java)
        verify(view).showAmountError(PARSED_ERROR)
    }
}