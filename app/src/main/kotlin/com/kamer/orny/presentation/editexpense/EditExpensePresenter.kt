package com.kamer.orny.presentation.editexpense

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.kamer.orny.presentation.core.ErrorMessageParser


@InjectViewState
class EditExpensePresenter(val errorParser: ErrorMessageParser) : MvpPresenter<EditExpenseView>() {

    override fun onFirstViewAttach() {
        viewState.showError(errorParser.getMessage(Exception("Test")))
    }

}