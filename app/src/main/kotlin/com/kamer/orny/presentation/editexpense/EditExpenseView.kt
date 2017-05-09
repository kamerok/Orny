package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.MvpView
import com.kamer.orny.data.model.Author


interface EditExpenseView : MvpView {

    fun setAuthors(authors: List<Author>)

    fun showAmountError(error: String)
    fun showError(message: String)

}