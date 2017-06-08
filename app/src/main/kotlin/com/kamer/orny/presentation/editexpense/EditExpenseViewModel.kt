package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.model.Author
import io.reactivex.Observable
import java.util.*


interface EditExpenseViewModel {

    fun bindAuthors(): Observable<List<Author>>
    fun bindDate(): Observable<Date>
    fun bindSavingProgress(): Observable<Boolean>
    fun bindShowDatePicker(): Observable<Date>

    fun amountChanged(amountRaw: String)
    fun exitScreen()
    fun commentChanged(comment: String)
    fun authorSelected(author: Author)
    fun selectDate()
    fun dateChanged(date: Date)
    fun offBudgetChanged(isOffBudget: Boolean)
    fun confirmExit()
    fun saveExpense()

}