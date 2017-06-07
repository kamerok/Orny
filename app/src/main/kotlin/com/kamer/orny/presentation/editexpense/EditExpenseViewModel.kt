package com.kamer.orny.presentation.editexpense

import io.reactivex.Observable


interface EditExpenseViewModel {

    fun getSavingProgress(): Observable<Boolean>

}