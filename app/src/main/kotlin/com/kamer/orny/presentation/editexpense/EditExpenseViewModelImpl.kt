package com.kamer.orny.presentation.editexpense

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.Expense
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.interaction.SaveExpenseInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.SingleLiveEvent
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.NoChangesException
import com.kamer.orny.presentation.editexpense.errors.SaveExpenseException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException
import timber.log.Timber
import java.util.*


class EditExpenseViewModelImpl(val errorParser: ErrorMessageParser,
                               val router: EditExpenseRouter,
                               val authorsInteractor: GetAuthorsInteractor,
                               val saveExpenseInteractor: SaveExpenseInteractor
) : BaseViewModel(), EditExpenseViewModel {

    private val expense = Expense()
    private val newExpense = expense.copy()

    private val authors = MutableLiveData<List<Author>>()
    private val date = MutableLiveData<Date>()
    private val savingProgress = MutableLiveData<Boolean>()
    private val showPicker = SingleLiveEvent<Date>()
    private val showExitDialog = SingleLiveEvent<Nothing>()
    private val showAmountError = SingleLiveEvent<String>()
    private val showError = SingleLiveEvent<String>()

    init {
        date.value = Date()
        loadAuthors()
    }

    override fun bindSavingProgress(): MutableLiveData<Boolean> = savingProgress

    override fun bindAuthors(): LiveData<List<Author>> = authors

    override fun bindDate(): LiveData<Date> = date

    override fun bindShowDatePicker(): SingleLiveEvent<Date> = showPicker

    override fun bindShowExitDialog(): SingleLiveEvent<Nothing> = showExitDialog

    override fun bindShowAmountError(): SingleLiveEvent<String> = showAmountError

    override fun bindShowError(): SingleLiveEvent<String> = showError

    override fun amountChanged(amountRaw: String) {
        if (amountRaw.isNullOrEmpty()) {
            newExpense.amount = 0.0
            return
        }
        val amount = amountRaw.toDoubleOrNull()
        when {
            amount == null ->
                showAmountError.value = errorParser.getMessage(WrongAmountFormatException("Can't parse"))
            amount < 0 ->
                showAmountError.value = errorParser.getMessage(WrongAmountFormatException("Amount can't be negative"))
            else -> newExpense.amount = amount
        }
    }

    override fun exitScreen() {
        when (newExpense) {
            expense -> router.closeScreen()
            else -> showExitDialog.call()
        }
    }

    override fun commentChanged(comment: String) {
        newExpense.comment = comment
    }

    override fun authorSelected(author: Author) {
        newExpense.author = author
    }

    override fun selectDate() {
        showPicker.value = newExpense.date
    }

    override fun dateChanged(date: Date) {
        newExpense.date = date
        this.date.value = date
    }

    override fun offBudgetChanged(isOffBudget: Boolean) {
        newExpense.isOffBudget = isOffBudget
    }

    override fun confirmExit() {
        router.closeScreen()
    }

    override fun saveExpense() {
        when (newExpense) {
            expense -> showError.value = errorParser.getMessage(NoChangesException())
            else -> saveChanges()
        }
    }

    private fun loadAuthors() {
        authorsInteractor
                .getAuthors()
                .disposeOnDestroy()
                .subscribe(
                        {
                            authors.value = it
                            expense.author = it.firstOrNull()
                        },
                        { showError.value = errorParser.getMessage(GetAuthorsException(it)) }
                )
    }

    private fun saveChanges() {
        saveExpenseInteractor
                .saveExpense(newExpense)
                .disposeOnDestroy()
                .doOnSubscribe { savingProgress.value = true }
                .doFinally { savingProgress.value = false }
                .subscribe(
                        { router.closeScreen() },
                        {
                            Timber.e(it.message, it)
                            showError.value = errorParser.getMessage(SaveExpenseException(it))
                        }
                )
    }

}