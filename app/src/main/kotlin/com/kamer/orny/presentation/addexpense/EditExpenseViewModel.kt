package com.kamer.orny.presentation.addexpense

import android.arch.lifecycle.LiveData
import com.kamer.orny.data.domain.model.Author
import com.kamer.orny.presentation.core.SingleLiveEvent
import java.util.*


interface EditExpenseViewModel {

    val authorsStream: LiveData<List<Author>>
    val dateStream: LiveData<Date>
    val savingProgressStream: LiveData<Boolean>
    val showDatePickerStream: SingleLiveEvent<Date>
    val showExitDialogStream: SingleLiveEvent<Nothing>
    val showAmountErrorStream: SingleLiveEvent<String>
    val showErrorStream: SingleLiveEvent<String>

    fun amountChanged(amountRaw: String)
    fun commentChanged(comment: String)
    fun authorSelected(author: Author)
    fun dateChanged(date: Date)
    fun offBudgetChanged(isOffBudget: Boolean)
    fun selectDate()
    fun exitScreen()
    fun confirmExit()
    fun saveExpense()

}