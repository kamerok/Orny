package com.kamer.orny.presentation.editexpense

import com.kamer.orny.data.model.Author
import io.reactivex.Observable


interface EditExpenseViewModel {

    fun getAuthors(): Observable<List<Author>>

    fun getSavingProgress(): Observable<Boolean>

}