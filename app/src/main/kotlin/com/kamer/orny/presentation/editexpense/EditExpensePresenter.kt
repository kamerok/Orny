package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.kamer.orny.data.model.Author
import com.kamer.orny.data.model.Expense
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.NoChangesException
import com.kamer.orny.presentation.editexpense.errors.SaveExpenseException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException
import timber.log.Timber
import java.util.*


@InjectViewState
class EditExpensePresenter(val errorParser: ErrorMessageParser,
                           val router: EditExpenseRouter,
                           val authorsInteractor: GetAuthorsInteractor,
                           val saveExpenseInteractor: SaveExpenseInteractor
) : MvpPresenter<EditExpenseView>() {

    private val expense = Expense()
    private val newExpense = expense.copy()

    override fun onFirstViewAttach() {
        loadAuthors()
        viewState.setDate(Date())
    }

    fun amountChanged(amountRaw: String) {
        try {
            val amount = amountRaw.toDouble()
            if (amount < 0) {
                viewState.showAmountError(errorParser.getMessage(
                        WrongAmountFormatException(Exception("Amount can't be negative"))))
            } else {
                newExpense.amount = amount
            }
        } catch(e: NumberFormatException) {
            viewState.showAmountError(errorParser.getMessage(WrongAmountFormatException(e)))
        }
    }

    fun exitScreen() {
        when (newExpense) {
            expense -> router.closeScreen()
            else -> viewState.showExitDialog()
        }
    }

    fun commentChanged(comment: String) {
        newExpense.comment = comment
    }

    fun authorSelected(author: Author) {
        newExpense.author = author
    }

    fun selectDate() {
        viewState.showDatePicker(newExpense.date)
    }

    fun dateChanged(date: Date) {
        newExpense.date = date
    }

    fun offBudgetChanged(isOffBudget: Boolean) {
        newExpense.isOffBudget = isOffBudget
    }

    fun confirmExit() {
        router.closeScreen()
    }

    fun saveExpense() {
        when (newExpense) {
            expense -> viewState.showError(errorParser.getMessage(NoChangesException()))
            else -> saveChanges()
        }
    }

    private fun loadAuthors() {
        authorsInteractor
                .getAuthors()
                .subscribe(
                        {
                            viewState.setAuthors(it)
                            expense.author = it.firstOrNull()
                        },
                        { viewState.showError(errorParser.getMessage(GetAuthorsException(it))) }
                )
    }

    private fun saveChanges() {
        saveExpenseInteractor
                .saveExpense(newExpense)
                .doOnSubscribe { viewState.setSavingProgress(true) }
                .doFinally { viewState.setSavingProgress(false) }
                .subscribe(
                        { router.closeScreen() },
                        {
                            if (it is UserRecoverableAuthIOException) {
                                viewState.startIntent(it.intent)
                            } else {
                                Timber.e(it.message, it)
                                viewState.showError(errorParser.getMessage(SaveExpenseException(it)))
                            }}
                )
    }

}