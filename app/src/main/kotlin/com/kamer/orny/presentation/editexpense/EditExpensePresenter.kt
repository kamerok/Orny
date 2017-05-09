package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException


@InjectViewState
class EditExpensePresenter(val errorParser: ErrorMessageParser,
                           val editExpenseRouter: EditExpenseRouter,
                           val authorsInteractor: GetAuthorsInteractor,
                           val saveExpenseInteractor: SaveExpenseInteractor
) : MvpPresenter<EditExpenseView>() {

    override fun onFirstViewAttach() {
        authorsInteractor
                .getAuthors()
                .subscribe(
                        { viewState.setAuthors(it) },
                        { viewState.showError(errorParser.getMessage(GetAuthorsException(it))) })
    }

    fun amountChanged(amountRaw: String) {
        try {
            parseAmount(amountRaw)
        } catch(e: NumberFormatException) {
            viewState.showAmountError(errorParser.getMessage(WrongAmountFormatException(e)))
        }
    }

    private fun parseAmount(amountRaw: String) {
        val amount = amountRaw.toDouble()
        if (amount < 0) {
            viewState.showAmountError(errorParser.getMessage(
                    WrongAmountFormatException(Exception("Amount can't be negative"))))
        }
    }

}