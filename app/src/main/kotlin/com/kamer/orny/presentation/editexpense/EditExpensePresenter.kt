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
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.util.*


@InjectViewState
class EditExpensePresenter(val errorParser: ErrorMessageParser,
                           val router: EditExpenseRouter,
                           val authorsInteractor: GetAuthorsInteractor,
                           val saveExpenseInteractor: SaveExpenseInteractor
) : MvpPresenter<EditExpenseView>(), EditExpenseViewModel {

    private val expense = Expense()
    private val newExpense = expense.copy()

    private val savingProgress = BehaviorSubject.create<Boolean>()
    private val authors = BehaviorSubject.create<List<Author>>()
    private val date = BehaviorSubject.createDefault<Date>(Date())
    private val showPicker = BehaviorSubject.create<Date>()

    override fun onFirstViewAttach() {
        loadAuthors()
    }

    override fun bindSavingProgress(): Observable<Boolean> = savingProgress

    override fun bindAuthors(): Observable<List<Author>> = authors

    override fun bindDate(): Observable<Date> = date

    override fun bindShowDatePicker(): Observable<Date> = showPicker

    override fun amountChanged(amountRaw: String) {
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

    override fun exitScreen() {
        when (newExpense) {
            expense -> router.closeScreen()
            else -> viewState.showExitDialog()
        }
    }

    override fun commentChanged(comment: String) {
        newExpense.comment = comment
    }

    override fun authorSelected(author: Author) {
        newExpense.author = author
    }

    override fun selectDate() {
        showPicker.onNext(newExpense.date)
    }

    override fun dateChanged(date: Date) {
        newExpense.date = date
    }

    override fun offBudgetChanged(isOffBudget: Boolean) {
        newExpense.isOffBudget = isOffBudget
    }

    override fun confirmExit() {
        router.closeScreen()
    }

    override fun saveExpense() {
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
                            authors.onNext(it)
                            expense.author = it.firstOrNull()
                        },
                        { viewState.showError(errorParser.getMessage(GetAuthorsException(it))) }
                )
    }

    private fun saveChanges() {
        saveExpenseInteractor
                .saveExpense(newExpense)
                .doOnSubscribe { savingProgress.onNext(true) }
                .doFinally { savingProgress.onNext(false) }
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