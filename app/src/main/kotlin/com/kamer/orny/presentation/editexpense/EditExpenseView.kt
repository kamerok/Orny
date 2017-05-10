package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.kamer.orny.data.model.Author
import java.util.*


interface EditExpenseView : MvpView {

    fun setAuthors(authors: List<Author>)

    fun setDate(capture: Date)

    fun setSavingProgress(isSaving: Boolean)

    @StateStrategyType(SkipStrategy::class)
    fun showExitDialog()

    @StateStrategyType(SkipStrategy::class)
    fun showAmountError(error: String)

    @StateStrategyType(SkipStrategy::class)
    fun showError(message: String)

}