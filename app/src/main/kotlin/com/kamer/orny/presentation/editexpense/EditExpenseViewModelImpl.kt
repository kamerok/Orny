package com.kamer.orny.presentation.editexpense

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.data.domain.model.NewExpense
import com.kamer.orny.interaction.CreateExpenseInteractor
import com.kamer.orny.interaction.GetAuthorsInteractor
import com.kamer.orny.presentation.core.BaseViewModel
import com.kamer.orny.presentation.core.ErrorMessageParser
import com.kamer.orny.presentation.core.SingleLiveEvent
import com.kamer.orny.presentation.editexpense.errors.GetAuthorsException
import com.kamer.orny.presentation.editexpense.errors.NoChangesException
import com.kamer.orny.presentation.editexpense.errors.SaveExpenseException
import com.kamer.orny.presentation.editexpense.errors.WrongAmountFormatException
import io.reactivex.Single
import timber.log.Timber
import java.util.*
import javax.inject.Inject


class EditExpenseViewModelImpl @Inject constructor(
        val errorParser: ErrorMessageParser,
        val router: EditExpenseRouter,
        val authorsInteractor: GetAuthorsInteractor,
        val createExpenseInteractor: CreateExpenseInteractor
) : BaseViewModel(), EditExpenseViewModel {

    private var authors = emptyList<Author>()

    private var comment: String = ""
    private var date = Date()
    private var isOffBudget = false
    private var amount = 0.0
    private var author: Author? = null

    private val authorsStream = MutableLiveData<List<Author>>()
    private val dateStream = MutableLiveData<Date>()
    private val savingProgressStream = MutableLiveData<Boolean>()
    private val showPickerStream = SingleLiveEvent<Date>()
    private val showExitDialogStream = SingleLiveEvent<Nothing>()
    private val showAmountErrorStream = SingleLiveEvent<String>()
    private val showErrorStream = SingleLiveEvent<String>()

    init {
        dateStream.value = date
        loadAuthors()
    }

    override fun bindSavingProgress(): MutableLiveData<Boolean> = savingProgressStream

    override fun bindAuthors(): LiveData<List<Author>> = authorsStream

    override fun bindDate(): LiveData<Date> = dateStream

    override fun bindShowDatePicker(): SingleLiveEvent<Date> = showPickerStream

    override fun bindShowExitDialog(): SingleLiveEvent<Nothing> = showExitDialogStream

    override fun bindShowAmountError(): SingleLiveEvent<String> = showAmountErrorStream

    override fun bindShowError(): SingleLiveEvent<String> = showErrorStream

    override fun amountChanged(amountRaw: String) {
        if (amountRaw.isNullOrEmpty()) {
            amount = 0.0
            return
        }
        val newAmount = amountRaw.toDoubleOrNull()
        when {
            newAmount == null ->
                showAmountErrorStream.value = errorParser.getMessage(WrongAmountFormatException("Can't parse"))
            newAmount < 0 ->
                showAmountErrorStream.value = errorParser.getMessage(WrongAmountFormatException("Amount can't be negative"))
            else -> amount = newAmount
        }
    }

    override fun exitScreen() {
        if (isSomethingToSave()) {
            showExitDialogStream.call()
        } else {
            router.closeScreen()
        }
    }

    override fun commentChanged(comment: String) {
        this.comment = comment
    }

    override fun authorSelected(author: Author) {
        this.author = author
    }

    override fun selectDate() {
        showPickerStream.value = date
    }

    override fun dateChanged(date: Date) {
        this.date = date
        this.dateStream.value = date
    }

    override fun offBudgetChanged(isOffBudget: Boolean) {
        this.isOffBudget = isOffBudget
    }

    override fun confirmExit() {
        router.closeScreen()
    }

    override fun saveExpense() {
        if (isSomethingToSave()) {
            saveChanges()
        } else {
            showErrorStream.value = errorParser.getMessage(NoChangesException())
        }
    }

    private fun loadAuthors() {
        authorsInteractor
                .getAuthors()
                .disposeOnDestroy()
                .subscribe(
                        {
                            authorsStream.value = it
                            author = it.firstOrNull()
                            authors = it
                        },
                        { showErrorStream.value = errorParser.getMessage(GetAuthorsException(it)) }
                )
    }

    private fun saveChanges() {
        Single
                .fromCallable {
                    val author = author
                    if (author == null) {
                        throw Exception("No author selected")
                    } else {
                        NewExpense(comment, date, isOffBudget, amount, author)
                    }
                }
                .flatMapCompletable { createExpenseInteractor.createExpense(it) }
                .disposeOnDestroy()
                .doOnSubscribe { savingProgressStream.value = true }
                .doFinally { savingProgressStream.value = false }
                .subscribe(
                        { router.closeScreen() },
                        {
                            Timber.e(it.message, it)
                            showErrorStream.value = errorParser.getMessage(SaveExpenseException(it))
                        }
                )
    }

    private fun isSomethingToSave(): Boolean = amount != 0.0 || comment.isNotEmpty()

}