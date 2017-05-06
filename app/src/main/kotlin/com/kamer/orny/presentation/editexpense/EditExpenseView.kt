package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.MvpView


interface EditExpenseView : MvpView {

    fun showError(message: String)

}