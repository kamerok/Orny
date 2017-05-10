package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.MvpView
import com.kamer.orny.data.model.Author
import java.util.*


interface EditExpenseView : MvpView {

    fun setAuthors(authors: List<Author>)
    fun setDate(capture: Date)

    fun setSavingProgress(isSaving: Boolean)

    fun showExitDialog()
    fun showAmountError(error: String)
    fun showError(message: String)

}