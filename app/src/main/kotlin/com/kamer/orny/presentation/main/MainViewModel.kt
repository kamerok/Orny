package com.kamer.orny.presentation.main

import android.arch.lifecycle.LiveData


interface MainViewModel {

    val updateProgressStream: LiveData<Boolean>

    fun addExpense()
    fun openSettings()

}