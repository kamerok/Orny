package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.model.Author
import io.reactivex.Observable
import java.util.*


interface EditExpenseViewModel {

    fun getAuthors(): Observable<List<Author>>

    fun getDate(): Observable<Date>

    fun getSavingProgress(): Observable<Boolean>

}